import { Driver } from 'neo4j-driver'
import { Pool } from 'pg'
import { NextApiResponse } from 'next'

// Type definition for response object
interface ResponseObj {
  title: string
  link: string
}

// Database connections
let neo4jDriver: Driver
let pgPool: Pool 

// Initialize Neo4j connection
const getNeo4jDriver = () => {
  if (!neo4jDriver) {
    neo4jDriver = require('neo4j-driver').driver(
      process.env.NEO4J_URI!,
      require('neo4j-driver').auth.basic(
        process.env.NEO4J_USERNAME!,
        process.env.NEO4J_PASSWORD!
      )
    )
  }
  return neo4jDriver
}

// Initialize Postgres connection
const getPgPool = () => {
  if (!pgPool) {
    pgPool = new Pool({
      user: process.env.POSTGRES_USER,
      host: process.env.POSTGRES_HOST,
      database: process.env.POSTGRES_DATABASE,
      password: process.env.POSTGRES_PASSWORD,
      port: parseInt(process.env.POSTGRES_PORT || '5432'),
    })
  }
  return pgPool
}

export async function getResults(query: string): Promise<ResponseObj[]> {
  try {
    const driver = getNeo4jDriver()
    const pool = getPgPool()

    // Clean and prepare the query
    const cleanQuery = query
      .toLowerCase()
      .replace(/[^a-z0-9]/g, ' ')
      .split(/\s+/)
      .filter(word => word.length > 0)

    // Step 1: Query Neo4j for relevant site IDs
    const session = driver.session()
    const neo4jQuery = `
      UNWIND $words as word
      MATCH (w:Word {text: word})-[r:FOUND_IN]->(s:Site)
      WITH s, sum(r.weight) as score
      ORDER BY score DESC
      LIMIT 15
      RETURN s.id, score
    `

    const neo4jResult = await session.executeWrite(tx =>
      tx.run(neo4jQuery, { words: cleanQuery })
    )
    
    const siteIds = neo4jResult.records.map(record => record.get('s.id').toNumber())
    await session.close()

    if (siteIds.length === 0) {
      return []
    }

    // Step 2: Query PostgreSQL for site details
    const pgQuery = `
      SELECT link, rawtitle as title, description 
      FROM public."Sites"
      WHERE id = ANY($1)
    `

    const pgResult = await pool.query(pgQuery, [siteIds])
    
    // Transform the results into ResponseObj array
    const searchResults: ResponseObj[] = pgResult.rows.map(row => ({
      title: row.title,
      link: row.link
    }))

    return searchResults

  } catch (error) {
    console.error('Search error:', error)
    throw error
  }
}

// Example API route handler
export async function searchAPI(req: any, res: NextApiResponse) {
  try {
    const { query } = req.query
    
    if (!query || typeof query !== 'string') {
      return res.status(400).json({ error: 'Invalid query parameter' })
    }

    const results = await getResults(query)
    return res.status(200).json(results)

  } catch (error) {
    console.error('API error:', error)
    return res.status(500).json({ error: 'Internal server error' })
  }
}
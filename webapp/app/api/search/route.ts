import { NextResponse } from "next/server"
import { getResults } from "@/lib/queryHelpers"

export async function GET(request: Request) {
  const { searchParams } = new URL(request.url)
  const query = searchParams.get("q")

  if (!query) {
    return NextResponse.json({ error: "Query parameter is required" }, { status: 400 })
  }

  try {
    const results = await getResults(query)
    return NextResponse.json(results)
  } catch (error) {
    console.error("Error processing search query:", error)
    return NextResponse.json({ error: "Internal server error" }, { status: 500 })
  }
}


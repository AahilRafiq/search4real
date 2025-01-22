"use client"

import { useState } from "react"
import { useRouter } from "next/navigation"
import { Github, Search, Loader2 } from "lucide-react"
import { motion, AnimatePresence } from "framer-motion"

export default function SearchComponent() {
  const [query, setQuery] = useState("")
  const [results, setResults] = useState([])
  const [isLoading, setIsLoading] = useState(false)
  const router = useRouter()

  const handleSearch = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    try {
      const response = await fetch(`/api/search?q=${encodeURIComponent(query)}`)
      const data = await response.json()
      setResults(data)
    } catch (error) {
      console.error("Error fetching search results:", error)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div className="w-full max-w-3xl bg-white rounded-lg shadow-xl p-4 sm:p-8">
      <div className="flex justify-between items-center mb-6 sm:mb-8">
        <h1 className="text-3xl sm:text-4xl font-bold text-blue-600">Search4Real</h1>
        <a
          href="https://github.com/AahilRafiq/search4real"
          target="_blank"
          rel="noopener noreferrer"
          className="text-gray-600 hover:text-blue-600 transition-colors"
        >
          <Github className="w-6 h-6 sm:w-8 sm:h-8" />
        </a>
      </div>
      <form onSubmit={handleSearch} className="mb-6 sm:mb-8">
        <div className="flex items-center bg-gray-100 rounded-full overflow-hidden shadow-inner">
          <input
            type="text"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Enter your search query"
            className="flex-grow px-4 sm:px-6 py-3 sm:py-4 bg-transparent focus:outline-none text-base sm:text-lg"
            disabled={isLoading}
          />
          <button
            type="submit"
            disabled={isLoading}
            className="px-4 sm:px-6 py-3 sm:py-4 bg-blue-500 text-white hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-opacity-50 transition-colors disabled:bg-blue-400"
          >
            {isLoading ? <Loader2 className="w-5 h-5 sm:w-6 sm:h-6 animate-spin" /> : <Search className="w-5 h-5 sm:w-6 sm:h-6" />}
          </button>
        </div>
      </form>

      {/* Loading State */}
      <AnimatePresence>
        {isLoading && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            className="flex flex-col items-center justify-center py-8"
          >
            <Loader2 className="w-8 h-8 sm:w-12 sm:h-12 animate-spin text-blue-500 mb-4" />
            <p className="text-gray-600 text-sm sm:text-base">Searching...</p>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Results */}
      <AnimatePresence>
        {!isLoading && results.length > 0 && (
          <motion.ul
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 20 }}
            className="space-y-4 sm:space-y-6"
          >
            {results.map((result: { link: string; title: string }, index: number) => (
              <motion.li
                key={index}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.1 }}
                className="border-b pb-3 sm:pb-4"
              >
                <a
                  href={result.link}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="block hover:bg-gray-50 rounded-lg p-3 sm:p-4 transition-colors"
                >
                  <h2 className="text-lg sm:text-xl font-semibold text-blue-600 mb-2">{result.title}</h2>
                  <p className="text-xs sm:text-sm text-gray-600">{result.link}</p>
                </a>
              </motion.li>
            ))}
          </motion.ul>
        )}
      </AnimatePresence>
    </div>
  )
}
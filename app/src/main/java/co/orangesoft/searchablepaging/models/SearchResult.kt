package co.orangesoft.searchablepaging.models

data class SearchResult(val total_count: Int, val incomplete_results: Boolean, val items: List<User>)

// core\src\commonMain\kotlin\com\x3squaredcircles\core\models\PagedList.kt
package com.x3squaredcircles.core.models
import kotlin.math.ceil
data class PagedList<T>(
val items: List<T>,
val pageNumber: Int,
val pageSize: Int,
val totalCount: Int
) {
val totalPages: Int
get() = ceil(totalCount / pageSize.toDouble()).toInt()
val hasPreviousPage: Boolean
    get() = pageNumber > 1

val hasNextPage: Boolean
    get() = pageNumber < totalPages

companion object {
    fun <T> create(items: List<T>, pageNumber: Int, pageSize: Int): PagedList<T> {
        val count = items.size
        val pagedItems = items
            .drop((pageNumber - 1) * pageSize)
            .take(pageSize)
        
        return PagedList(pagedItems, pageNumber, pageSize, count)
    }
    
    fun <T> createOptimized(pagedItems: List<T>, totalCount: Int, pageNumber: Int, pageSize: Int): PagedList<T> {
        return PagedList(pagedItems, pageNumber, pageSize, totalCount)
    }
}
}KD
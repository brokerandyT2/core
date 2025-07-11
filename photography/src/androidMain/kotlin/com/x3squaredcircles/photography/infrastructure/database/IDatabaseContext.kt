// core/src/commonMain/kotlin/com/x3squaredcircles/core/infrastructure/database/IDatabaseContext.kt
package com.x3squaredcircles.core.infrastructure.database
import kotlinx.coroutines.Job
/**

Database context interface for all database operations
Supports all entities defined in the SQLDelight schema files
*/
interface IDatabaseContext {
/**

Initialize the database and create all tables
*/
suspend fun initializeDatabaseAsync()

/**

Get a table query builder for the specified entity type
*/
fun <T> table(): ITableQuery<T>

/**

Insert a single entity
*/
suspend fun <T> insertAsync(entity: T): Int

/**

Insert multiple entities in a batch
*/
suspend fun <T> insertBatchAsync(entities: List<T>): List<Int>

/**

Update an existing entity
*/
suspend fun <T> updateAsync(entity: T): Int

/**

Delete an entity
*/
suspend fun <T> deleteAsync(entity: T): Int

/**

Execute a raw SQL query
*/
suspend fun executeAsync(sql: String, parameters: List<Any> = emptyList()): Int

/**

Execute a raw SQL query and return results
*/
suspend fun <T> queryAsync(sql: String, parameters: List<Any> = emptyList(), mapper: (Map<String, Any?>) -> T): List<T>

/**

Begin a database transaction
*/
suspend fun beginTransactionAsync()

/**

Commit the current transaction
*/
suspend fun commitTransactionAsync()

/**

Rollback the current transaction
*/
suspend fun rollbackTransactionAsync()

/**

Execute operations within a transaction
*/
suspend fun <T> withTransactionAsync(block: suspend () -> T): T
}



/**

Table query builder interface for type-safe database operations
*/
interface ITableQuery<T> {
/**

Add a WHERE clause condition
*/
fun where(predicate: (T) -> Boolean): ITableQuery<T>

/**

Add an ORDER BY clause
/
fun orderBy(selector: (T) -> Comparable<>): ITableQuery<T>

/**

Add an ORDER BY DESC clause
/
fun orderByDescending(selector: (T) -> Comparable<>): ITableQuery<T>

/**

Add a secondary ORDER BY clause
/
fun thenBy(selector: (T) -> Comparable<>): ITableQuery<T>

/**

Add a secondary ORDER BY DESC clause
/
fun thenByDescending(selector: (T) -> Comparable<>): ITableQuery<T>

/**

Skip the specified number of items
*/
fun skip(count: Int): ITableQuery<T>

/**

Take only the specified number of items
*/
fun take(count: Int): ITableQuery<T>

/**

Execute the query and return all results as a list
*/
suspend fun toList(): List<T>

/**

Execute the query and return the first result or null
*/
suspend fun firstOrNull(): T?

/**

Execute the query and return the first result
@throws NoSuchElementException if no element is found
*/
suspend fun first(): T

/**

Execute the query and return the count of results
*/
suspend fun count(): Int

/**

Execute the query and check if any results exist
*/
suspend fun any(): Boolean

/**

Execute the query and check if any results exist matching the predicate
*/
suspend fun any(predicate: (T) -> Boolean): Boolean
}



/**

Extension functions for common database operations
*/

/**

Find entity by ID
*/
suspend inline fun <reified T : Any> IDatabaseContext.findByIdAsync(id: Int): T? {
return table<T>().where { entity ->
// This would need to be implemented by the concrete implementation
// to extract the ID property from the entity
when (entity) {
is com.x3squaredcircles.core.domain.entities.Location -> entity.id == id
is com.x3squaredcircles.photography.domain.entities.CameraBody -> entity.id == id
is com.x3squaredcircles.photography.domain.entities.Lens -> entity.id == id
is com.x3squaredcircles.core.domain.entities.Setting -> entity.id == id
is com.x3squaredcircles.core.domain.entities.Tip -> entity.id == id
is com.x3squaredcircles.core.domain.entities.TipType -> entity.id == id
is com.x3squaredcircles.core.domain.entities.Weather -> entity.id == id
is com.x3squaredcircles.photography.domain.entities.LensCameraCompatibility -> entity.id == id
is com.x3squaredcircles.photography.domain.entities.PhoneCameraProfile -> entity.id == id
is com.x3squaredcircles.photography.domain.entities.UserCameraBody -> entity.id == id
is com.x3squaredcircles.core.domain.entities.Subscription -> entity.id == id
else -> false
}
}.firstOrNull()
}

/**

Check if entity exists by ID
*/
suspend inline fun <reified T : Any> IDatabaseContext.existsByIdAsync(id: Int): Boolean {
return findByIdAsync<T>(id) != null
}

/**

Get all entities of type T
*/
suspend inline fun <reified T : Any> IDatabaseContext.getAllAsync(): List<T> {
return table<T>().toList()
}

/**

Get paged results
*/
suspend inline fun <reified T : Any> IDatabaseContext.getPagedAsync(skip: Int, take: Int): List<T> {
return table<T>().skip(skip).take(take).toList()
}

/**

Delete entity by ID
*/
suspend inline fun <reified T : Any> IDatabaseContext.deleteByIdAsync(id: Int): Int {
val entity = findByIdAsync<T>(id) ?: return 0
return deleteAsync(entity)
}

/**

Simplified table access
*/
inline fun <reified T : Any> IDatabaseContext.table(): ITableQuery<T> = table<T>()
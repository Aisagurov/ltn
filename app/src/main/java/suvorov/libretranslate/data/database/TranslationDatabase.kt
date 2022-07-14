package suvorov.libretranslate.data.database

import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TranslationDatabase @Inject constructor(private val realm: Realm) {

    fun getAll(): Flow<List<TranslationRealm>> {
        return realm.query<TranslationRealm>().asFlow().map { it.list }
    }

    suspend fun insert(originalData: String, translatedData: String) {
        realm.write {
            copyToRealm(
                TranslationRealm().apply {
                    this.originalData = originalData
                    this.translatedData = translatedData
                }
            )
        }
    }

    suspend fun delete() {
        realm.write {
            query<TranslationRealm>().first().find()?.let { delete(it) }
                ?: throw IllegalStateException("Translation not found")
        }
    }

    suspend fun deleteAll() {
        realm.write {
            delete(query<TranslationRealm>())
        }
    }
}
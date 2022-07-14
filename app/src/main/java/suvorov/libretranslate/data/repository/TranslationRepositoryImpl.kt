package suvorov.libretranslate.data.repository

import kotlinx.coroutines.flow.Flow
import suvorov.libretranslate.data.api.ApiService
import suvorov.libretranslate.data.database.TranslationDatabase
import suvorov.libretranslate.data.database.TranslationRealm
import suvorov.libretranslate.domain.repository.TranslationRepository
import javax.inject.Inject

class TranslationRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val database: TranslationDatabase
    ): TranslationRepository {

    override suspend fun getTranslation(text: String, source: String, target: String) =
        apiService.getTranslation(text, source, target)

    override fun getAllFromDatabase(): Flow<List<TranslationRealm>> {
        return database.getAll()
    }

    override suspend fun insertIntoDatabase(originalData: String, translatedData: String) {
        database.insert(originalData, translatedData)
    }

    override suspend fun deleteFromDatabase() {
        database.delete()
    }

    override suspend fun deleteAllFromDatabase() {
        database.deleteAll()
    }
}
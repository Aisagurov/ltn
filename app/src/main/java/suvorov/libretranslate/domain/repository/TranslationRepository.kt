package suvorov.libretranslate.domain.repository

import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import suvorov.libretranslate.data.api.Translation
import suvorov.libretranslate.data.database.TranslationRealm

interface TranslationRepository {

    suspend fun getTranslation(text: String, source: String, target: String): Response<Translation>

    fun getAllFromDatabase(): Flow<List<TranslationRealm>>

    suspend fun insertIntoDatabase(originalData: String, translatedData: String)

    suspend fun deleteFromDatabase()

    suspend fun deleteAllFromDatabase()
}
package suvorov.libretranslate.data.repository

import androidx.lifecycle.LiveData
import suvorov.libretranslate.api.ApiService
import suvorov.libretranslate.data.database.TranslationDao
import suvorov.libretranslate.data.database.TranslationEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslationRepository @Inject constructor(
    private val translationDao: TranslationDao,
    private val apiService: ApiService
    ) {

    suspend fun getTranslation(text: String, source: String, target: String) =
        apiService.getTranslation(text, source, target)

    fun getAll(): LiveData<List<TranslationEntity>> {
        return translationDao.getAll()
    }

    suspend fun insert(translation: TranslationEntity) {
        translationDao.insert(translation)
    }

    suspend fun delete(translation: TranslationEntity) {
        translationDao.delete(translation)
    }

    suspend fun deleteAll() {
        translationDao.deleteAll()
    }
}
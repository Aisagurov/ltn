package suvorov.libretranslate.data.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TranslationDao {
    @Query("SELECT * FROM translation")
    fun getAll(): LiveData<List<TranslationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(translation: TranslationEntity)

    @Delete
    suspend fun delete(translation: TranslationEntity)

    @Query("DELETE FROM translation")
    suspend fun deleteAll()
}
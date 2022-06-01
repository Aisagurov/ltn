package suvorov.libretranslate.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translation")
data class TranslationEntity(
    @PrimaryKey
    val originalData: String,
    val translatedData: String
)
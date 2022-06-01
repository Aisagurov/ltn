package suvorov.libretranslate.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import suvorov.libretranslate.data.database.TranslationDao
import suvorov.libretranslate.data.database.TranslationDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TranslationDatabase {
        return Room.databaseBuilder(context, TranslationDatabase::class.java, "TranslationDatabase").build()
    }

    @Provides
    @Singleton
    fun provideDao(database: TranslationDatabase): TranslationDao {
        return database.translationDao()
    }
}
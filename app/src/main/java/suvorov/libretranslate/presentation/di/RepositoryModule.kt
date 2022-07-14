package suvorov.libretranslate.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import suvorov.libretranslate.data.api.ApiService
import suvorov.libretranslate.data.database.TranslationDatabase
import suvorov.libretranslate.data.repository.TranslationRepositoryImpl
import suvorov.libretranslate.domain.repository.TranslationRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideTranslationRepository(
        service: ApiService,
        database: TranslationDatabase
        ): TranslationRepository {

        return TranslationRepositoryImpl(service, database)
    }
}
package suvorov.libretranslate.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import suvorov.libretranslate.data.database.TranslationDatabase
import suvorov.libretranslate.data.database.TranslationRealm
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideRealm(): Realm {
        val configuration = RealmConfiguration.create(setOf(TranslationRealm::class))
        return Realm.open(configuration)
    }

    @Provides
    @Singleton
    fun provideDatabase(realm: Realm): TranslationDatabase {
        return TranslationDatabase(realm)
    }
}
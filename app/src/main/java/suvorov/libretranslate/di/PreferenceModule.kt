package suvorov.libretranslate.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import suvorov.libretranslate.data.preference.SharedPreference
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PreferenceModule {
    @Provides
    @Singleton
    fun provideSharedPreference(@ApplicationContext context: Context) = SharedPreference(context)
}
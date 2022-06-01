package suvorov.libretranslate.api

import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query
import suvorov.libretranslate.api.model.Translation

interface ApiService {
    @POST("translate")
    suspend fun getTranslation(
        @Query("q") text: String,
        @Query("source") source: String,
        @Query("target") target: String
    ): Response<Translation>
}
package suvorov.libretranslate.data.preference

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreference @Inject constructor(context: Context) {
    private val preferences = context.getSharedPreferences("TRANSLATION_PREFERENCES", Context.MODE_PRIVATE)

    fun set(KEY_NAME: String, text: String) {
        preferences.edit().putString(KEY_NAME, text).apply()
    }

    fun set(KEY_NAME: String, value: Int) {
        preferences.edit().putInt(KEY_NAME, value).apply()
    }

    fun getValueString(KEY_NAME: String): String? {
        return preferences.getString(KEY_NAME, null)
    }

    fun getValueInt(KEY_NAME: String, defValue: Int): Int {
        return preferences.getInt(KEY_NAME, defValue)
    }
}
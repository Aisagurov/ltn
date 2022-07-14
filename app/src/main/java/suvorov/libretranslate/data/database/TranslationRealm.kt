package suvorov.libretranslate.data.database

import io.realm.kotlin.types.RealmObject

class TranslationRealm: RealmObject {
    var originalData: String = ""
    var translatedData: String? = null
}
package suvorov.libretranslate.ui.translation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import suvorov.libretranslate.api.model.Translation
import suvorov.libretranslate.data.repository.TranslationRepository
import javax.inject.Inject

@HiltViewModel
class TranslationViewModel @Inject constructor(private val repository: TranslationRepository): ViewModel() {

    val translation = MutableLiveData<Translation>()

    fun getTranslation(text: String, source: String, target: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getTranslation(text, source, target).apply {
                    translation.postValue(body())
                }
            }catch (e: Exception) {
                e.message.toString()
            }
        }
    }
}
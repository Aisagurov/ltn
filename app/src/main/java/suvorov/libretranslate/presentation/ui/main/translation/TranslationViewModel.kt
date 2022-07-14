package suvorov.libretranslate.presentation.ui.main.translation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import suvorov.libretranslate.data.api.Translation
import suvorov.libretranslate.domain.repository.TranslationRepository
import javax.inject.Inject

@HiltViewModel
class TranslationViewModel @Inject constructor(private val repository: TranslationRepository): ViewModel() {

    val translation = MutableLiveData<Translation>()

    fun getTranslation(text: String, source: String, target: String) {
        viewModelScope.launch {
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
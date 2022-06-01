package suvorov.libretranslate.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import suvorov.libretranslate.data.database.TranslationEntity
import suvorov.libretranslate.data.repository.TranslationRepository
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val repository: TranslationRepository): ViewModel() {

    fun getAllFromDatabase(): LiveData<List<TranslationEntity>> {
        return repository.getAll()
    }

    fun insertIntoDatabase(translation: TranslationEntity) {
        viewModelScope.launch {
            repository.insert(translation)
        }
    }

    fun deleteFromDatabase(translation: TranslationEntity) {
        viewModelScope.launch {
            repository.delete(translation)
        }
    }

    fun deleteAllDatabase() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }
}
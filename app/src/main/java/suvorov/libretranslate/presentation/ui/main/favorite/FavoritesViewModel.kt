package suvorov.libretranslate.presentation.ui.main.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import suvorov.libretranslate.data.database.TranslationRealm
import suvorov.libretranslate.domain.repository.TranslationRepository
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val repository: TranslationRepository): ViewModel() {

    fun getAllFromDatabase(): Flow<List<TranslationRealm>> {
        return repository.getAllFromDatabase()
    }

    fun insertIntoDatabase(originalData: String, translatedData: String) {
        viewModelScope.launch {
            repository.insertIntoDatabase(originalData, translatedData)
        }
    }

    fun deleteFromDatabase() {
        viewModelScope.launch {
            repository.deleteFromDatabase()
        }
    }

    fun deleteAllFromDatabase() {
        viewModelScope.launch {
            repository.deleteAllFromDatabase()
        }
    }
}
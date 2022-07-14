package suvorov.libretranslate.util

import android.widget.ArrayAdapter
import android.widget.Spinner

object SpinnerHelper {
    fun changeDirectionTranslation(
        sourceSpinner: Spinner,
        targetSpinner: Spinner,
        sourceLanguageAdapter: ArrayAdapter<String>?,
        targetLanguageAdapter: ArrayAdapter<String>?
    ) {
        val spinnerIndex = sourceSpinner.selectedItemPosition
        sourceSpinner.setSelection(targetSpinner.selectedItemPosition)
        targetSpinner.setSelection(spinnerIndex)
        sourceLanguageAdapter?.notifyDataSetChanged()
        targetLanguageAdapter?.notifyDataSetChanged()
    }
}
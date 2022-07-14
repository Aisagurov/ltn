package suvorov.libretranslate.util

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import java.util.*

object VoiceHelper {
    fun promptSpeechInput(resultLauncher: ActivityResultLauncher<Intent>) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите")
        try {
            resultLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            e.message
        }
    }

    fun getResultLauncher(fragment: Fragment, inputText: EditText): ActivityResultLauncher<Intent> {
        val resultLauncher = fragment.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Constants.REQUEST_CODE_SPEECH_INPUT) {
                    if (result.resultCode == Activity.RESULT_OK) {
                        val data = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                        inputText.setText(data!![0])
                    }
                }
            }
        return resultLauncher
    }
}
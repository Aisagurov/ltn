package suvorov.libretranslate.ui.translation

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import suvorov.libretranslate.R
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import suvorov.libretranslate.ui.favorites.FavoritesViewModel
import suvorov.libretranslate.data.database.TranslationEntity
import suvorov.libretranslate.data.preference.SharedPreference
import suvorov.libretranslate.databinding.FragmentTranslationBinding
import suvorov.libretranslate.util.Constants.REQUEST_CODE_SPEECH_INPUT
import suvorov.libretranslate.util.hideKeyboard
import javax.inject.Inject
import java.util.*

@AndroidEntryPoint
class TranslationFragment: Fragment(), TextWatcher, TextToSpeech.OnInitListener {
    private var _binding: FragmentTranslationBinding? = null
    private val binding get() = _binding!!

    private val translationViewModel: TranslationViewModel by viewModels()
    private val historyViewModel: FavoritesViewModel by viewModels()

    @Inject lateinit var preference: SharedPreference

    private var sourceCode = ""
    private var targetCode = ""
    private var tts: TextToSpeech? = null
    private var sourceLanguageAdapter: ArrayAdapter<String>? = null
    private var targetLanguageAdapter: ArrayAdapter<String>? = null
    private var resultLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTranslationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadAdapter()

        loadData()

        translationData()

        getResultLauncher()

        binding.inputText.addTextChangedListener(this)

        tts = TextToSpeech(requireActivity(), this)

        binding.directionTranslationImageView.setOnClickListener {
            changeDirectionTranslation()
        }

        binding.deleteInputTextImageView.setOnClickListener {
            binding.inputText.text.clear()
        }

        binding.speechButton.setOnClickListener {
            speakOut()
            hideKeyboard(requireActivity())
        }

        binding.voiceButton.setOnClickListener {
            promptSpeechInput()
        }

        binding.saveButton.setOnClickListener {
            if(binding.inputText.text.isNotEmpty() && binding.outputText.text.isNotEmpty()) {
                saveIntoDatabase()
                hideKeyboard(requireActivity())
                showToast("Сохранено в избранное")
            }
            else {
                showToast("Перевод для сохранения отсутствует")
            }
        }
    }

    private fun loadAdapter() {
        val sourceLanguageAdapter = ArrayAdapter.createFromResource(
            requireActivity(), R.array.languages_names, android.R.layout.simple_spinner_item)

        sourceLanguageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.sourceSpinner.adapter = sourceLanguageAdapter

        binding.sourceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sourceCode = resources.getStringArray(R.array.languages_codes)[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val targetLanguageAdapter = ArrayAdapter.createFromResource(
            requireActivity(), R.array.languages_names, android.R.layout.simple_spinner_item)

        targetLanguageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.targetSpinner.adapter = sourceLanguageAdapter

        binding.targetSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                targetCode = resources.getStringArray(R.array.languages_codes)[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun translationData() {
        translationViewModel.translation.observe(viewLifecycleOwner) {
            binding.outputText.text = it?.translatedText
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if(s.toString().isNotEmpty()) {
            binding.outputText.visibility = View.VISIBLE
            binding.deleteInputTextImageView.visibility = View.VISIBLE
        }
        else {
            binding.outputText.clearComposingText()
            binding.outputText.visibility = View.GONE
            binding.deleteInputTextImageView.visibility = View.GONE
        }
    }

    override fun afterTextChanged(s: Editable?) {
        s?.let {
            Linkify.addLinks(it, Linkify.WEB_URLS)
        }

        updateTranslationCard()
    }

    private fun updateTranslationCard() {
        translationViewModel.getTranslation(binding.inputText.text.toString().trim(), sourceCode, targetCode)
    }

    private fun changeDirectionTranslation() {
        val spinnerIndex = binding.sourceSpinner.selectedItemPosition
        binding.sourceSpinner.setSelection(binding.targetSpinner.selectedItemPosition)
        binding.targetSpinner.setSelection(spinnerIndex)
        sourceLanguageAdapter?.notifyDataSetChanged()
        targetLanguageAdapter?.notifyDataSetChanged()
    }

    private fun saveIntoDatabase() {
        historyViewModel.insertIntoDatabase(
            TranslationEntity(
                binding.inputText.text.toString().trim(),
                binding.outputText.text.toString().trim()
            )
        )
    }

    private fun saveData() {
        preference.set("SOURCE_SPINNER", binding.sourceSpinner.selectedItemPosition)
        preference.set("TARGET_SPINNER", binding.targetSpinner.selectedItemPosition)
        preference.set("OUTPUT_TEXT", binding.outputText.text.toString())
    }

    private fun loadData() {
        binding.sourceSpinner.setSelection(preference.getValueInt("SOURCE_SPINNER", 1))
        binding.targetSpinner.setSelection(preference.getValueInt("TARGET_SPINNER", 15))
        binding.outputText.text = preference.getValueString("OUTPUT_TEXT")
    }

    private fun getResultLauncher() {
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == REQUEST_CODE_SPEECH_INPUT) {
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    binding.inputText.setText(data!![0])
                }
            }
        }
    }

    private fun promptSpeechInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите")
        try {
            resultLauncher?.launch(intent)
        } catch (a: ActivityNotFoundException) {
            showToast("Ошибка голосового ввода")
        }
    }

    private fun speakOut() {
        val text = binding.inputText.text.toString()
        if (text == "") showToast("Введите текст")
        else tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            binding.speechButton.isEnabled = true
        } else {
            showToast("Ошибка при озвучивании текста")
        }
    }

    override fun onDestroyView(){
        super.onDestroyView()
        saveData()

        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }

        _binding = null
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
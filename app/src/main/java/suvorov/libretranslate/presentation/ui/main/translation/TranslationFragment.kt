package suvorov.libretranslate.presentation.ui.main.translation

import android.os.Bundle
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import suvorov.libretranslate.R
import suvorov.libretranslate.data.preference.SharedPreference
import suvorov.libretranslate.databinding.FragmentTranslationBinding
import suvorov.libretranslate.presentation.ui.main.favorite.FavoritesViewModel
import suvorov.libretranslate.util.Constants
import suvorov.libretranslate.util.KeyboardHelper
import suvorov.libretranslate.util.SpinnerHelper
import suvorov.libretranslate.util.VoiceHelper
import javax.inject.Inject

@AndroidEntryPoint
class TranslationFragment: Fragment(), TextWatcher, TextToSpeech.OnInitListener {
    private var _binding: FragmentTranslationBinding? = null
    private val binding get() = _binding!!

    private val translationViewModel: TranslationViewModel by viewModels()
    private val favoritesViewModel: FavoritesViewModel by viewModels()

    @Inject
    lateinit var preference: SharedPreference

    private var sourceCode = ""
    private var targetCode = ""
    private var tts: TextToSpeech? = null
    private var sourceLanguageAdapter: ArrayAdapter<String>? = null
    private var targetLanguageAdapter: ArrayAdapter<String>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTranslationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resultLauncher = VoiceHelper.getResultLauncher(this, binding.inputText)

        loadAdapter()

        loadData()

        translationData()

        binding.inputText.addTextChangedListener(this)

        tts = TextToSpeech(requireActivity(), this)

        binding.directionTranslationImageView.setOnClickListener {
            SpinnerHelper.changeDirectionTranslation(
                binding.sourceSpinner, binding.targetSpinner,
                sourceLanguageAdapter, targetLanguageAdapter
            )
        }

        binding.deleteInputTextImageView.setOnClickListener {
            binding.inputText.text.clear()
        }

        binding.speechButton.setOnClickListener {
            speakOut()
            KeyboardHelper.hideKeyboard(requireActivity())
        }

        binding.voiceButton.setOnClickListener {
            VoiceHelper.promptSpeechInput(resultLauncher)
        }

        binding.saveButton.setOnClickListener {
            if(binding.inputText.text.isNotEmpty() && binding.outputText.text.isNotEmpty()) {
                saveIntoDatabase()
                KeyboardHelper.hideKeyboard(requireActivity())
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
        translationViewModel.getTranslation(
            binding.inputText.text.toString().trim(),
            sourceCode, targetCode
        )
    }

    private fun saveIntoDatabase() {
        favoritesViewModel.insertIntoDatabase(
            binding.inputText.text.toString(),
            binding.outputText.text.toString()
        )
    }

    private fun saveData() {
        preference.set(Constants.SOURCE_SPINNER, binding.sourceSpinner.selectedItemPosition)
        preference.set(Constants.TARGET_SPINNER, binding.targetSpinner.selectedItemPosition)
        preference.set(Constants.OUTPUT_TEXT, binding.outputText.text.toString())
    }

    private fun loadData() {
        binding.sourceSpinner.setSelection(preference.getValueInt(Constants.SOURCE_SPINNER, 1))
        binding.targetSpinner.setSelection(preference.getValueInt(Constants.TARGET_SPINNER, 15))
        binding.outputText.text = preference.getValueString(Constants.OUTPUT_TEXT)
    }

    private fun speakOut() {
        val text = binding.inputText.text.toString()
        if (text == "") showToast("Введите текст")
        else tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
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
            tts?.stop()
            tts?.shutdown()
        }

        _binding = null
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
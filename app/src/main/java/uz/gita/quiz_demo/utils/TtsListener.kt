package uz.gita.quiz_demo.utils

import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import java.util.Locale

class TtsListener(private val tts: TextToSpeech?, private val soundButton: View) :
    TextToSpeech.OnInitListener {
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "The Language not supported!")
            } else {
                soundButton.isEnabled = true
            }
        }
    }

}
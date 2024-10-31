package uz.gita.quiz_demo

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Space
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import uz.gita.quiz_demo.databinding.ActivityQuizBinding
import uz.gita.quiz_demo.dialogs.ResultDialog
import uz.gita.quiz_demo.model.QuizModel
import uz.gita.quiz_demo.utils.HexagonView
import uz.gita.quiz_demo.utils.TtsListener

class QuizActivity : AppCompatActivity() {
    private var _binding: ActivityQuizBinding? = null
    private val binding get() = _binding!!
    private var currentIndex = 0
    private var correctCount = 0
    private lateinit var spacer: Space
    private var tts: TextToSpeech? = null
    private var answerButtons = mutableListOf<AppCompatButton>()
    private var variantButtons = mutableListOf<HexagonView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        _binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
        initActions()
        loadDataToViews()
    }

    private fun initActions() {
        binding.btnToBack.setOnClickListener { finish() }
        variantButtons.forEach { hexView -> hexView.setOnClickListener { clickVariant(hexView) } }
        binding.btnSound.setOnClickListener { speakOut() }
        binding.btnPrev.setOnClickListener { currentIndex--;loadDataToViews() }
        binding.btnNext.setOnClickListener {
            if (currentIndex == quizList.lastIndex) {
                showResultDialog()
            } else {
                currentIndex++;loadDataToViews()
            }
        }
    }

    private fun initViews() {
        binding.btnSound.isEnabled = false
        binding.progress.max = quizList.size
        tts = TextToSpeech(this, TtsListener(tts, binding.btnSound))
        spacer = LayoutInflater.from(this)
            .inflate(R.layout.item_spacer, binding.answerGroup, false) as Space
        variantButtons.add(binding.hv11)
        variantButtons.add(binding.hv12)
        variantButtons.add(binding.hv13)
        variantButtons.add(binding.hv14)
        variantButtons.add(binding.hv15)
        variantButtons.add(binding.hv16)
        variantButtons.add(binding.hv21)
        variantButtons.add(binding.hv22)
        variantButtons.add(binding.hv23)
        variantButtons.add(binding.hv24)
        variantButtons.add(binding.hv25)
        variantButtons.add(binding.hv31)
        variantButtons.add(binding.hv32)
        variantButtons.add(binding.hv33)
        variantButtons.add(binding.hv34)
    }

    private fun loadDataToViews() {
        manageBottomButtons()
        binding.tvLevel.text = (currentIndex + 1).toString()
        binding.progress.progress = currentIndex + 1
        binding.imgQuiz.setImageResource(quizList[currentIndex].imageRes)
        updateAnswerButtons(quizList[currentIndex].answer)
        updateVariantButtons(quizList[currentIndex].variant)
    }

    private fun updateAnswerButtons(answer: String) {
        binding.answerGroup.removeAllViews()
        answerButtons.clear()
        answer.forEachIndexed { index, sign ->
            val newAnswer = LayoutInflater.from(this)
                .inflate(R.layout.item_answer, binding.answerGroup, false) as AppCompatButton
            newAnswer.setOnClickListener { clickAnswer(newAnswer) }
            checkAnswerButtonIsFilled(sign.toString(), newAnswer)
            answerButtons.add(newAnswer)
            binding.answerGroup.addView(newAnswer)
        }
    }

    private fun updateVariantButtons(variant: String) {
        variant.forEachIndexed { index, sign ->
            variantButtons[index].symbol = sign.toString()
            variantButtons[index].isGradientEnabled = false
            variantButtons[index].isEnabled = true
        }
    }

    private fun checkAnswerButtonIsFilled(sign: String, answer: AppCompatButton) {
        if (quizList[currentIndex].isFilled) {
            answer.text = sign.toString()
            answer.setBackgroundResource(R.drawable.bg_fill_correct_btn)
        } else {
            answer.setBackgroundResource(R.drawable.bg_unfill_btn)
        }
    }

    private fun showResultDialog() {
        val dialog = ResultDialog(quizList.size, correctCount)
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, "Result")
        dialog.apply {
            setOnMenuClickListener { dismiss();finish() }
            setOnReplayClickListener {
                currentIndex = 0
                quizList.forEach { it.isFilled = false }
                loadDataToViews()
                dismiss()
            }
        }
    }

    private fun clickAnswer(button: AppCompatButton) {
        if (button.text.isNotEmpty()) {
            variantButtons.forEach { variantButtons ->
                if (variantButtons.isGradientEnabled && variantButtons.symbol == button.text) {
                    button.setBackgroundResource(R.drawable.bg_unfill_btn)
                    button.text = ""
                    variantButtons.isGradientEnabled = false
                    variantButtons.isEnabled = true
                    setDefault()
                    return
                }
            }
        }
    }

    private fun clickVariant(hexagonView: HexagonView) {
        answerButtons.forEach {
            if (it.text.isNullOrEmpty()) {
                it.text = hexagonView.symbol
                it.tag = hexagonView.id
                it.setBackgroundResource(R.drawable.bg_fill_btn)
                hexagonView.isEnabled = false
                hexagonView.isGradientEnabled = true
                check()
                return
            }
        }
    }

    private fun check() {
        val ans = StringBuilder()
        answerButtons.forEach { ans.append(it.text) }
        if (ans.toString().lowercase() == quizList[currentIndex].answer.lowercase()) {
            quizList[currentIndex].isFilled = true
            noticeCorrectAnswer()
        } else if (ans.toString().length == quizList[currentIndex].answer.length) {
            noticeWrongAnswer()
        } else {
            setDefault()
        }
    }

    private fun setDefault() {
        if (answerButtons.first().currentTextColor == Color.RED) {
            answerButtons.forEach { answerButton ->
                answerButton.setTextColor(Color.WHITE)
            }
        }
    }

    private fun noticeWrongAnswer() {
        val vibrator = this.getSystemService("vibrator") as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
        answerButtons.forEach { answerButton ->
            val animation = AnimationUtils.loadAnimation(this, R.anim.vibrate_animation)
            answerButton.startAnimation(animation)
            answerButton.setTextColor(Color.RED)
        }
    }

    private fun noticeCorrectAnswer() {
        correctCount++
        answerButtons.forEach { answerButton ->
            val animation = AnimationUtils.loadAnimation(this, R.anim.scale_animation)
            answerButton.startAnimation(animation)
            answerButton.setBackgroundResource(R.drawable.bg_fill_correct_btn)
        }
    }

    private fun manageBottomButtons() {
        when (currentIndex) {
            0 -> binding.btnPrev.visibility = View.GONE
            quizList.lastIndex -> {
                binding.btnPrev.visibility = View.VISIBLE
                binding.btnNext.text = "Finish"
            }

            else -> {
                binding.btnPrev.visibility = View.VISIBLE
                binding.btnNext.text = "Next"
            }
        }
    }


    private fun speakOut() {
        tts?.speak(quizList[currentIndex].answer, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    public override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
        super.onDestroy()
    }

    companion object {
        private val quizList = listOf(
            QuizModel(R.drawable.ic_crown_svg, "curourorowkneni", "crown"),
            QuizModel(R.drawable.ic_tomato, "cutouaormokneti", "tomato"),
            QuizModel(R.drawable.shark, "slhtubaroskneni", "shark"),
            QuizModel(R.drawable.ic_turtle, "uroutrorotkelni", "turtle"),
            QuizModel(R.drawable.ic_grapes, "groarohpowknesi", "grapes"),
        )
    }
}
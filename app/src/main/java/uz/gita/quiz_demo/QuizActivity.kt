package uz.gita.quiz_demo

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Space
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import uz.gita.quiz_demo.custom_view.HexagonView
import uz.gita.quiz_demo.databinding.ActivityQuizBinding
import uz.gita.quiz_demo.model.QuizModel

class QuizActivity : AppCompatActivity() {
    private var _binding: ActivityQuizBinding? = null
    private val binding get() = _binding!!
    private var currentIndex = 0
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
                Toast.makeText(this, "Finish!", Toast.LENGTH_SHORT).show()
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
        binding.answerGroup.removeAllViews()
        manageBottomButtons()
        answerButtons.clear()
        binding.tvLevel.text = "${currentIndex + 1}"
        binding.progress.progress = currentIndex + 1
        binding.imgQuiz.setImageResource(quizList[currentIndex].imageRes)
        quizList[currentIndex].answer.forEach { sign ->
            val answer = LayoutInflater.from(this)
                .inflate(R.layout.item_answer, binding.answerGroup, false) as AppCompatButton
            answer.setOnClickListener { clickAnswer(answer) }
            answerButtons.add(answer)
            binding.answerGroup.addView(answer)
        }
        quizList[currentIndex].variant.forEachIndexed { index, sign ->
            variantButtons[index].symbol = sign.toString()
            variantButtons[index].isGradientEnabled = false
            variantButtons[index].isEnabled = true
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
            Toast.makeText(this, "You Win!", Toast.LENGTH_SHORT).show()
        } else if (ans.toString().length == quizList[currentIndex].answer.length) {
            Toast.makeText(this, "Answer is wrong!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun manageBottomButtons() {
        if (currentIndex == 0) {
            binding.btnPrev.visibility = View.GONE
        } else if (currentIndex == quizList.lastIndex) {
            binding.btnPrev.visibility = View.VISIBLE
            binding.btnNext.text = "Finish"
        } else {
            binding.btnPrev.visibility = View.VISIBLE
            binding.btnNext.text = "Next"

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
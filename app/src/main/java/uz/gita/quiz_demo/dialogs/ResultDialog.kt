package uz.gita.quiz_demo.dialogs

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import uz.gita.quiz_demo.databinding.DialogResultBinding

class ResultDialog(private val totalCount: Int,private val correctCount: Int) : DialogFragment() {
    private var _binding: DialogResultBinding? = null
    private val binding get() = _binding!!
    private var onMenuClickListener: (() -> Unit)? = null
    private var onReplayClickListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("TTT", "onCreateView: ")
        _binding = DialogResultBinding.inflate(inflater,container,false)
        return binding.root
    }

    fun setOnMenuClickListener(listener: () -> Unit) {
        onMenuClickListener = listener
    }

    fun setOnReplayClickListener(listener: () -> Unit) {
        onReplayClickListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnMenu.setOnClickListener { onMenuClickListener?.invoke() }
        binding.btnReplay.setOnClickListener { onReplayClickListener?.invoke() }
        binding.tvCorrect.text = correctCount.toString()
        binding.tvWrong.text = (totalCount - correctCount).toString()
        val percent = (correctCount.toFloat() / totalCount.toFloat()) * 100f
        Log.d("TTT", "onViewCreated: $percent")
        binding.tvPercent.text = "${percent.toInt()}%"
    }

    override fun dismiss() {
        super.dismiss()
        _binding = null
    }

}
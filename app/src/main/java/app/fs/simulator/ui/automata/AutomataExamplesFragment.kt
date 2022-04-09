package app.fs.simulator.ui.automata

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import app.fs.simulator.databinding.FragmentAutomatonExamplesBinding
import app.fs.simulator.ui.compiler.MachineViewModel

class AutomataExamplesFragment : DialogFragment() {

    private var _binding : FragmentAutomatonExamplesBinding? = null
    private val binding get() = _binding!!

    private val viewModel : MachineViewModel by activityViewModels()

    private var exampleSelected = false

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAutomatonExamplesBinding.inflate(inflater)

        binding.exe1TextView.setOnClickListener {
            viewModel.getAutomataExamplesFromLocalFile(1)
            exampleSelected = true
            dismiss()
        }

        binding.exe2TextView.setOnClickListener {
            viewModel.getAutomataExamplesFromLocalFile(2)
            exampleSelected = true
            dismiss()
        }

        binding.exe3TextView.setOnClickListener {
            viewModel.getAutomataExamplesFromLocalFile(3)
            exampleSelected = true
            dismiss()
        }

        binding.exe4TextView.setOnClickListener {
            viewModel.getAutomataExamplesFromLocalFile(4)
            exampleSelected = true
            dismiss()
        }

        binding.exe5TextView.setOnClickListener {
            viewModel.getAutomataExamplesFromLocalFile(5)
            exampleSelected = true
            dismiss()
        }

        binding.exe6TextView.setOnClickListener {
            viewModel.getAutomataExamplesFromLocalFile(6)
            exampleSelected = true
            dismiss()
        }

        binding.exe7TextView.setOnClickListener {
            viewModel.getAutomataExamplesFromLocalFile(7)
            exampleSelected = true
            dismiss()
        }

        binding.exe8TextView.setOnClickListener {
            viewModel.getAutomataExamplesFromLocalFile(8)
            exampleSelected = true
            dismiss()
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.playerButtonState.value = exampleSelected
    }


    fun newInstance() : DialogFragment = AutomataExamplesFragment()
}

package app.fs.simulator.ui.turing

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import app.fs.simulator.databinding.FragmentTuringMachineExamplesBinding
import app.fs.simulator.ui.compiler.MachineViewModel

class TuringMachineExamplesFragment : DialogFragment() {

    private var _binding : FragmentTuringMachineExamplesBinding? = null
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
        _binding = FragmentTuringMachineExamplesBinding.inflate(inflater)

        binding.exe1TextView.setOnClickListener {
            viewModel.getTuringMachineExampleFromLocalFile(1)
            exampleSelected = true
            dismiss()
        }

        binding.exe2TextView.setOnClickListener {
            viewModel.getTuringMachineExampleFromLocalFile(2)
            exampleSelected = true
            dismiss()
        }

        binding.exe3TextView.setOnClickListener {
            viewModel.getTuringMachineExampleFromLocalFile(3)
            exampleSelected = true
            dismiss()
        }

        binding.exe4TextView.setOnClickListener {
            viewModel.getTuringMachineExampleFromLocalFile(5)
            exampleSelected = true
            dismiss()
        }

        binding.exe5TextView.setOnClickListener {
            viewModel.getTuringMachineExampleFromLocalFile(4)
            exampleSelected = true
            dismiss()
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.playerButtonState.value = exampleSelected
    }

    companion object {
        fun newInstance() = TuringMachineExamplesFragment()
    }

}
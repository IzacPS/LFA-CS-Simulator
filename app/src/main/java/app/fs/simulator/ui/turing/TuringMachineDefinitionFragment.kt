package app.fs.simulator.ui.turing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import app.fs.lfa.tm.TuringMachine
import app.fs.lfa.tm.customToStringWithComma
import app.fs.simulator.R
import app.fs.simulator.base.BaseCustomDialogFragment
import app.fs.simulator.custom.CustomDefocusableTextInputEditText
import app.fs.simulator.databinding.FragmentTuringMachineDefinitionBinding
import app.fs.simulator.databinding.TransitionListItemBinding
import app.fs.simulator.ui.compiler.MachineViewModel

class TuringMachineDefinitionFragment : BaseCustomDialogFragment(){

    private var _binding : FragmentTuringMachineDefinitionBinding? = null
    private val binding get() = _binding!!

    private val viewModel : MachineViewModel by activityViewModels()

    private var hasClickedToCreateMachine = false

    private lateinit var aa1 : ArrayAdapter<String>
    private lateinit var aa2 : ArrayAdapter<String>
    private lateinit var aa3 : ArrayAdapter<String>
    private lateinit var aa4 : ArrayAdapter<String>

    var machine = TuringMachine<String, String>()
    val transitions : MutableList<TuringMachine.TransitionData<String,String>> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTuringMachineDefinitionBinding.inflate(inflater)

        bindTitleTextViewToSubItemContainerLayout()

        setupEditTexts()

        binding.alphabetInputEditText.setOnFocusChangeListener { view, b ->
            if(b.not()){
                var tapeInput = binding.tapeInputEditText.text?.toString()?.replace(" ", "")?.split(",")?.toMutableSet()
                tapeInput?.addAll((view as CustomDefocusableTextInputEditText).text.toString().split(","))
                aa1.clear()
                tapeInput?.remove("")
                tapeInput = tapeInput?.toSortedSet()
                tapeInput?.customToStringWithComma()?.split(",")?.let { aa1.addAll(it) }
            }
        }
        binding.tapeInputEditText.setOnFocusChangeListener { view, b ->
            if(b.not()){
                var alphabetInput = binding.alphabetInputEditText.text?.toString()?.replace(" ", "")?.split(",")?.toMutableSet()
                val thisInput = (view as CustomDefocusableTextInputEditText).text.toString().split(",")

                aa4.clear()
                aa4.addAll(thisInput)

                alphabetInput?.addAll(thisInput)
                aa1.clear()

                alphabetInput?.remove("")
                alphabetInput = alphabetInput?.toSortedSet()
                alphabetInput?.customToStringWithComma()?.split(",")?.let { aa1.addAll(it) }
            }
        }

        binding.statesInputEditText.setOnFocusChangeListener { view, b ->
            if(b.not()){
                val states = (view as CustomDefocusableTextInputEditText).text.toString().replace(" ", "").split(",")
                machine.addStates(states)
                aa2.clear()
                aa2.addAll(states)
            }
        }

        binding.addTransitionButton.setOnClickListener {
            val direction = when((binding.directionMenu.editText as? AutoCompleteTextView)?.text.toString()){
                "ESQUERDA" -> TuringMachine.HeadDirection.ESQUERDA
                "DIREITA" -> TuringMachine.HeadDirection.DIREITA
                else -> TuringMachine.HeadDirection.NONE
            }

            val transition = TuringMachine.TransitionData(
                (binding.fromStateMenu.editText as? AutoCompleteTextView)?.text.toString(),
                (binding.symbolReadMenu.editText as? AutoCompleteTextView)?.text.toString(),
                (binding.toStateMenu.editText as? AutoCompleteTextView)?.text.toString(),
                (binding.symbolWriteMenu.editText as? AutoCompleteTextView)?.text.toString(),
                direction)

            val sli = TransitionListItemBinding.inflate(inflater)
            //sli.transition = transition
            sli.removeTransitionButton.setOnClickListener {
                binding.subItemTransitionsContainer.removeView(sli.root)
                transitions.remove(transition)
            }
            sli.textView.text = transition.toString()
            binding.subItemTransitionsContainer.addView(sli.transitionItemRoot)

            transitions.add(transition)
        }

        binding.createMachineButton.setOnClickListener {
            binding.tapeInputEditText.text?.toString()?.replace(" ", "")?.split(",")?.toList()?.let {
                machine.addTapeAlphabet(it)
            }
            binding.alphabetInputEditText.text?.toString()?.replace(" ", "")?.split(",")?.toList()?.let {
                machine.addInputAlphabet(it)
            }
            binding.statesInputEditText.text.toString().replace(" ", "").split(",").let {
                machine.addStates(it)
            }
            binding.startStateMenu.editText?.text.toString().let { machine.startState = it }
            binding.acceptStateMenu.editText?.text.toString().let { machine.acceptState = it }
            binding.rejectStateMenu.editText?.text.toString().let { machine.rejectState = it }
            binding.emptySymbolStateMenu.editText?.text.toString().let { machine.emptySymbol =
                it.ifEmpty { " " }
            }

            machine.setupTransition(transitions)
            viewModel.currentMachine.value = machine
            hasClickedToCreateMachine = true
            dismiss()
        }


        return binding.root
    }

    private fun bindTitleTextViewToSubItemContainerLayout(){
        binding.itemTitleAlphabet.bindSubItemLayout(binding.subItemContainerAlphabet)
        binding.itemTitleSet.bindSubItemLayout(binding.subItemTitleContainer)
        binding.itemTitleTransition.bindSubItemLayout(binding.subItemContainerTransitionAdd)
        binding.itemTitleTransitions.bindSubItemLayout(binding.subItemTransitionsContainer)
        binding.itemTitleTape.bindSubItemLayout(binding.subItemContainerTape)
    }

    private fun setupEditTexts(){
        aa1 = ArrayAdapter<String>(requireContext(), R.layout.drop_down_item_1, mutableListOf())
        aa2 = ArrayAdapter<String>(requireContext(), R.layout.drop_down_item_1, mutableListOf())
        aa4 = ArrayAdapter<String>(requireContext(), R.layout.drop_down_item_1, mutableListOf())
        aa3 = ArrayAdapter(
            requireContext(),
            R.layout.drop_down_item_1,
            mutableListOf("ESQUERDA","DIREITA"))

        (binding.symbolReadMenu.editText as? AutoCompleteTextView)?.setAdapter(aa1)
        (binding.symbolWriteMenu.editText as? AutoCompleteTextView)?.setAdapter(aa1)
        (binding.fromStateMenu.editText as? AutoCompleteTextView)?.setAdapter(aa2)
        (binding.toStateMenu.editText as? AutoCompleteTextView)?.setAdapter(aa2)
        (binding.startStateMenu.editText as? AutoCompleteTextView)?.setAdapter(aa2)
        (binding.acceptStateMenu.editText as? AutoCompleteTextView)?.setAdapter(aa2)
        (binding.rejectStateMenu.editText as? AutoCompleteTextView)?.setAdapter(aa2)
        (binding.directionMenu.editText as? AutoCompleteTextView)?.setAdapter(aa3)
        (binding.emptySymbolStateMenu.editText as? AutoCompleteTextView)?.setAdapter(aa4)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.playerButtonState.value = hasClickedToCreateMachine
    }

    companion object {
        fun newInstance() = TuringMachineDefinitionFragment()
    }
}
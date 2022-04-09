package app.fs.simulator.ui.automata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.activityViewModels
import app.fs.lfa.fsm.DFA
import app.fs.simulator.R
import app.fs.simulator.base.BaseCustomDialogFragment
import app.fs.simulator.databinding.FragmentAutomataDefinitionBinding
import app.fs.simulator.databinding.TransitionListItemBinding
import app.fs.simulator.ui.compiler.MachineViewModel

class AutomatonDefinitionFragment : BaseCustomDialogFragment() {

    private var _binding : FragmentAutomataDefinitionBinding? = null
    private val binding get() = _binding!!

    private val viewModel : MachineViewModel by activityViewModels()

    private var hasClickedToCreateAutomaton = false

    private lateinit var aa1 : ArrayAdapter<String>
    private lateinit var aa2 : ArrayAdapter<String>

    var dfa = DFA<String,String>()
    val transitions : MutableList<DFA.TransitionData<String,String>> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAutomataDefinitionBinding.inflate(inflater)

        bindTitleTextViewToSubItemContainerLayout()
        setupEditTexts()

        binding.inputStatesInputEditText.setOnFocusChangeListener { _, b ->
            if(b.not()){
                var input = binding.inputStatesInputEditText.text?.toString()?.replace(" ", "")?.split(",")?.toMutableSet()
                aa1.clear()
                input?.remove("")
                input = input?.toSortedSet()
                input?.let { aa1.addAll(it) }
            }
        }

        binding.inputAlphabetInputEditText.setOnFocusChangeListener { _, b ->
            if(b.not()){
                var input = binding.inputAlphabetInputEditText.text?.toString()?.replace(" ", "")?.split(",")?.toMutableSet()
                aa2.clear()
                input?.remove("")
                input = input?.toSortedSet()
                input?.let { aa2.addAll(it) }
            }
        }

        binding.addTransitionButton.setOnClickListener {
            val transition = DFA.TransitionData(
                (binding.fromStateMenu.editText as? AutoCompleteTextView)?.text.toString(),
                (binding.symbolReadMenu.editText as? AutoCompleteTextView)?.text.toString(),
                (binding.toStateMenu.editText as? AutoCompleteTextView)?.text.toString()
            )

            val sli = TransitionListItemBinding.inflate(inflater)
            sli.removeTransitionButton.setOnClickListener {
                binding.subItemTransitionsContainer.removeView(sli.root)
                transitions.remove(transition)
            }
            sli.textView.text = transition.toString()
            binding.subItemTransitionsContainer.addView(sli.transitionItemRoot)
            transitions.add(transition)
        }

//        binding.acceptStateInputText.setOnFocusChangeListener { view, b ->
//            if(b.not()){
//                val input = binding.inputAlphabetInputEditText.text?.toString()?.replace(" ", "")?.split(",")?.toMutableSet()
//                input?.remove("")
//                input?.let { acceptStates = it }
//            }
//        }

        binding.createAutomatonButton.setOnClickListener {
            binding.inputStatesInputEditText.text?.toString()?.replace(" ", "")?.split(",")?.toList()?.let {
                dfa.addStates(it)
            }
            binding.inputAlphabetInputEditText.text?.toString()?.replace(" ","")?.split(",")?.toList()?.let {
                dfa.addAlphabet(it)
            }
            binding.startStateMenu.editText?.text?.toString()?.let {
                dfa.setStartState(it)
            }

            val input = binding.acceptStateInputText.editText?.text.toString().replace(" ", "").split(",")
                .toMutableSet()
            input.remove("")
            input.let {
                dfa.addAcceptStates(it.toList())
            }
            dfa.setupTransitions(transitions)
            viewModel.currentDfa.value = dfa
            hasClickedToCreateAutomaton = true
            dismiss()
        }


        return binding.root
    }

    private fun bindTitleTextViewToSubItemContainerLayout()
    {
        binding.itemTitleAlphabet.bindSubItemLayout(binding.subItemContainerAlphabet)
        binding.itemTitleSet.bindSubItemLayout(binding.subItemTitleContainer)
        binding.itemTitleTransition.bindSubItemLayout(binding.subItemContainerTransitionAdd)
        binding.itemTitleTransitions.bindSubItemLayout(binding.subItemTransitionsContainer)
    }

    private fun setupEditTexts(){
        aa1 = ArrayAdapter<String>(requireContext(), R.layout.drop_down_item_1, mutableListOf())
        aa2 = ArrayAdapter<String>(requireContext(), R.layout.drop_down_item_1, mutableListOf())

        (binding.fromStateMenu.editText as? AutoCompleteTextView)?.setAdapter(aa1)
        (binding.symbolReadMenu.editText as? AutoCompleteTextView)?.setAdapter(aa2)
        (binding.toStateMenu.editText as? AutoCompleteTextView)?.setAdapter(aa1)
        (binding.startStateMenu.editText as? AutoCompleteTextView)?.setAdapter(aa1)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.playerButtonState.value = hasClickedToCreateAutomaton
    }

    companion object {
        fun newInstance() = AutomatonDefinitionFragment()
    }

}













package app.fs.simulator.ui.automata

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import app.fs.lfa.fsm.DFA
import app.fs.simulator.R
import app.fs.simulator.base.BaseMachineFragment
import app.fs.simulator.custom.CustomDefocusableTextInputEditText
import app.fs.simulator.databinding.FragmentAutomataBinding
import com.gordonwong.materialsheetfab.MaterialSheetFab
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener


class AutomataFragment : BaseMachineFragment<FragmentAutomataBinding>(){


    override fun init() {
        setupFabOptions()
        setupWebViewFiniteStateMachine()
        setupPlayerButtons()

        viewModel.currentDfa.observe(viewLifecycleOwner){ dfa ->
            dfa?.let {
                if(it.isAutomatonReady()){
                    var str = it.getVizFormat()
                    str = str.replace("\n", "\\n")
                    binding.webView.evaluateJavascript("clearVisualization();", null)
                    binding.webView.evaluateJavascript("setVisualGraph('$str');", null)
                    binding.webView.evaluateJavascript("colorStartState('${it.startState.toString()}');",null)
                }

            }
        }

        viewModel.playerButtonState.observe(viewLifecycleOwner){
            if(it) {
                setupButtonNewMachineState()
            }else{
                binding.inputStringLayout.inputStringTextInputLayout.isEnabled = bottomPanelState.input
                binding.inputStringLayout.randomButton.isEnabled = bottomPanelState.randomButton
                binding.playerButtonsLayout.resetButton.isEnabled = bottomPanelState.resetButton
                binding.playerButtonsLayout.previousButton.isEnabled = bottomPanelState.prevButton
                binding.playerButtonsLayout.playButton.isEnabled = bottomPanelState.playPauseButton
                binding.playerButtonsLayout.nextButton.isEnabled = bottomPanelState.nextButton
                binding.playerButtonsLayout.playAllButton.isEnabled = bottomPanelState.playAllButton
            }
        }

        binding.inputStringLayout.inputStringTextInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
            var str = text.toString()
            viewModel.currentDfa.value?.let { dfa ->
                dfa.alphabet.forEach { input ->
                    str = str.replace(input, "")
                }
                //TODO: disable play button if error
                if(str.isNotEmpty()){
                    binding.inputStringLayout.inputStringTextInputLayout.error = "Cadeia invalida"
                }else{
                    binding.inputStringLayout.inputStringTextInputLayout.error = null
                }
            }
        }

    }

    private fun setupFabOptions(){
        customFabWithMenu = MaterialSheetFab(binding.fab,
            binding.fabSheet,
            binding.overlay,
            ContextCompat.getColor(requireContext(), R.color.secondaryColor),
            ContextCompat.getColor(requireContext(), R.color.secondaryColor))

        customFabWithMenu.setEventListener(object : MaterialSheetFabEventListener() {
            override fun onShowSheet() {

                bottomPanelState.input = binding.inputStringLayout.inputStringTextInputLayout.isEnabled
                bottomPanelState.randomButton = binding.inputStringLayout.randomButton.isEnabled
                bottomPanelState.resetButton = binding.playerButtonsLayout.resetButton.isEnabled
                bottomPanelState.prevButton = binding.playerButtonsLayout.previousButton.isEnabled
                bottomPanelState.playPauseButton = binding.playerButtonsLayout.playButton.isEnabled
                bottomPanelState.nextButton = binding.playerButtonsLayout.nextButton.isEnabled
                bottomPanelState.playAllButton = binding.playerButtonsLayout.playAllButton.isEnabled

                binding.playerButtonsLayout.previousButton.isEnabled = false
                binding.playerButtonsLayout.nextButton.isEnabled = false
                binding.playerButtonsLayout.playAllButton.isEnabled = false
                binding.playerButtonsLayout.resetButton.isEnabled = false
                binding.playerButtonsLayout.playButton.isEnabled = false
                binding.inputStringLayout.randomButton.isEnabled = false
                binding.inputStringLayout.inputStringTextInputLayout.isEnabled = false
            }

            override fun onSheetShown() {
                // Called when the material sheet's "show" animation ends.
            }

            override fun onHideSheet() {
                if(fabMenuItemSelected.not()){
                    binding.inputStringLayout.inputStringTextInputLayout.isEnabled = bottomPanelState.input
                    binding.inputStringLayout.randomButton.isEnabled = bottomPanelState.randomButton
                    binding.playerButtonsLayout.resetButton.isEnabled = bottomPanelState.resetButton
                    binding.playerButtonsLayout.previousButton.isEnabled = bottomPanelState.prevButton
                    binding.playerButtonsLayout.playButton.isEnabled = bottomPanelState.playPauseButton
                    binding.playerButtonsLayout.nextButton.isEnabled = bottomPanelState.nextButton
                    binding.playerButtonsLayout.playAllButton.isEnabled = bottomPanelState.playAllButton
                }
                fabMenuItemSelected = false
            }

            override fun onSheetHidden() {
            }
        })

        binding.newClickableTextView.setOnClickListener {
            fabMenuItemSelected = true
            AutomatonDefinitionFragment.newInstance().show(childFragmentManager, "TuringMachineDefinitionFragment")
            customFabWithMenu.hideSheet()
        }

        binding.newUsingReClickableTextView.setOnClickListener {

        }

        binding.examplesClickableTextView.setOnClickListener {
            fabMenuItemSelected = true
            AutomataExamplesFragment().newInstance().show(childFragmentManager, "TuringMachineExamplesFragment")
            customFabWithMenu.hideSheet()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebViewFiniteStateMachine(){
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.allowFileAccess = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.displayZoomControls = false
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.useWideViewPort = true
        binding.webView.webViewClient = object : WebViewClient(){
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                Log.e("WEBVIEW", error?.description.toString() + " ${request?.url}")
            }
        }

        viewModel.webData.observe(viewLifecycleOwner){
            binding.webView.loadDataWithBaseURL(
                viewModel.baseURL,
                it,
                "text/html",
                "utf-8", null)
        }
    }

    private fun setupPlayerButtons(){
        binding.playerButtonsLayout.nextButton.setOnClickListener {
            viewModel.currentDfa.value?.let { dfa ->
                dfa.nextStep()?.let {
                    val now = it.whereItsNow
                    val was = it.whereItWas
                    binding.webView.evaluateJavascript(
                        "colorVisualizationNextStep('$was','$now');",
                        null
                    )
                    binding.inputStringLayout.inputStringTextInputLayout.editText?.let { tx ->
                        (tx as CustomDefocusableTextInputEditText).inputString.next()
                    }
                }
            }
        }

        binding.playerButtonsLayout.previousButton.setOnClickListener {
            viewModel.currentDfa.value?.let { dfa ->
                dfa.previousStep()?.let { pd ->
                    binding.webView.evaluateJavascript(
                        "colorVisualizationNextStep('${pd.was}','${pd.now}');",
                        null
                    )
                    binding.inputStringLayout.inputStringTextInputLayout.editText?.let { tx ->
                        (tx as CustomDefocusableTextInputEditText).inputString.prev()
                    }
                }
            }
        }

        binding.playerButtonsLayout.playButton.setOnClickListener {
            playButtonIsPlay = if(playButtonIsPlay){
                setButtonsSetupPauseState()
                (it as ImageView).setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
                viewModel.currentDfa.value?.let { dfa ->
                    resetAutomata(dfa)
                }
//                binding.inputStringLayout.inputStringTextInputLayout.editText?.let { tx ->
//                    (tx as CustomDefocusableTextInputEditText).inputString.reset()
//                }
                false
            }else{
                setButtonsSetupPlayState()
                (it as ImageView).setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)
                binding.inputStringLayout.inputStringTextInputLayout.editText?.let{ tx ->
                    if(tx.text.isEmpty()) return@let
                    viewModel.currentDfa.value?.let { dfa ->
                        val pair: MutableList<Triple<Int, Int, String>> = mutableListOf()
                        dfa.alphabet.forEach { str ->
                            var index = 0
                            do {
                                index = tx.text.indexOf(str, index)
                                if (index != -1) {
                                    val prev = index
                                    index += str.length
                                    pair.add(Triple(prev, index,str))
                                }
                            } while (index != -1)
                        }
                        pair.sortBy { value -> value.first }
                        val plist = mutableListOf<Pair<Int,Int>>()
                        val list = List(pair.size){ i ->
                            plist.add(Pair(pair[i].first, pair[i].second))
                            pair[i].third
                        }
                        dfa.addInput(list)
                        (tx as CustomDefocusableTextInputEditText).inputString.setIndexAndText(plist, tx.text.toString())
                    }
                }
                true
            }
        }

        binding.playerButtonsLayout.resetButton.setOnClickListener {
            viewModel.currentDfa.value?.let { dfa ->
                resetAutomata(dfa)
            }
        }

        binding.playerButtonsLayout.playAllButton.setOnClickListener {
            viewModel.currentDfa.value?.let { dfa ->
                var step: DFA.CurrentStateData<String, String>?
                while(dfa.hasInput()) {
                    step = dfa.nextStep()
                    binding.webView.evaluateJavascript(
                        "colorVisualizationNextStep('${step?.whereItWas}','${step?.whereItsNow}');",
                        null
                    )
                }
                binding.inputStringLayout.inputStringTextInputLayout.editText?.let { tx ->
                    (tx as CustomDefocusableTextInputEditText).inputString.end()
                }
            }
        }
    }

    private fun resetAutomata(dfa : DFA<String, String>){
        var prev: DFA.PreviousData<String, String>?
        do {
            prev = dfa.previousStep()
            prev?.let { pd ->
                binding.webView.evaluateJavascript(
                    "colorVisualizationNextStep('${pd.was}','${pd.now}');",
                    null
                )
            }
        }while(prev != null)
        binding.inputStringLayout.inputStringTextInputLayout.editText?.let { tx ->
            (tx as CustomDefocusableTextInputEditText).inputString.start()
        }
    }

    private fun setButtonsSetupPlayState(){
        binding.playerButtonsLayout.previousButton.isEnabled = true
        binding.playerButtonsLayout.nextButton.isEnabled = true
        binding.playerButtonsLayout.playAllButton.isEnabled = true
        binding.playerButtonsLayout.resetButton.isEnabled = true
        binding.inputStringLayout.randomButton.isEnabled = false
        binding.inputStringLayout.inputStringTextInputLayout.isEnabled = false
    }

    private fun setButtonsSetupPauseState(){
        binding.playerButtonsLayout.previousButton.isEnabled = false
        binding.playerButtonsLayout.nextButton.isEnabled = false
        binding.playerButtonsLayout.playAllButton.isEnabled = false
        binding.playerButtonsLayout.resetButton.isEnabled = false
        binding.inputStringLayout.randomButton.isEnabled = true
        binding.inputStringLayout.inputStringTextInputLayout.isEnabled = true
    }

    private fun setupButtonNewMachineState(){
        binding.playerButtonsLayout.previousButton.isEnabled = false
        binding.playerButtonsLayout.nextButton.isEnabled = false
        binding.playerButtonsLayout.playButton.isEnabled = true
        playButtonIsPlay = false
        binding.playerButtonsLayout.playButton.setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
        binding.playerButtonsLayout.playAllButton.isEnabled = false
        binding.playerButtonsLayout.resetButton.isEnabled = false
        binding.inputStringLayout.randomButton.isEnabled = true
        binding.inputStringLayout.inputStringTextInputLayout.isEnabled = true
        binding.inputStringLayout.inputStringTextInputLayout.editText?.text = null
    }

    override fun onBackPressed() {
        if(customFabWithMenu.isSheetVisible){
            customFabWithMenu.hideSheet()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.currentDfa.value = null
    }

    override fun setupViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAutomataBinding {
        return FragmentAutomataBinding.inflate(layoutInflater, container, false)
    }

}


//fun main(){
//    val enfa = ENFA<String,String>("ε")
//    enfa.addStates("A,B,C,D,E,F".split(","))
//    enfa.setStartState("A")
//    enfa.addAlphabet("0".split(","))
//    enfa.addAcceptStates(listOf("B,C"))
//
//    val transitions = listOf(
//        ENFA.TransitionData("A", enfa.emptySymbol, "B"),
//        ENFA.TransitionData("A", enfa.emptySymbol, "C"),
//        ENFA.TransitionData("B", "0", "F"),
//        ENFA.TransitionData("C", "0", "E"),
//        ENFA.TransitionData("D", "0", "C"),
//        ENFA.TransitionData("E", "0", "D"),
//        ENFA.TransitionData("F", "1", "B"))
//
//    enfa.setupTransitions(transitions)
//
//    val gson = Gson()
//
//    val gstr = gson.toJson(enfa)
//
//    val str = enfa.getVizFormat()
//
//
//}

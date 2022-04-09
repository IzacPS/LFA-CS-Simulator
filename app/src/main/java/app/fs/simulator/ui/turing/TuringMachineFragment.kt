package app.fs.simulator.ui.turing

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import app.fs.lfa.tm.TuringMachine
import app.fs.simulator.R
import app.fs.simulator.base.BaseMachineFragment
import app.fs.simulator.databinding.FragmentTuringMachineBinding
import app.fs.simulator.viewholder.TapeSquareViewHolder
import com.gordonwong.materialsheetfab.MaterialSheetFab
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener
import smartadapter.SmartRecyclerAdapter
import kotlin.math.roundToInt


class TuringMachineFragment : BaseMachineFragment<FragmentTuringMachineBinding>(){

    private var tapeRecyclerViewFirstPositionOffset : Int = 0

    override fun init() {

        setupFabOptions()
        setupWebViewStateDiagram()
        setupTapeRecyclerView()
        setupPlayerButtons()

        viewModel.currentMachine.observe(viewLifecycleOwner){ machine ->
            machine?.let {
                if(it.isMachineReady()) {
                    //TODO: remove this hard entered input
                    var str = it.getVizFormat()
                    str = str.replace("\n", "\\n")
                    binding.webView.evaluateJavascript("clearVisualization();", null)
                    binding.webView.evaluateJavascript("setVisualGraph('$str');", null)
                    binding.webView.evaluateJavascript("colorStartState('${it.startState.toString()}');",null)
                    viewModel.tapeView.indices.forEach { i ->
                        viewModel.tapeView[i] = it.emptySymbol!!
                        binding.tapeRecyclerView.adapter?.notifyItemChanged(i)
                    }
                    binding.tapeRecyclerView.scrollToPosition(0)
                    binding.tapeRecyclerView.smoothScrollBy(tapeRecyclerViewFirstPositionOffset, 0)
                }
            }
        }

        binding.inputStringLayout.inputStringTextInputLayout.editText?.doOnTextChanged { text, _, _, _ ->
            var str = text.toString()
            viewModel.currentMachine.value?.let { machine ->
                machine.inputAlphabet.forEach { input ->
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

        binding.inputStringLayout.inputStringTextInputLayout.isEnabled = false

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
            TuringMachineDefinitionFragment.newInstance().show(childFragmentManager, "TuringMachineDefinitionFragment")
            customFabWithMenu.hideSheet()
        }

        binding.examplesClickableTextView.setOnClickListener {
            fabMenuItemSelected = true
            TuringMachineExamplesFragment.newInstance().show(childFragmentManager, "TuringMachineExamplesFragment")
            customFabWithMenu.hideSheet()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebViewStateDiagram(){

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

    private fun setupTapeRecyclerView(){
        SmartRecyclerAdapter
            .items(viewModel.tapeView)
            .map(String::class, TapeSquareViewHolder::class)
            .setLayoutManager(object : LinearLayoutManager(requireContext(),  LinearLayoutManager.HORIZONTAL, false){
                override fun canScrollHorizontally(): Boolean {
                    return true
                }

                override fun smoothScrollToPosition(
                    recyclerView: RecyclerView?,
                    state: RecyclerView.State?,
                    position: Int
                ) {
                    val linearSmoothScroller = object : LinearSmoothScroller(requireContext()){
                        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics?): Float {
                            //1F milliseconds per inch
                            return 1F / displayMetrics!!.densityDpi
                        }
                    }

                    linearSmoothScroller.targetPosition = position
                    startSmoothScroll(linearSmoothScroller)
                }
            }.apply {
                isItemPrefetchEnabled = true
                initialPrefetchItemCount = 20 })
            .into<SmartRecyclerAdapter>(binding.tapeRecyclerView)

        binding.tapeRecyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return true
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

            }

        })

    }

    private fun getTapeFirstPositionOffset(){
        val tapeVisibleFrameMiddleX = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity?.windowManager?.currentWindowMetrics?.bounds?.width() ?: 0
        } else {
            @Suppress("DEPRECATION")
            activity?.windowManager?.defaultDisplay?.width ?: 0
        }

        val middle = tapeVisibleFrameMiddleX.toFloat() / 2F
        val tapeHeadItemOffset = dpToPx(44)

        val proportion = middle / tapeHeadItemOffset.toFloat()
        var x = proportion - proportion.toInt().toFloat()
        x = 1F - x
        val xTapeHeadItemOffset = (x * tapeHeadItemOffset.toFloat()).roundToInt()
        tapeRecyclerViewFirstPositionOffset = xTapeHeadItemOffset + (tapeHeadItemOffset / 2)
    }

    override fun onResume() {
        super.onResume()
        getTapeFirstPositionOffset()
        binding.tapeRecyclerView.scrollToPosition(0)
        binding.tapeRecyclerView.smoothScrollBy(tapeRecyclerViewFirstPositionOffset, 0)
    }

    private fun setupPlayerButtons(){
        binding.playerButtonsLayout.nextButton.setOnClickListener {
            if(binding.tapeRecyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                viewModel.currentMachine.value?.let {
                    it.nextStep()?.let { step ->
                        val curState = step.whereItWas
                        val nextState = step.whereItsNow
                        binding.webView.evaluateJavascript(
                            "colorVisualizationNextStep('$curState','$nextState');",
                            null
                        )
                        viewModel.tapeView[step.headPosition + 5] = step.symbolWrote!!
                        binding.tapeRecyclerView.adapter?.notifyItemChanged(step.headPosition + 5)
                        step.headDirection?.let { dir -> moveHead(dir) }
                    }
                }
            }
        }

        binding.playerButtonsLayout.previousButton.setOnClickListener {
            if(binding.tapeRecyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
                viewModel.currentMachine.value?.let {
                    it.previousStep().let { prev ->
                        prev?.let { p ->
                            val curState = p.was
                            val prevState = p.now
                            binding.webView.evaluateJavascript(
                                "colorVisualizationNextStep('$curState','$prevState');",
                                null
                            )
                            viewModel.tapeView[p.headPosition + 5] = p.previousHeadValue
                            binding.tapeRecyclerView.adapter?.notifyItemChanged(p.headPosition + 5)
                            moveHead(p.previousHeadDirection)
                        }
                    }
                }
            }
        }

        binding.playerButtonsLayout.playButton.setOnClickListener {
            playButtonIsPlay = if(playButtonIsPlay){
                setButtonsSetupPauseState()
                (it as ImageView).setImageResource(R.drawable.ic_baseline_play_circle_filled_24)
                viewModel.currentMachine.value?.let { machine ->
                    resetMachine(machine)
                }
                false
            }else{
                setButtonsSetupPlayState()
                (it as ImageView).setImageResource(R.drawable.ic_baseline_pause_circle_filled_24)
                true
            }
            binding.inputStringLayout.inputStringTextInputLayout.editText?.let { tx ->
                if(tx.text.isEmpty()) return@let
                viewModel.currentMachine.value?.let { machine ->
                    val pair : MutableList<Pair<Int, String>> = mutableListOf()
                    machine.inputAlphabet.forEach { str ->
                        var index = 0
                        do {
                            index = tx.text.indexOf(str, index)
                            if(index != -1) {
                                index += str.length
                                pair.add(Pair(index, str))
                            }
                        }while (index != -1)
                    }
                    pair.sortBy { value -> value.first }
                    val list = List(pair.size){ i ->
                        pair[i].second
                    }
                    machine.addInput(list)
                    list.forEachIndexed { index, s ->
                        viewModel.tapeView[index + 5] = s
                        binding.tapeRecyclerView.adapter?.notifyItemChanged(index + 5)
                    }
                }
            }
        }

        binding.playerButtonsLayout.resetButton.setOnClickListener {
            viewModel.currentMachine.value?.let { machine ->
                resetMachine(machine)
            }
        }

        binding.playerButtonsLayout.playAllButton.setOnClickListener {
            var step : TuringMachine.CurrentStateData<String,String>? = null
            viewModel.currentMachine.value?.let { machine ->
                while(machine.hasMachineHalted().not()){
                    step = machine.nextStep()
                    binding.webView.evaluateJavascript(
                        "colorVisualizationNextStep('${step?.whereItWas}','${step?.whereItsNow}');",
                        null
                    )
                }
                step?.let { s ->
                    machine.tape.forEachIndexed { index, st ->
                        viewModel.tapeView[index + 5] = st.toString()
                        binding.tapeRecyclerView.adapter?.notifyItemChanged(index + 5)
                    }
                    binding.tapeRecyclerView.scrollToPosition(0)
                    val pos = if(s.headDirection == TuringMachine.HeadDirection.DIREITA) s.headPosition + 1 else s.headPosition - 1
                    binding.tapeRecyclerView.smoothScrollBy(tapeRecyclerViewFirstPositionOffset + 116 * pos, 0)
                }
            }
        }

    }

    private fun resetMachine(machine : TuringMachine<String,String>){
        var step: TuringMachine.PreviousData<String, String>?
        do{
            step = machine.previousStep()
            binding.webView.evaluateJavascript(
                "colorVisualizationNextStep('${step?.was}','${step?.now}');",
                null
            )
        }while(step != null)
        machine.tape.forEachIndexed { index, s ->
            viewModel.tapeView[index + 5] = s.toString()
            binding.tapeRecyclerView.adapter?.notifyItemChanged(index + 5)
        }
        binding.tapeRecyclerView.scrollToPosition(0)
        binding.tapeRecyclerView.smoothScrollBy(tapeRecyclerViewFirstPositionOffset, 0)
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
    }

    private fun moveHead(direction : TuringMachine.HeadDirection){
        when(direction){
            TuringMachine.HeadDirection.ESQUERDA -> binding.tapeRecyclerView.smoothScrollBy(-116, 0)
            TuringMachine.HeadDirection.DIREITA -> binding.tapeRecyclerView.smoothScrollBy(116, 0)
            else -> {}
        }
    }

    private fun dpToPx(dp: Int): Int {
        return Math.round(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                resources.displayMetrics))
    }

    override fun onBackPressed() {
        if(customFabWithMenu.isSheetVisible){
            customFabWithMenu.hideSheet()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.currentMachine.value = null
        (0 until viewModel.tapeView.size).forEach {
            viewModel.tapeView[it] = ""
        }
    }

    override fun setupViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTuringMachineBinding {
        return FragmentTuringMachineBinding.inflate(inflater, container, false)
    }
}
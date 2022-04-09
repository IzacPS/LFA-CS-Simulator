package app.fs.simulator.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import app.fs.simulator.custom.CustomExtendedFabWithMenu
import app.fs.simulator.interfaces.IButtonPressed
import app.fs.simulator.ui.compiler.MachineViewModel
import com.gordonwong.materialsheetfab.MaterialSheetFab

abstract class BaseMachineFragment<VB : ViewBinding> : Fragment(), IButtonPressed {

    private var _binding: VB? = null
    protected val binding get() = _binding!!
    protected val viewModel : MachineViewModel by activityViewModels()
    protected lateinit var customFabWithMenu: MaterialSheetFab<CustomExtendedFabWithMenu>
    protected val bottomPanelState = BottomPlayerPanelState()
    protected var fabMenuItemSelected = false
    protected var playButtonIsPlay = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = setupViewBinding(inflater,container)
        init()
        return binding.root
    }

    abstract fun setupViewBinding(inflater: LayoutInflater, container: ViewGroup?) : VB

    abstract fun init()

    override fun onBackPressed() {
        if(customFabWithMenu.isSheetVisible){
            customFabWithMenu.hideSheet()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.playerButtonState.value = false
        _binding = null
    }

    data class BottomPlayerPanelState(
        var input: Boolean = false,
        var randomButton : Boolean = false,
        var resetButton : Boolean = false,
        var prevButton : Boolean = false,
        var playPauseButton : Boolean = false,
        var nextButton : Boolean = false,
        var playAllButton : Boolean = false)

}
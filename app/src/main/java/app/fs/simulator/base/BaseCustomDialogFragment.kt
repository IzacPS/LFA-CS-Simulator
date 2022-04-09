package app.fs.simulator.base

import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

open class BaseCustomDialogFragment : DialogFragment() {

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}
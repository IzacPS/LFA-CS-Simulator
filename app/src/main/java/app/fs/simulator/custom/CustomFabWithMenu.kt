package app.fs.simulator.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.gordonwong.materialsheetfab.AnimatedFab

class CustomExtendedFabWithMenu : ExtendedFloatingActionButton, AnimatedFab {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context,
                attrs: AttributeSet?,
                defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun show() {
        show(0F, 0F)
    }

    override fun show(translationX: Float, translationY: Float) {
        // NOTE: Using the parameters is only needed if you want
        // to support moving the FAB around the screen.
        // NOTE: This immediately hides the FAB. An animation can
        // be used instead - see the sample app.
        visibility = View.VISIBLE;
    }

    override fun hide() {
        // NOTE: This immediately hides the FAB. An animation can
        // be used instead - see the sample app.
        visibility = View.INVISIBLE;
    }
}
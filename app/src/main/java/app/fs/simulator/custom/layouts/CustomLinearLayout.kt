package app.fs.simulator.custom.layouts

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import app.fs.simulator.custom.listeners.OnSwipeTouchListener

class CustomLinearLayout : LinearLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context,
                attrs: AttributeSet?,
                defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        setOnTouchListener(object : OnSwipeTouchListener(context){
            override fun onSwipeRight() {
                super.onSwipeRight()
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
            }
        })
    }
}
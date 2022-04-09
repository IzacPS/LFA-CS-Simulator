package app.fs.simulator.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import app.fs.simulator.R

class SelectableHeaderTextView : androidx.appcompat.widget.AppCompatTextView {

    private var layout : LinearLayout? = null
    private var expanded = true

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context,
                    attrs: AttributeSet?,
                    defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_expand_less_24, 0)
    }

    fun bindSubItemLayout(l : LinearLayout){
        layout = l
        setOnClickListener {
            if(expanded){
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_expand_more_24, 0)
                layout?.visibility = View.GONE
                expanded = false
            }else{
                setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_expand_less_24, 0)
                layout?.visibility = View.VISIBLE
                expanded = true
            }
        }

    }

}
package app.fs.simulator.custom

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.core.text.clearSpans
import com.google.android.material.textfield.TextInputEditText

class CustomDefocusableTextInputEditText : TextInputEditText {

    var inputString : InputString = InputString()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context,
                attrs: AttributeSet?,
                defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_UP){
            clearFocus()
            return false
        }
        return super.onKeyPreIme(keyCode, event)
    }

    inner class InputString{
        private var position = 0

        private lateinit var inputPosition : MutableList<Pair<Int,Int>>
        private var originalStr : String? = null

        private lateinit var spannable : SpannableString

        fun setIndexAndText(index : MutableList<Pair<Int,Int>>, text : String){
            position = 0
            inputPosition = index
            originalStr = text
            spannable = SpannableString(text)
            val pair = inputPosition[position++]
            spannable.clearSpans()
            spannable.setSpan(
                BackgroundColorSpan(Color.GRAY),
                pair.first,
                pair.second,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            setText(spannable)
        }

        fun reset(){
            spannable.clearSpans()
            setText(spannable)
        }

        fun getModifiedText() : SpannableString { return spannable }

        fun next(){
            if(position >= spannable.length) {
                spannable.clearSpans()
                setText(spannable)
                return
            }else {
                val pair = inputPosition[position++]
                spannable.clearSpans()
                spannable.setSpan(
                    BackgroundColorSpan(Color.GRAY),
                    pair.first,
                    pair.second,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                setText(spannable)
            }
        }

        fun prev(){
            if(position < 0) {
                spannable.clearSpans()
                setText(spannable)
            }else {
                val pair = inputPosition[--position]
                if(position == 0) position++
                spannable.clearSpans()
                spannable.setSpan(
                    BackgroundColorSpan(Color.GRAY),
                    pair.first,
                    pair.second,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                setText(spannable)
            }
        }

        fun start(){
            position = 0
            val pair = inputPosition[position++]
            spannable.clearSpans()
            spannable.setSpan(
                BackgroundColorSpan(Color.GRAY),
                pair.first,
                pair.second,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            setText(spannable)
        }

        fun end(){
            position = inputPosition.size
            spannable.clearSpans()
            setText(spannable)
        }
    }
}
package app.fs.simulator.viewholder

import android.view.ViewGroup
import android.widget.TextView
import app.fs.simulator.R
import smartadapter.viewholder.SmartViewHolder

class TapeSquareViewHolder(parentView : ViewGroup) :
    SmartViewHolder<String>(parentView, R.layout.tape_square_item) {

    override fun bind(item: String) {
        itemView.findViewById<TextView>(R.id.tape_square_text_view).text = item
    }

}
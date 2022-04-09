//package app.fs.simulator.viewholder
//
//import android.view.View
//import android.view.ViewGroup
//import android.widget.LinearLayout
//import android.widget.TextView
//import app.fs.simulator.R
//import app.fs.simulator.data.TransitionFunctionData
//import smartadapter.viewevent.model.ViewEvent
//import smartadapter.viewevent.viewholder.OnItemSelectedEventListener
//import smartadapter.viewholder.SmartViewHolder
//
//class TransitionsExpandableItemViewHolder (parentView : ViewGroup) :
//    SmartViewHolder<TransitionFunctionData>(parentView, R.layout.transitions_expandable_item),
//    OnItemSelectedEventListener {
//
//    private val title: TextView = itemView.findViewById(R.id.itemTitle)
//    private val subItem: LinearLayout = itemView.findViewById(R.id.subItemContainer)
//
//    override fun bind(item: TransitionFunctionData) {
//
//    }
//
//    override fun onItemSelect(event: ViewEvent.OnItemSelected) {
//        when (event.isSelected) {
//            true -> {
//                title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_expand_less_24, 0)
//                subItem.visibility = View.VISIBLE
//            }
//            false -> {
//                title.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_expand_more_24, 0)
//                subItem.visibility = View.GONE
//            }
//        }
//    }
//}
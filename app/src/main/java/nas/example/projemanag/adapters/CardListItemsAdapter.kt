package nas.example.projemanag.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nas.example.projemanag.R
import nas.example.projemanag.activities.TaskListActivity
import nas.example.projemanag.models.Card
import nas.example.projemanag.models.SelectedMembers

open class CardListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Card>
) : RecyclerView.Adapter<CardListItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        if (model.labelColor.isNotEmpty()) {
            holder.viewLabelColor.visibility = View.VISIBLE
            holder.viewLabelColor.setBackgroundColor(Color.parseColor(model.labelColor))
        } else {
            holder.viewLabelColor.visibility = View.GONE
        }
        holder.bind(model)
        if((context as TaskListActivity).mAssignedMembersDetailList.size>0){
            val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()
            for(i in context.mAssignedMembersDetailList.indices){
                for (j in model.assignedTo){
                    if(context.mAssignedMembersDetailList[i].id == j){
                        val selectedMembers = SelectedMembers(
                            context.mAssignedMembersDetailList[i].id,
                            context.mAssignedMembersDetailList[i].image
                        )
                        selectedMembersList.add(selectedMembers)
                    }
                }
            }
            if(selectedMembersList.size>0){
                if(selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy){
                    holder.rvCardSelectedMembersList.visibility = View.GONE
                }else{
                    holder.rvCardSelectedMembersList.visibility = View.VISIBLE
                    holder.rvCardSelectedMembersList.layoutManager = GridLayoutManager(context, 4)
                    val adapter = CardMemberListItemsAdapter(context, selectedMembersList, false)
                    holder.rvCardSelectedMembersList.adapter  = adapter
                    //adapter.setOnClickListener(object: CardMemberListItemsAdapter.OnClickListener{
                    holder.itemView.setOnClickListener {
                        if (onClickListener != null) {
                            onClickListener!!.onClick(holder.adapterPosition)
                        }
                    }
                }
            }else{
                holder.rvCardSelectedMembersList.visibility = View.GONE
            }
        }


        holder.itemView.setOnClickListener {
            if (onClickListener != null) {
                onClickListener!!.onClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvCardName: TextView = view.findViewById(R.id.tv_card_name)
        val viewLabelColor: View = view.findViewById(R.id.view_label_color)
        val rvCardSelectedMembersList: RecyclerView = view.findViewById(R.id.rv_card_selected_members_list)

        fun bind(model: Card) {
            tvCardName.text = model.name
        }
    }
}
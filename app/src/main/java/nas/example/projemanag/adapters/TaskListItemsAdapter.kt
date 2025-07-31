package nas.example.projemanag.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import nas.example.projemanag.R
import nas.example.projemanag.activities.TaskListActivity
import nas.example.projemanag.models.Task
import java.util.Collections

open class TaskListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Task>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)
        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {

            if (position == list.size - 1) {
                holder.tvAddTaskList.visibility = View.VISIBLE
                holder.llTaskItem.visibility = View.GONE
            } else {
                holder.tvAddTaskList.visibility = View.GONE
                holder.llTaskItem.visibility = View.VISIBLE
            }

            holder.tvTaskListTitle.text = model.title

            holder.tvAddTaskList.setOnClickListener {

                holder.tvAddTaskList.visibility = View.GONE
                holder.cvAddTaskListName.visibility = View.VISIBLE
            }

            holder.ibCloseListName.setOnClickListener {
                holder.tvAddTaskList.visibility = View.VISIBLE
                holder.cvAddTaskListName.visibility = View.GONE
            }

            holder.ibDoneListName.setOnClickListener {
                val listName = holder.etTaskListName.text.toString()

                if (listName.isNotEmpty()) {

                    if (context is TaskListActivity) {
                        context.createTaskList(listName)
                    }
                } else {
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }
            }

            holder.ibEditListName.setOnClickListener {

                holder.etEditTaskListName.setText(model.title) // Set the existing title
                holder.llTitleView.visibility = View.GONE
                holder.cvEditTaskListName.visibility = View.VISIBLE
            }

            holder.ibCloseEditableView.setOnClickListener {
                holder.llTitleView.visibility = View.VISIBLE
                holder.cvEditTaskListName.visibility = View.GONE
            }

            holder.ibDoneEditListName.setOnClickListener {
                val listName = holder.etEditTaskListName.text.toString()
                if (listName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.updateTaskList(holder.adapterPosition, listName, model) // Use holder.adapterPosition
                    }
                } else {
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }
            }
            holder.ibDeleteList.setOnClickListener {
                alertDialogForDeleteList(holder.adapterPosition, model.title) // Use holder.adapterPosition
            }

            holder.tvAddCard.setOnClickListener{
                holder.tvAddCard.visibility = View.GONE
                holder.cvAddCard.visibility = View.VISIBLE
            }
            holder.ibCloseCardName.setOnClickListener{
                holder.tvAddCard.visibility = View.VISIBLE
                holder.cvAddCard.visibility = View.GONE
            }

            holder.ibDoneCardName.setOnClickListener {
                val cardName = holder.etCardName.text.toString()

                if (cardName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.addCardToTaskList(position, cardName)
                    }
                } else {
                    Toast.makeText(context, "Please Enter a Card Name.", Toast.LENGTH_SHORT).show()
                }
            }

            holder. rvCardList.layoutManager = LinearLayoutManager(context)
            holder.rvCardList.setHasFixedSize(true)
            val adapter = CardListItemsAdapter(context, model.cards)
            holder.rvCardList.adapter = adapter
            adapter.setOnClickListener(object : CardListItemsAdapter.OnClickListener {
                override fun onClick(cardPosition: Int) {
                    if (context is TaskListActivity) {
                        context.cardDetails(holder.adapterPosition, cardPosition)
                    }
                }
            })

            val dividerItemDecoration = DividerItemDecoration(context,DividerItemDecoration.VERTICAL)
            holder.rvCardList.addItemDecoration(dividerItemDecoration)
            // Other code...
            val helper = ItemTouchHelper(
                object: ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
                ) {
                    override fun onMove(
                        recyclerView: RecyclerView,
                        dragged: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        val draggedPosition = dragged.adapterPosition
                        val targetPosition = target.adapterPosition
                        if (mPositionDraggedFrom == -1) {
                            mPositionDraggedFrom = draggedPosition // Corrected assignment
                        }
                        mPositionDraggedTo = targetPosition
                        Collections.swap(list[holder.adapterPosition].cards, draggedPosition, targetPosition) // Use holder.adapterPosition
                        adapter.notifyItemMoved(draggedPosition, targetPosition)
                        return true // Return true to indicate the move was handled
                    }
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    }

                    override fun clearView(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder
                    ) {
                        super.clearView(recyclerView, viewHolder)
                        if (mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 &&
                            mPositionDraggedFrom != mPositionDraggedTo) {
                            (context as TaskListActivity).updateCardsInTasklist(
                                holder.adapterPosition, // Use holder.adapterPosition
                                list[holder.adapterPosition].cards // Use holder.adapterPosition
                            )
                        }
                        mPositionDraggedFrom = -1
                        mPositionDraggedTo = -1
                    }
                }
            )
            helper.attachToRecyclerView(holder.rvCardList)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun Int.toDp(): Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }

        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAddTaskList = view.findViewById<TextView>(R.id.tv_add_task_list)
        val llTaskItem = view.findViewById<LinearLayout>(R.id.ll_task_item)
        val tvTaskListTitle = view.findViewById<TextView>(R.id.tv_task_list_title)
        val cvAddTaskListName = view.findViewById<CardView>(R.id.cv_add_task_list_name)
        val ibCloseListName = view.findViewById<ImageButton>(R.id.ib_close_list_name)
        val ibDoneListName = view.findViewById<ImageButton>(R.id.ib_done_list_name)
        val etTaskListName = view.findViewById<EditText>(R.id.et_task_list_name)
        val ibEditListName = view.findViewById<ImageButton>(R.id.ib_edit_list_name)
        val etEditTaskListName = view.findViewById<EditText>(R.id.et_edit_task_list_name)
        val llTitleView = view.findViewById<LinearLayout>(R.id.ll_title_view)
        val cvEditTaskListName = view.findViewById<CardView>(R.id.cv_edit_task_list_name)
        val ibCloseEditableView = view.findViewById<ImageButton>(R.id.ib_close_editable_view)
        val ibDeleteList = view.findViewById<ImageButton>(R.id.ib_delete_list)
        val ibDoneEditListName = view.findViewById<ImageButton>(R.id.ib_done_edit_list_name)
        val tvAddCard = view.findViewById<TextView>(R.id.tv_add_card)
        val cvAddCard = view.findViewById<CardView>(R.id.cv_add_card)
        val ibCloseCardName = view.findViewById<ImageButton>(R.id.ib_close_card_name)
        val ibDoneCardName = view.findViewById<ImageButton>(R.id.ib_done_card_name)
        val etCardName = view.findViewById<EditText>(R.id.et_card_name)
        val rvCardList = view.findViewById<RecyclerView>(R.id.rv_card_list)
        //val etCardName = view.findViewById<EditText>(R.id.et_card_name)
        //val etCardName = view.findViewById<EditText>(R.id.et_card_name)
        //val etCardName = view.findViewById<EditText>(R.id.et_card_name)
        //val etCardName = view.findViewById<EditText>(R.id.et_card_name)
    }
}

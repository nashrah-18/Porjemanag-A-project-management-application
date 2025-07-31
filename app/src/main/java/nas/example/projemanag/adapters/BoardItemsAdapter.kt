package nas.example.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import nas.example.projemanag.R
import nas.example.projemanag.models.Board

open class BoardItemsAdapter(private val context: Context, private var list: List<Board>) :
    RecyclerView.Adapter<BoardItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_board, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            Glide.with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.ivBoardImage)

            holder.tvName.text = model.name
            holder.tvCreatedBy.text = "Created By : ${model.createdBy}"

            holder.itemView.setOnClickListener {
                onClickListener?.onClick(position, model)
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
        fun onClick(position: Int, model: Board)
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivBoardImage = view.findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.iv_board_image)
        val tvName = view.findViewById<android.widget.TextView>(R.id.tv_name)
        val tvCreatedBy = view.findViewById<android.widget.TextView>(R.id.tv_created_by)
    }
}
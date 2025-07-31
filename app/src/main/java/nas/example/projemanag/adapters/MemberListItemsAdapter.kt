package nas.example.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import nas.example.projemanag.R
import nas.example.projemanag.models.User
import nas.example.projemanag.utils.Constants

open class MemberListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<MemberListItemsAdapter.MyViewHolder>() {

    private var onClickListener: OnClickListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_member, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivMemberImage: CircleImageView = view.findViewById(R.id.iv_member_image)
        val tvMemberName: TextView = view.findViewById(R.id.tv_member_name)
        val tvMemberEmail: TextView = view.findViewById(R.id.tv_member_email)
        val ivSelectedMember: ImageView = view.findViewById(R.id.iv_selected_member)

        fun bind(model: User) {
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(ivMemberImage)

            tvMemberName.text = model.name
            tvMemberEmail.text = model.email

            if(model.selected){
                ivSelectedMember.visibility = View.VISIBLE
            }else{
                ivSelectedMember.visibility = View.GONE
            }

            itemView.setOnClickListener{
                if(onClickListener!= null){
                    if(model.selected){
                        onClickListener!!.onClick(position, model, Constants.UNSELECT)
                    }else{
                        onClickListener!!.onClick(position, model, Constants.SELECT)
                    }
                }
            }
        }
    }

    interface OnClickListener{
        fun onClick(position: Int, user: User, action: String)
    }
}
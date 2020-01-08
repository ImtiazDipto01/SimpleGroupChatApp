package com.example.myapplication.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.utils.Sticker
import com.example.myapplication.utils.inflate
import com.sendbird.android.User
import kotlinx.android.synthetic.main.item_users.view.*

class UserListAdapter(private val list: List<User>, private val listener: UserListener)
    : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.item_users))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list.get(position))

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private lateinit var sticker: Sticker

        init{
            itemView.setOnClickListener {
                listener.userClicked(list.get(adapterPosition))
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(user: User) {
            itemView.tvName.text = user.userId
        }
    }

    interface UserListener {
        fun userClicked(user: User)
    }
}
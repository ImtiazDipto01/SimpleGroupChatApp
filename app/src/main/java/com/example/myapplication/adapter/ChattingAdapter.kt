package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.utils.Messages
import kotlinx.android.synthetic.main.item_receive.view.*
import kotlinx.android.synthetic.main.item_send.view.*

class ChattingAdapter(var list: MutableList<Messages>, context : Context) : RecyclerView.Adapter<ChattingAdapter.MyViewHolder>() {

    var inflater : LayoutInflater
    init{
       inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val viewHolder = if(viewType == 1){
            MyViewHolder(inflater.inflate(R.layout.item_send, parent, false))
        }
        else{
            MyViewHolder(inflater.inflate(R.layout.item_receive, parent, false))
        }
        return viewHolder
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) = holder.bind(list.get(position))

    override fun getItemViewType(position: Int): Int {
        var viewType = list.get(position).viewTypeId
        return list.get(position).viewTypeId
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(messages: Messages){
            if(messages.viewTypeId == 1){
                itemView.messageSender.text = messages.msg
            }
            else{
                itemView.messageReceiver.text = messages.msg
            }
        }
    }
}
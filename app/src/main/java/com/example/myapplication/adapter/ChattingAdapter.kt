package com.example.myapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.R
import com.example.myapplication.utils.App
import com.example.myapplication.utils.Messages
import kotlinx.android.synthetic.main.item_receive.view.*
import kotlinx.android.synthetic.main.item_send.view.*
import kotlinx.android.synthetic.main.item_sticker_receiver.view.*
import kotlinx.android.synthetic.main.item_sticker_sender.view.*

class ChattingAdapter(var list: MutableList<Messages>, context : Context) : RecyclerView.Adapter<ChattingAdapter.MyViewHolder>() {

    var inflater : LayoutInflater
    init{
       inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val viewHolder = if(viewType == App.SENDER_MSG){
            MyViewHolder(inflater.inflate(R.layout.item_send, parent, false))
        }
        else if(viewType == App.RECEIVER_MSG){
            MyViewHolder(inflater.inflate(R.layout.item_receive, parent, false))
        }
        else if(viewType == App.SENDER_STICKER){
            MyViewHolder(inflater.inflate(R.layout.item_sticker_sender, parent, false))
        }
        else{
            MyViewHolder(inflater.inflate(R.layout.item_sticker_receiver, parent, false))
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
            if(messages.viewTypeId == App.SENDER_MSG){
                itemView.messageSender.text = messages.msg
            }
            else if(messages.viewTypeId == App.RECEIVER_MSG){
                itemView.messageReceiver.text = messages.msg
            }
            else if(messages.viewTypeId == App.SENDER_STICKER){
                val options : RequestOptions = RequestOptions()
                    .transform(FitCenter())
                    .priority(Priority.HIGH)

                Glide.with(itemView.context)
                    .load(messages.msg)
                    .apply(options)
                    .into(itemView.stickerSender)
            }
            else {
                val options : RequestOptions = RequestOptions()
                    .transform(FitCenter())
                    .priority(Priority.HIGH)

                Glide.with(itemView.context)
                    .load(messages.msg)
                    .apply(options)
                    .into(itemView.stickerReceiver)
            }
        }
    }
}
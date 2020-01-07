package com.example.myapplication.adapter

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.utils.Sticker
import com.example.myapplication.utils.inflate
import kotlinx.android.synthetic.main.item_sticker_bottom_dialog.view.*

class StickerAdapter(private val list: List<Sticker>, private val listener: StickerListener)
    : RecyclerView.Adapter<StickerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(parent.inflate(R.layout.item_sticker_bottom_dialog))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(list.get(position))

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private lateinit var sticker: Sticker

        init{
            itemView.setOnClickListener {
                listener?.apply {
                    stickerClicked(list.get(adapterPosition))
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(sticker: Sticker) {
            this.sticker = sticker
            val bitmap = BitmapFactory.decodeResource(itemView.photo.context.resources, this.sticker.drawable)
            itemView.photo.setImageDrawable(BitmapDrawable(itemView.photo.context.resources, bitmap))
        }
    }

    interface StickerListener {
        fun stickerClicked(sticker: Sticker)
    }
}
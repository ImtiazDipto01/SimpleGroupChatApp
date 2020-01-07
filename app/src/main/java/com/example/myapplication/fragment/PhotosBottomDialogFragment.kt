package com.example.myapplication.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.StickerAdapter
import com.example.myapplication.utils.Sticker
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.layout_photo_bottom_sheet.*

class PhotosBottomDialogFragment : BottomSheetDialogFragment(), StickerAdapter.StickerListener {

    val TAG = "BottomDialog"
    var stickerListener : StickerListener? = null
    val stickerList : List<Sticker> by lazy{
        val stickerList = mutableListOf<Sticker>()

        stickerList.add(Sticker(R.drawable.discount))
        stickerList.add(Sticker(R.drawable.free))

        stickerList
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_photo_bottom_sheet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        generateRecyclverView()
    }

    fun generateRecyclverView(){
        photosRecyclerView.layoutManager = GridLayoutManager(context, 3)
        photosRecyclerView.adapter =
            StickerAdapter(stickerList, this)
    }

    override fun stickerClicked(sticker: Sticker) {
        stickerListener?.apply {
            stickerClickedDataPassToActivity(sticker)
            dismiss()
        }.run {
            Log.e(TAG, "ClickListener")
        }
    }

    interface StickerListener {
        fun stickerClickedDataPassToActivity(sticker: Sticker)
    }
}
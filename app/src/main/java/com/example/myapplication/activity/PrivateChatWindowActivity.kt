package com.example.myapplication.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.example.myapplication.R
import com.example.myapplication.adapter.ChattingAdapter
import com.example.myapplication.fragment.PhotosBottomDialogFragment
import com.example.myapplication.utils.App
import com.example.myapplication.utils.Messages
import com.example.myapplication.utils.Sticker
import com.sendbird.android.*
import com.sendbird.android.BaseChannel.SendFileMessageHandler
import com.sendbird.android.BaseChannel.SendUserMessageHandler
import kotlinx.android.synthetic.main.activity_private_chat_window.*
import java.io.*


class PrivateChatWindowActivity : AppCompatActivity(), PhotosBottomDialogFragment.StickerListener {

    val userId = "MyPc-01"
    //val userId = "Imtiaz-01"

    lateinit var chattingAdapter: ChattingAdapter
    lateinit var list : MutableList<Messages>
    var privateChatUrl = "sendbird_group_channel_189317504_ba5ab0a7e6e6a04402b20ee6ee9c580011898dd6"
    val TAG = "PrivateChatInvite"
    var groupChannelForPrivateChat : GroupChannel? = null
    lateinit var smoothScroller: SmoothScroller

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_chat_window)
        initInstance()
        getChannelInstanceForCheckingChannelInvitation()
        generateRecyclerView()
        notifyEventHandler()

        btnSendMessage.setOnClickListener {
            sendMessage()
        }

        btnSendSticker.setOnClickListener {
            val addPhotFromBottomDialog = PhotosBottomDialogFragment()
            addPhotFromBottomDialog.stickerListener = this
            addPhotFromBottomDialog.show(supportFragmentManager, "PhotosBottomDialogFragment")
        }

        smoothScroller = object : LinearSmoothScroller(applicationContext) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_ANY
            }
        }
    }

    fun getChannelInstanceForCheckingChannelInvitation(){
        GroupChannel.getChannel(privateChatUrl, object : GroupChannel.GroupChannelGetHandler{
            override fun onResult(groupChannel: GroupChannel?, exp: SendBirdException?) {
                if(exp != null){
                    Log.e(TAG, "getChannelInstanceExp : $exp")// Error.
                }
                else{
                    groupChannelForPrivateChat = groupChannel?.let {
                        Log.e(TAG, "getChannelInstance not NULL")
                        Log.e(TAG, "channelUrl : ${it.url}")
                        it
                    }
                }
            }
        })
    }

    private fun initInstance() {
        list = ArrayList<Messages>()
    }

    private fun generateRecyclerView() {
        rvMessagingList.apply {
            chattingAdapter = ChattingAdapter(list, applicationContext)
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = chattingAdapter
        }
    }

    fun notifyEventHandler(){
        SendBird.addChannelHandler(privateChatUrl, object : SendBird.ChannelHandler(){
            override fun onMessageReceived(p0: BaseChannel?, baseMessage: BaseMessage?) {
                baseMessage?.let {
                    if(it is UserMessage){
                        //Toast.makeText(this@PrivateChatWindowActivity, it.message, Toast.LENGTH_SHORT).show()
                        list.add(Messages(it.message, App.RECEIVER_MSG))
                        chattingAdapter.notifyDataSetChanged()
                    }
                    else if(it is FileMessage){
                        list.add(Messages(it.url, App.RECEIVER_STICKER))
                        chattingAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onUserReceivedInvitation(channel: GroupChannel?, inviter: User?, invitees: MutableList<User>?) {
                invitees?.let {
                    Log.e(TAG, "You got ${it.size} invitation")
                }
                Toast.makeText(this@PrivateChatWindowActivity, "You Got A Invitation", Toast.LENGTH_SHORT).show()
            }

        })

    }

    fun sendMessage() {
        val msg = etUserMessage.text.toString()
        etUserMessage.setText("")
        groupChannelForPrivateChat?.sendUserMessage(msg, SendUserMessageHandler { userMessage, e ->
            if (e != null) {
                Log.e(TAG, "sendMessageExp : $e")// Error.// Error.
                return@SendUserMessageHandler
            }
            else{
                Log.e(TAG, "sendMessage : ${userMessage.message}")
                list.add(Messages(userMessage.message, App.SENDER_MSG))
                chattingAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun stickerClickedDataPassToActivity(sticker: Sticker) {
        sendSticker(sticker)
    }

    private fun sendSticker(sticker: Sticker) {
        val file = createStickerAsFile(sticker)

        file?.let {

            Log.e(TAG, "sendStickerExp : File Not Null")
            val params = FileMessageParams()
                .setFile(it)
                .setFileName(it.name)

            groupChannelForPrivateChat?.sendFileMessage(
                params,
                SendFileMessageHandler { fileMessage, e ->
                    if (e != null) { // Error.
                        Log.e(TAG, "sendStickerExp : $e")
                        return@SendFileMessageHandler
                    }
                    else{
                        Log.e(TAG, "sendSticker : Success, {${fileMessage.url.toString()}}")
                        list.add(Messages(fileMessage.url, App.SENDER_STICKER))
                        chattingAdapter.notifyDataSetChanged()
                    }
                })
        }

    }

    fun createStickerAsFile(sticker: Sticker) : File?{
        try
        {
            val file = File(cacheDir, "myFile")
            val inputStream : InputStream? = resources.openRawResource(sticker.drawable)
            val out : OutputStream = FileOutputStream(file)

            val buf = ByteArray(1024)
            var len : Int
            var lenIsNotZero = true
            while(lenIsNotZero){
                if(inputStream != null){
                    len = inputStream.read(buf)
                    if(len > 0){
                        Log.e(TAG, "len is not 0")
                        out.write(buf,0,len)
                    }
                    else {
                        Log.e(TAG, "len is 0")
                        lenIsNotZero = false
                    }
                }
                else{
                    lenIsNotZero = false
                }

            }
            out.close()
            if(inputStream != null) inputStream.close()

            return file
        }
        catch (e : IOException){
            return null
        }
    }
}

package com.example.myapplication.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Telephony.BaseMmsColumns.MESSAGE_TYPE
import android.util.Log
import android.view.View
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
import com.sendbird.android.BaseChannel.*
import kotlinx.android.synthetic.main.activity_private_chat_window.*
import java.io.*
import java.sql.Types.TIMESTAMP


class PrivateChatWindowActivity : AppCompatActivity(), PhotosBottomDialogFragment.StickerListener,
    View.OnClickListener {

    var privateChatUrl = "sendbird_group_channel_189317504_ba5ab0a7e6e6a04402b20ee6ee9c580011898dd6"
    val TAG = "PrivateChatInvite"
    //val userId = "MyPc-01"
    //val userId = "Imtiaz-01"
    val CUSTOM_TYPE = "AllMessages"

    lateinit var chattingAdapter: ChattingAdapter
    lateinit var Msglist : MutableList<Messages>
    lateinit var sharedPreferences: SharedPreferences
    lateinit var smoothScroller: SmoothScroller

    var groupChannelForPrivateChat : GroupChannel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_chat_window)
        initInstance()
        initViewListener()
        generateRecyclerView()
        getChannelInstanceForCheckingChannelInvitation()
        notifyEventHandler()
    }

    private fun initViewListener() {
        btnSendMessage.setOnClickListener(this)
        btnSendSticker.setOnClickListener(this)
    }

    private fun initInstance() {
        Msglist = ArrayList<Messages>()
        sharedPreferences = getSharedPreferences(App.PREF_NAME, Context.MODE_PRIVATE)
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
                    if(groupChannelForPrivateChat != null){
                        getUnseenMessages()
                    }
                }
            }
        })
    }

    private fun generateRecyclerView() {
        rvMessagingList.apply {
            chattingAdapter = ChattingAdapter(Msglist, applicationContext)
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = chattingAdapter
        }
    }

    fun notifyEventHandler(){
        SendBird.addChannelHandler(privateChatUrl, object : SendBird.ChannelHandler(){
            override fun onMessageReceived(p0: BaseChannel?, baseMessage: BaseMessage?) {
                baseMessage?.let {
                    if(it is UserMessage){
                        Msglist.add(Messages(it.message, App.RECEIVER_MSG))
                        chattingAdapter.notifyDataSetChanged()
                        rvMessagingList.smoothScrollToPosition(Msglist.size - 1)
                    }
                    else if(it is FileMessage){
                        Msglist.add(Messages(it.url, App.RECEIVER_STICKER))
                        chattingAdapter.notifyDataSetChanged()
                        rvMessagingList.smoothScrollToPosition(Msglist.size - 1)
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

        val params = UserMessageParams()
            .setMessage(etUserMessage.text.toString())
            .setCustomType(CUSTOM_TYPE)
        etUserMessage.setText("")

        groupChannelForPrivateChat?.sendUserMessage(params, SendUserMessageHandler { userMessage, e ->
            if (e != null) {
                Log.e(TAG, "sendMessageExp : $e")// Error.// Error.
                return@SendUserMessageHandler
            }
            else{
                Log.e(TAG, "sendMessage : ${userMessage.message}")
                Msglist.add(Messages(userMessage.message, App.SENDER_MSG))
                chattingAdapter.notifyDataSetChanged()

                rvMessagingList.smoothScrollToPosition(Msglist.size - 1)
            }
        })
    }

    override fun stickerClickedDataPassToActivity(sticker: Sticker) {
        sendSticker(sticker)
    }

    private fun sendSticker(sticker: Sticker) {

        Log.e(TAG, "sendStickerExp : File Not Null")
        val params = FileMessageParams()
            .setFileUrl(sticker.link)
            .setCustomType(CUSTOM_TYPE)
            .setFileName("myGif")

        groupChannelForPrivateChat?.sendFileMessage(
            params,
            SendFileMessageHandler { fileMessage, e ->
                if (e != null) { // Error.
                    Log.e(TAG, "sendStickerExp : $e")
                    return@SendFileMessageHandler
                }
                else{
                    Log.e(TAG, "sendSticker : Success, {${fileMessage.url.toString()}}")
                    Msglist.add(Messages(fileMessage.url, App.SENDER_STICKER))
                    chattingAdapter.notifyDataSetChanged()

                    rvMessagingList.smoothScrollToPosition(Msglist.size - 1)
                }
            })

    }

    fun createStickerAsFile(sticker: Sticker) : File?{
        try
        {
            val file = File(cacheDir, "myFile")
            val inputStream : InputStream? = /*resources.openRawResource(/*sticker.drawable*/)*/ null
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

    fun getUnseenMessages() {
        val timeMillis = sharedPreferences.getLong(App.LAST_SEEN_TIMEMILLI, 0)
        if(timeMillis > 0){
            groupChannelForPrivateChat?.getNextMessagesByTimestamp(timeMillis, true, 10, false, MessageTypeFilter.ALL, CUSTOM_TYPE, GetMessagesHandler { list, e ->
                if (e != null) { // Error.
                    Log.e(TAG, "getUnseenMsgExp : $e")
                    return@GetMessagesHandler
                }
                else {
                    Log.e(TAG, "getUnseenMsgSize : ${list.size}")
                    for(i in 0 until list.size){
                        val baseMessage = list.get(i)
                        if(baseMessage != null){
                            if(baseMessage is UserMessage){
                                Msglist.add(Messages(baseMessage.message, App.RECEIVER_MSG))
                            }
                            else if(baseMessage is FileMessage){
                                Msglist.add(Messages(baseMessage.url, App.RECEIVER_STICKER))
                            }
                        }
                    }

                    if(Msglist.size > 0){
                        chattingAdapter.notifyDataSetChanged()
                        rvMessagingList.smoothScrollToPosition(Msglist.size - 1)
                    }


                }
            })
        }

    }


    override fun onBackPressed() {
        super.onBackPressed()
        val timeMillis = System.currentTimeMillis()
        sharedPreferences.edit().putLong(App.LAST_SEEN_TIMEMILLI, timeMillis).apply()
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.btnSendMessage -> {
                    sendMessage()
                }

                R.id.btnSendSticker -> {
                    val addPhotFromBottomDialog = PhotosBottomDialogFragment()
                    addPhotFromBottomDialog.stickerListener = this
                    addPhotFromBottomDialog.show(supportFragmentManager, "PhotosBottomDialogFragment")
                }
            }
        }
    }
}

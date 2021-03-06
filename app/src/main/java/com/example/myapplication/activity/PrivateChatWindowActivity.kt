package com.example.myapplication.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.example.myapplication.adapter.ChattingAdapter
import com.example.myapplication.fragment.PhotosBottomDialogFragment
import com.example.myapplication.utils.App
import com.example.myapplication.utils.Messages
import com.example.myapplication.utils.Sticker
import com.sendbird.android.*
import com.sendbird.android.BaseChannel.*
import kotlinx.android.synthetic.main.activity_private_chat_window.*
import java.io.*
import com.example.myapplication.R


class PrivateChatWindowActivity : AppCompatActivity(), PhotosBottomDialogFragment.StickerListener,
    View.OnClickListener {

    var privateChatUrl = ""
    val TAG = "PrivateChatInvite"
    val CUSTOM_TYPE = "AllMessages"

    lateinit var chattingAdapter: ChattingAdapter
    lateinit var Msglist : MutableList<Messages>
    lateinit var sharedPreferences: SharedPreferences
    lateinit var smoothScroller: SmoothScroller

    var userId : String? = null
    var groupChannelForPrivateChat : GroupChannel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_chat_window)
        initInstance()
        initViewListener()
        getValuesFromIntent()
        generateRecyclerView()
        //connectToSendBird(userId)
        getChannelInstanceForCheckingChannelInvitation()
        notifyEventHandler()
    }

    private fun getValuesFromIntent() {
        if(intent.hasExtra(App.CHANNEL_URL)){
            intent.getStringExtra(App.CHANNEL_URL)?.let {
                privateChatUrl = it
            }
        }
    }

    private fun initViewListener() {
        btnSendMessage.setOnClickListener(this)
        btnSendSticker.setOnClickListener(this)
    }

    private fun initInstance() {
        Msglist = ArrayList<Messages>()
        sharedPreferences = getSharedPreferences(App.PREF_NAME, Context.MODE_PRIVATE)
        userId = sharedPreferences.getString(App.USER_ID, null)
    }

    fun getChannelInstanceForCheckingChannelInvitation(){
        if(privateChatUrl.equals("")){
            Log.e(TAG, "privateChatUrleExp : privateChatUrl is Empty")
            return
        }
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
                    groupChannelForPrivateChat?.markAsRead()
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

    fun createImageAsFile(sticker: Sticker) : File?{
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

    private fun connectToSendBird(userIdTxt: String?) {
        SendBird.connect(userIdTxt, object : SendBird.ConnectHandler {
            override fun onConnected(user: User?, ex: SendBirdException?) {
                if(ex != null){
                    Toast.makeText(applicationContext,"Something Worng, Retry Or Try Later", Toast.LENGTH_SHORT).show()
                }
                else{
                    user?.apply {
                        Toast.makeText(applicationContext,"Connected !", Toast.LENGTH_SHORT).show()
                        getChannelInstanceForCheckingChannelInvitation()
                    }
                }
            }
        })
    }

    fun disconnectFromSendBird(){
        SendBird.disconnect(SendBird.DisconnectHandler {
            Log.e(TAG, "SendBird Disconnected!")
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val timeMillis = System.currentTimeMillis()
        sharedPreferences.edit().putLong(App.LAST_SEEN_TIMEMILLI, timeMillis).apply()
        //disconnectFromSendBird()
    }

    override fun onResume() {
        super.onResume()
        //connectToSendBird(userId)
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

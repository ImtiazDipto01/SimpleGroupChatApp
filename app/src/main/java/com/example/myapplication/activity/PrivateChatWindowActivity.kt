package com.example.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.ChattingAdapter
import com.example.myapplication.utils.Messages
import com.sendbird.android.*
import com.sendbird.android.BaseChannel.SendUserMessageHandler
import kotlinx.android.synthetic.main.activity_private_chat_window.*

class PrivateChatWindowActivity : AppCompatActivity() {

    val userId = "MyPc-01"
    //val userId = "Imtiaz-01"

    lateinit var chattingAdapter: ChattingAdapter
    lateinit var list : MutableList<Messages>
    var privateChatUrl = "sendbird_group_channel_189317504_ba5ab0a7e6e6a04402b20ee6ee9c580011898dd6"
    val TAG = "PrivateChatInvite"
    var groupChannelForPrivateChat : GroupChannel? = null

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

    private fun setdemoData(){
        for(i in 1..4){
            if(i % 2 == 0){
                list.add(Messages("Hello !!!", 1))
            }
            else{
                list.add(Messages("Hi !!!", 0))
            }
        }
    }

    fun notifyEventHandler(){
        SendBird.addChannelHandler(privateChatUrl, object : SendBird.ChannelHandler(){
            override fun onMessageReceived(p0: BaseChannel?, baseMessage: BaseMessage?) {
                baseMessage?.let {
                    if(it is UserMessage){
                        //Toast.makeText(this@PrivateChatWindowActivity, it.message, Toast.LENGTH_SHORT).show()
                        list.add(Messages(it.message, 0))
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
                list.add(Messages(userMessage.message, 1))
                chattingAdapter.notifyDataSetChanged()
            }
        })
    }
}

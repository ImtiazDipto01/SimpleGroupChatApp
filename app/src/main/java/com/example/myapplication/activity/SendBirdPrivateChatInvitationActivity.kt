package com.example.myapplication.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.sendbird.android.*
import com.sendbird.android.BaseChannel.SendUserMessageHandler
import com.sendbird.android.BaseMessageParams.MentionType
import com.sendbird.android.BaseMessageParams.PushNotificationDeliveryOption
import com.sendbird.android.GroupChannel.GroupChannelAcceptInvitationHandler
import com.sendbird.android.GroupChannel.GroupChannelCreateHandler
import kotlinx.android.synthetic.main.activity_send_bird_private_chat.*


class SendBirdPrivateChatInvitationActivity : AppCompatActivity() {

    val userId = "MyPc-01"
    // val userId = "Imtiaz-01"
    var privateChatUrl = "sendbird_group_channel_189317504_ba5ab0a7e6e6a04402b20ee6ee9c580011898dd6"
    val TAG = "PrivateChatInvite"
    var groupChannelForPrivateChat : GroupChannel? = null

    val invitor = "MyPc-01"
    val acceptor = "Imtiaz-01"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_bird_private_chat)
        connectToSendBird()

        if(userId.equals("Imtiaz-01") && !privateChatUrl.equals("")){
            getChannelInstanceForCheckingChannelInvitation()
        }

        btnCreatePrivateChatChannel.setOnClickListener {
            if(userId.equals("MyPc-01")){
                val userIds = listOf<String>("Imtiaz-01")
                createPrivateChatChannel(userIds)
            }
        }


        btnStartMessaging.setOnClickListener {
            startActivity(Intent(this@SendBirdPrivateChatInvitationActivity, PrivateChatWindowActivity::class.java))
        }

        notifyEventHandler()
    }

    private fun connectToSendBird() {
        SendBird.connect(userId, object : SendBird.ConnectHandler {
            override fun onConnected(user: User?, ex: SendBirdException?) {
                ex?.let {

                }.run {
                    Log.e(TAG, "ConnectedPrivateChat: YES")
                }
            }
        })
    }

    fun createPrivateChatChannel(USER_IDS : List<String>, IS_DISTINCT: Boolean = true){

        GroupChannel.createChannelWithUserIds(
            USER_IDS,
            IS_DISTINCT,
            GroupChannelCreateHandler { groupChannel, e ->
                if (e != null) {
                    Log.e(TAG, "privateChatExp : $e")// Error.
                    return@GroupChannelCreateHandler
                }
                else{
                    privateChatUrl = groupChannel.url
                    this.groupChannelForPrivateChat = groupChannel
                    Log.e(TAG, "privateChatUrl : $privateChatUrl")

                }
            })
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

    fun acceptInvitation(){

        Log.e(TAG, "${groupChannelForPrivateChat?.inviter?.userId}>>>>>>>>>")
        val member = groupChannelForPrivateChat?.members


        groupChannelForPrivateChat?.acceptInvitation(GroupChannelAcceptInvitationHandler { e ->
            if (e != null) {
                Log.e(TAG, "acceptInvitationExp : $e")// Error.
                return@GroupChannelAcceptInvitationHandler
            }
            else{
                Log.e(TAG, "acceptInvitation SuccessFull")
            }
        })
    }

    fun notifyEventHandler(){
        SendBird.addChannelHandler(privateChatUrl, object : SendBird.ChannelHandler(){
            override fun onMessageReceived(p0: BaseChannel?, baseMessage: BaseMessage?) {
                baseMessage?.let {
                    if(it is UserMessage){
                        Toast.makeText(this@SendBirdPrivateChatInvitationActivity, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onUserReceivedInvitation(channel: GroupChannel?, inviter: User?, invitees: MutableList<User>?) {
                invitees?.let {
                    Log.e(TAG, "You got ${it.size} invitation")
                }
                Toast.makeText(this@SendBirdPrivateChatInvitationActivity, "You Got A Invitation", Toast.LENGTH_SHORT).show()
            }

        })

    }

    fun sendMessage() {
        groupChannelForPrivateChat?.sendUserMessage("Hello Form $userId", SendUserMessageHandler { userMessage, e ->
            if (e != null) {
                Log.e(TAG, "sendMessageExp : $e")// Error.// Error.
                return@SendUserMessageHandler
            }
            else{
                Log.e(TAG, "sendMessage : ${userMessage.message}")
            }
        })
    }


}

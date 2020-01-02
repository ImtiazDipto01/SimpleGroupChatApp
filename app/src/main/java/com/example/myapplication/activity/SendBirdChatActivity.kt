package com.example.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import com.sendbird.android.SendBird.ConnectHandler
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import android.widget.Toast
import com.sendbird.android.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_send_bird_chat.*


class SendBirdChatActivity : AppCompatActivity() {

    val userId = "MyMurad-01"
    var channelUrl = "sendbird_open_channel_61863_5d64dd30c4defce9450fb1d8c33a25017e70e24a"
    var openChatChannel : OpenChannel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_bird_chat)
        connectToSendBird()
        createOpenChannel()
        btnSendMsg.setOnClickListener {
            sendMsg(etMsg.text.toString())
        }
    }

    private fun createOpenChannel() {
        if(!channelUrl.equals("")){
            enterToTheChannel()
            return
        }

        OpenChannel.createChannel(object : OpenChannel.OpenChannelCreateHandler{
            override fun onResult(openChannel: OpenChannel?, ex: SendBirdException?) {
                if (ex != null) { // Error.
                    Log.e("ChannelUrl", ex.toString())
                    return
                }
                else{
                    openChannel?.let {
                        Log.e("ChannelUrl", it.url)
                        channelUrl = it.url
                        enterToTheChannel()
                    }
                }
            }
        })
    }

    private fun connectToSendBird() {
        SendBird.connect(userId, object : ConnectHandler{
            override fun onConnected(user: User?, ex: SendBirdException?) {
                ex?.let {

                }.run {
                    Log.e("onConnected", "YES")
                }
            }

        })

    }

    private fun enterToTheChannel(){
        OpenChannel.getChannel(channelUrl, object :  OpenChannel.OpenChannelGetHandler{
            override fun onResult(openChannel: OpenChannel?, p1: SendBirdException?) {
                openChannel?.apply {
                    openChatChannel = this
                    enter(OpenChannel.OpenChannelEnterHandler{exp ->
                        if (exp != null) {
                            openChatChannel = null
                            Log.e("enterToTheChannel", "False, "+exp.toString())// Error.
                            Toast.makeText(applicationContext, "Connection Lost", Toast.LENGTH_SHORT).show()
                            return@OpenChannelEnterHandler
                        }
                        else{
                            Log.e("enterToTheChannel", "YES")
                            Toast.makeText(applicationContext, "Connected", Toast.LENGTH_SHORT).show()
                            tvConnection.visibility = View.VISIBLE
                            receiveMsg()
                        }
                    })

                }
            }
        })

    }


    private fun receiveMsg(){
        SendBird.addChannelHandler(channelUrl, object : SendBird.ChannelHandler() {
            override fun onMessageReceived(baseChannel: BaseChannel?, baseMessage: BaseMessage?) {
                if(baseMessage is UserMessage){
                    Toast.makeText(applicationContext, baseMessage.message, Toast.LENGTH_SHORT).show()
                    Log.e("receiveMessage", baseMessage.message+">>>>>>>")
                }
            }
        })
    }

    private fun sendMsg(msg : String){
        openChatChannel?.apply {
            sendUserMessage(msg, BaseChannel.SendUserMessageHandler{ userMessage, exp ->
                Log.e("sendUserMessage", userMessage.message+">>>>>>>")
                if(exp != null){
                    Log.e("sendMessageEx", "False, "+exp.toString())// Error.
                }
            })
        }
    }


}

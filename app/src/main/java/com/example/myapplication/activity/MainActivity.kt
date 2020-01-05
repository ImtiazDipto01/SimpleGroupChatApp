package com.example.myapplication.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSendBirdChat.setOnClickListener {
            val intent = Intent(this@MainActivity, SendBirdOpenChatActivity::class.java)
            startActivity(intent)
        }

        btnStartPrivateChat.setOnClickListener {
            startActivity(Intent(this@MainActivity, SendBirdPrivateChatInvitationActivity::class.java))
        }
    }


}

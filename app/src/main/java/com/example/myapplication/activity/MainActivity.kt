package com.example.myapplication.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.utils.App
import com.sendbird.android.SendBird
import com.sendbird.android.SendBirdException
import com.sendbird.android.User
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initInstance()
        initClickListener()
        connectToSendBird()
    }

    fun openChat(){
        btnSendBirdChat.setOnClickListener {
            val intent = Intent(this@MainActivity, SendBirdOpenChatActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initClickListener() {
        btnSendBirdChat.setOnClickListener(this)
        btnStartPrivateChat.setOnClickListener(this)
    }

    private fun initInstance() {
        sharedPreferences = getSharedPreferences(App.PREF_NAME, Context.MODE_PRIVATE)
    }


    private fun connectToSendBird() {
        if(sharedPreferences.getString(App.USER_ID, null) != null){
            val userIdTxt = sharedPreferences.getString(App.USER_ID, null)
            SendBird.connect(userIdTxt, object : SendBird.ConnectHandler {
                override fun onConnected(user: User?, ex: SendBirdException?) {
                    if(ex != null){
                        Toast.makeText(applicationContext,"Something Worng, Retry Or Try Later", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(applicationContext,"Connected!!", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
        else{

        }

    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.btnStartPrivateChat -> {
                    startActivity(Intent(this@MainActivity, UserListActivity::class.java))
                }
            }
        }
    }

}

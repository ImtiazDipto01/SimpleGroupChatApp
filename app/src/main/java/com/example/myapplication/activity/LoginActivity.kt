package com.example.myapplication.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.utils.App
import com.sendbird.android.SendBird
import com.sendbird.android.SendBirdException
import com.sendbird.android.User
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initInstance()
        initClickListener()
        isUserLogedIn()
    }

    private fun isUserLogedIn() {
        if(sharedPreferences.getString(App.USER_ID, null) != null){
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun initInstance() {
        sharedPreferences = getSharedPreferences(App.PREF_NAME, Context.MODE_PRIVATE)
    }

    private fun initClickListener() {
        btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v != null){
            when(v.id){
                R.id.btnLogin ->{
                    if(etUserName.text.toString().equals("")){
                        Toast.makeText(applicationContext,"Empty UserId Not Accepted!", Toast.LENGTH_LONG).show()
                    }
                    else{
                        connectToSendBird(etUserName.text.toString())
                    }
                }
            }
        }
    }

    private fun connectToSendBird(userIdTxt: String) {
        SendBird.connect(userIdTxt, object : SendBird.ConnectHandler {
            override fun onConnected(user: User?, ex: SendBirdException?) {
                if(ex != null){
                    Toast.makeText(applicationContext,"Something Worng, Retry Or Try Later", Toast.LENGTH_SHORT).show()
                }
                else{
                    user?.apply {
                        Toast.makeText(applicationContext,"Welcome $userId!!", Toast.LENGTH_SHORT).show()
                        sharedPreferences.edit().putString(App.USER_ID, userId).apply()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                }
            }
        })
    }
}

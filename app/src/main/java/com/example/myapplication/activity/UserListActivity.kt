package com.example.myapplication.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.adapter.UserListAdapter
import com.example.myapplication.utils.App
import com.sendbird.android.GroupChannel
import com.sendbird.android.SendBird
import com.sendbird.android.SendBirdException
import com.sendbird.android.User
import com.sendbird.android.UserListQuery.UserListQueryResultHandler
import kotlinx.android.synthetic.main.activity_user_list.*


class UserListActivity : AppCompatActivity(), UserListAdapter.UserListener {

    lateinit var userList : MutableList<User>
    lateinit var userListAdapter: UserListAdapter

    val TAG = "UserListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        initInstance()
        generateRecyclerView()
        getUserList()
    }

    private fun initInstance() {
        userList = ArrayList<User>()
    }


    private fun getUserList() {
        val applicationUserListQuery = SendBird.createApplicationUserListQuery()
        applicationUserListQuery.next(UserListQueryResultHandler { list, e ->
            if (e != null) { // Error.
                Log.e("UserList", "$e")
                return@UserListQueryResultHandler
            }
            else{
                Log.e("UserList", "${list.size}")
                list?.let {
                    userList.addAll(it)
                    userListAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    fun generateRecyclerView() {
        rvUserList.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            userListAdapter = UserListAdapter(userList, this@UserListActivity)
            adapter = userListAdapter
        }
    }

    override fun userClicked(user: User) {
        val userIds = listOf<String>(user.userId)
        createPrivateChatChannel(userIds)
    }

    fun createPrivateChatChannel(USER_IDS : List<String>, IS_DISTINCT: Boolean = true){
        Toast.makeText(applicationContext, "Please Wait..We are creating Chat Window For You", Toast.LENGTH_SHORT).show()
        GroupChannel.createChannelWithUserIds(
            USER_IDS,
            IS_DISTINCT,
            GroupChannel.GroupChannelCreateHandler { groupChannel, e ->
                if (e != null) {
                    Toast.makeText(applicationContext, "Something went Wrong, Try Latter!", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "privateChatExp : $e")// Error.
                    return@GroupChannelCreateHandler
                } else {
                    Log.e(TAG, "privateChatUrl : $groupChannel.url")
                    Intent(this@UserListActivity, PrivateChatWindowActivity::class.java).apply {
                        putExtra(App.CHANNEL_URL, groupChannel.url)
                        startActivity(this)
                    }
                }
            })
    }
}

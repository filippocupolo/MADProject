package com.example.andrea.lab11

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class ChatMessage(view: View): RecyclerView.ViewHolder(view){

    val deBugTag = "ChatMessage"
    var messageUser : TextView? = null
    var messageTime : TextView? = null
    var messageText : TextView? = null
    var readSymbol : ImageView? = null

    init {
        messageText = view.findViewById<TextView>(R.id.message_text)
        messageTime = view.findViewById<TextView>(R.id.message_time)
        messageUser = view.findViewById<TextView>(R.id.message_user)
        readSymbol = view.findViewById<ImageView>(R.id.readCheck)
    }

    fun bindData(m_text:String, m_user:String, m_time:Long , m_read: Boolean, myUserId :String, m_userId:String){
        messageText?.text = m_text
        messageUser?.text = m_user
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = m_time
        val date = calendar.time
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        messageTime?.text = sdf.format(date)

        if(myUserId.equals(m_userId) && m_read){

            readSymbol?.visibility = View.VISIBLE

        }else{

            readSymbol?.visibility = View.GONE
        }
    }
}
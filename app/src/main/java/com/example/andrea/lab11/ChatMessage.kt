package com.example.andrea.lab11

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class ChatMessage(view: View): RecyclerView.ViewHolder(view){

    val deBugTag = "ChatMessage"
    var message_user : TextView? = null
    var message_time : TextView? = null
    var message_text : TextView? = null

    init {
        message_text = view.findViewById<TextView>(R.id.message_text)
        message_time = view.findViewById<TextView>(R.id.message_time)
        message_user = view.findViewById<TextView>(R.id.message_user)
    }

    fun bindData(m_text:String, m_user:String, m_time:Long ){
        message_text?.text = m_text
        message_user?.text = m_user
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = m_time
        val date = calendar.time
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        message_time?.text = sdf.format(date)
    }
}
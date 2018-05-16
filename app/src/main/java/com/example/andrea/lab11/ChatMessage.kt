package com.example.andrea.lab11

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ChatMessage(view: View): RecyclerView.ViewHolder(view){

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
        message_time?.text = m_time.toString()
    }
}
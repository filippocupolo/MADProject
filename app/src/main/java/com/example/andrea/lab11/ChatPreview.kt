package com.example.andrea.lab11

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class ChatPreview(view: View): RecyclerView.ViewHolder(view){

    var image : ImageView? = null
    var user : TextView? = null
    var lastMessage : TextView? = null

    init {
        image = view.findViewById<ImageView>(R.id.imageUser)
        user = view.findViewById<TextView>(R.id.user_name)
        lastMessage = view.findViewById<TextView>(R.id.last_message)
    }

    fun binData(u:String,lm:String){
        user?.text = u
        lastMessage?.text = lm
    }
}
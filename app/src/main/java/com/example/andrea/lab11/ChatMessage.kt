package com.example.andrea.lab11

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
import android.view.Gravity
import android.R.attr.gravity
import android.graphics.Color
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout



class ChatMessage(view: View): RecyclerView.ViewHolder(view){

    val deBugTag = "ChatMessage"
    var messageTime : TextView? = null
    var messageText : TextView? = null
    var readSymbol : ImageView? = null

    init {
        messageText = view.findViewById<TextView>(R.id.message_text)
        messageTime = view.findViewById<TextView>(R.id.message_time)
        readSymbol = view.findViewById<ImageView>(R.id.readCheck)
    }

    fun bindData(m_text:String, m_user:String, m_time:Long , m_read: Boolean, myUserId :String, m_userId:String){

        //set margin and get parameters layout
        val dpValue = 50 // margin in dips
        val d = itemView.getResources().getDisplayMetrics().density
        val margin = (dpValue * d).toInt()
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.bottomMargin = 20

        //bind data
        messageText?.text = m_text
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = m_time
        val date = calendar.time
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")
        messageTime?.text = sdf.format(date)

        //set gravity and check simbol
        if(myUserId.equals(m_userId) ){

            if(m_read)
                readSymbol?.visibility = View.VISIBLE
            else
                readSymbol?.visibility = View.GONE

            //params.gravity = Gravity.LEFT
            params.leftMargin = margin
            params.rightMargin = 20
            itemView.setBackgroundResource(R.drawable.other_message)


        }else{

            readSymbol?.visibility = View.GONE
            //params.gravity = Gravity.RIGHT
            params.rightMargin = margin
            params.leftMargin = 20
            itemView.setBackgroundResource(R.drawable.my_message)
            //itemView.setBackgroundColor(Color.parseColor("#EEEEEE"))
        }

        itemView.layoutParams = params
    }
}
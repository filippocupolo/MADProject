package com.example.andrea.lab11

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*
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

        //set parameters layout
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.bottomMargin = getIndb(10)

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
            itemView.setBackgroundResource(R.drawable.other_message)
            params.leftMargin = getIndb(50)
            params.rightMargin = getIndb(10)


        }else{

            readSymbol?.visibility = View.GONE
            //params.gravity = Gravity.RIGHT
            itemView.setBackgroundResource(R.drawable.my_message)
            params.rightMargin = getIndb(50)
            params.leftMargin = getIndb(10)
            //itemView.setBackgroundColor(Color.parseColor("#EEEEEE"))
        }

        itemView.layoutParams = params
    }

    private fun getIndb(dpValue: Int): Int {
        return (dpValue * itemView.resources.displayMetrics.density).toInt()
    }
}
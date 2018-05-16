package com.example.andrea.lab11

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage

class ChatPreview(view: View): RecyclerView.ViewHolder(view){

    var image : ImageView? = null
    var userName : TextView? = null
    var lastMessage : TextView? = null
    var chatKey : TextView? = null
    var userId : String? = null
    val deBugTag = "ChatPreview"

    init {
        image = view.findViewById<ImageView>(R.id.imageUser)
        userName = view.findViewById<TextView>(R.id.user_name)
        lastMessage = view.findViewById<TextView>(R.id.last_message)
    }

    fun bindData(ck:String, uId:String, u:String,lm:String ){
        chatKey?.text = ck
        userId = uId
        userName?.text = u
        lastMessage?.text = lm

        //open Show Book if card is pressed
        val context = itemView.context
        itemView.isClickable = true
        itemView.setOnClickListener { v ->

            val intent = Intent(context, PersonalChat::class.java)
            intent.putExtra("chat", ck)
            intent.putExtra("userId", userId)
            intent.putExtra("userName", u)
            context.startActivity(intent)
        }

        //request for userImage
        val ref = FirebaseStorage.getInstance().reference.child("profileImages/$userId")

        //todo ref.getBytes lancia degli errori cercare di capire cosa sono
        //todo ridurre la dimensione del file ma per fare questo bisogna comprimere tutte le immagini e forse è meglio sostituite bitmap con drawable per migliorare le prestazioni
        ref.downloadUrl.addOnSuccessListener { uri ->
            FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString()).getBytes((10 * 1024 * 1024).toLong()).addOnSuccessListener { bytes ->

                image!!.setImageDrawable(BitmapDrawable(BitmapFactory.decodeByteArray(bytes, 0, bytes.size)))

            }.addOnFailureListener { e -> Log.e(deBugTag, e.message) }
        }.addOnFailureListener { e -> Log.e(deBugTag, e.message) }
    }
}
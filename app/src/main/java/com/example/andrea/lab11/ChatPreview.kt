package com.example.andrea.lab11

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ChatPreview(view: View): RecyclerView.ViewHolder(view){

    var image : ImageView? = null
    var userName : TextView? = null
    var lastMessage : TextView? = null
    var chatKey : String? = null
    var userId : String? = null
    val deBugTag = "ChatPreview"

    init {
        image = view.findViewById<ImageView>(R.id.imageUser)
        userName = view.findViewById<TextView>(R.id.user_name)
        lastMessage = view.findViewById<TextView>(R.id.last_message)
    }

    fun bindData(ck:String, uId:String, u:String ){
        chatKey = ck
        userId = uId
        userName?.text = u

        FirebaseDatabase.getInstance().reference.child("chat").child(ck).orderByKey().limitToLast(1).addValueEventListener(object :ValueEventListener{

            override fun onDataChange(p0: DataSnapshot?) {
                val message = p0!!.children!!.iterator().next()

                lastMessage?.text = message.child("messageText").value.toString()
                if(!message.child("messageRead").value.toString().toBoolean() && message.child("messageUserId").value.toString().equals(userId)) {
                    lastMessage!!.setTypeface(null, Typeface.BOLD)
                }else{
                    lastMessage!!.setTypeface(null, Typeface.NORMAL)
                }

            }

            override fun onCancelled(p0: DatabaseError?) {
                //todo gestire
            }
        })

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
            FirebaseStorage.getInstance().getReferenceFromUrl(uri.toString()).getBytes((1024 * 1024).toLong()).addOnSuccessListener { bytes ->

                image!!.setImageDrawable(BitmapDrawable(BitmapFactory.decodeByteArray(bytes, 0, bytes.size)))

            }.addOnFailureListener { e -> Log.e(deBugTag, e.message) }
        }.addOnFailureListener { e -> Log.e(deBugTag, e.message) }
    }
}
package com.example.andrea.lab11

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.EditText
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.database.*

class PersonalChat : AppCompatActivity() {

    private var fab :FloatingActionButton? = null
    private var listOfMessages : RecyclerView? = null
    private val deBugTag : String = "PersonalChat"
    private var myUserName :String? = null
    private var myUserId :String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_chat)

        //get element from intent
        val userName = intent.getStringExtra("userName")
        val userId = intent.getStringExtra("userId")
        val chatKey = intent.getStringExtra("chat")

        //set toolbar
        val toolbarTitle = findViewById<TextView>(R.id.back_toolbar_text)
        toolbarTitle.text = userName
        findViewById<ImageButton>(R.id.imageButton).setOnClickListener(View.OnClickListener {onBackPressed()})

        //get elements
        fab = findViewById<FloatingActionButton>(R.id.fab)
        listOfMessages = findViewById<RecyclerView>(R.id.list_of_messages)

        //set myUserName and my myUserId
        val myUser = MyUser(applicationContext)
        myUserName = myUser.name + " " + myUser.surname
        myUserId = myUser.userID

        if (chatKey != null){
            receive_send_message(chatKey)
        }else{
            val dbRef = FirebaseDatabase.getInstance().reference.child("usersChat")
            dbRef.child(myUserId).orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot?) {

                    if(dataSnapshot == null || dataSnapshot.value == null){
                        val cK = myUserId + userId
                        dbRef.child(myUserId).child(cK).child("userId").setValue(userId)
                        dbRef.child(userId).child(cK).child("userId").setValue(myUserId)
                        dbRef.child(myUserId).child(cK).child("userName").setValue(userName)
                        dbRef.child(userId).child(cK).child("userName").setValue(myUserName)
                        receive_send_message(cK)
                    }else{
                        Log.d(deBugTag,dataSnapshot.children.iterator().next().key)
                        receive_send_message(dataSnapshot.children.iterator().next().key)
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(deBugTag,databaseError.getMessage()+databaseError.getCode());
                    //todo gestire
                }
            })
        }

    }

    private fun receive_send_message(chatKey:String){

        //initialization
        val dbRef = FirebaseDatabase.getInstance().reference.child("chat").child(chatKey)

        //send message
        fab?.setOnClickListener {
            val input = findViewById<EditText>(R.id.input)

            // Read the input field and push a new instance
            // of ChatMessage to the Firebase database
            dbRef.push().setValue(ChatMessageModel(input.text.toString(),myUserName!!,myUserId!!))

            // Clear the input
            input.setText("")
        }

        //set adapter
        val options = FirebaseRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(dbRef, SnapshotParser<ChatMessageModel> {snapshot ->
                    val messageUserId = snapshot.child("messageUserId").value.toString()
                    if(!messageUserId.equals(myUserId)){
                        dbRef.child(snapshot.key).child("messageRead").setValue(true)
                        dbRef.child(snapshot.key).child("messageReceived").setValue(true)
                    }
                    ChatMessageModel(snapshot.child("messageText").value.toString(),snapshot.child("messageUser").value.toString(),snapshot.child("messageTime").value.toString().toLong(),messageUserId, snapshot.child("messageRead").value.toString().toBoolean(),snapshot.child("messageReceived").value.toString().toBoolean())
                })
                .setLifecycleOwner(this)
                .build()

        val adapter = object : FirebaseRecyclerAdapter<ChatMessageModel, ChatMessage>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessage {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.message_chat, parent, false)

                return ChatMessage(view)
            }

            override fun onBindViewHolder(holder: ChatMessage, position: Int, model: ChatMessageModel) {

                holder.bindData(model.messageText!!,model.messageUser!!,model.messageTime!!, model.messageRead!!, myUserId!!, model.messageUserId!!)
            }

        }

        val layoutManager = LinearLayoutManager(applicationContext)
        layoutManager.stackFromEnd = true
        listOfMessages?.layoutManager = layoutManager
        listOfMessages?.adapter = adapter
    }
}

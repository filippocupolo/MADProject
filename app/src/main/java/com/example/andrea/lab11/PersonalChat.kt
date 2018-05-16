package com.example.andrea.lab11

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.database.FirebaseDatabase
import android.widget.EditText
import android.support.design.widget.FloatingActionButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.database.DataSnapshot

class PersonalChat : AppCompatActivity() {

    var userName : String? = null
    var userId : String? = null
    var chatKey : String? = null
    var myUserID : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_chat)

        //get element from intent
        userName = intent.getStringExtra("userName")
        userId = intent.getStringExtra("userId")
        chatKey = intent.getStringExtra("chat")

        //initialization
        val myUserName = MyUser(applicationContext).name + " " + MyUser(applicationContext).surname
        val dbRef = FirebaseDatabase.getInstance().reference.child("chat").child(chatKey)

        //set toolbar
        val toolbarTitle = findViewById<TextView>(R.id.back_toolbar_text)
        toolbarTitle.text = userName
        findViewById<ImageButton>(R.id.imageButton).setOnClickListener(View.OnClickListener {onBackPressed()})

        //get elements
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val listOfMessages = findViewById<RecyclerView>(R.id.list_of_messages)

        fab.setOnClickListener {
            val input = findViewById<View>(R.id.input) as EditText

            // Read the input field and push a new instance
            // of ChatMessage to the Firebase database
            dbRef.push().setValue(ChatMessageModel(input.text.toString(),myUserName))

            // Clear the input
            input.setText("")
        }

        //set adapter
        val options = FirebaseRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(dbRef, SnapshotParser<ChatMessageModel> {snapshot ->
                    ChatMessageModel(snapshot.child("messageText").value.toString(),snapshot.child("messageUser").value.toString(),snapshot.child("messageTime").value.toString().toLong())
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

                holder.bindData(model.messageText!!,model.messageUser!!,model.messageTime!!)
            }

        }

        listOfMessages.layoutManager = LinearLayoutManager(applicationContext)
        listOfMessages.adapter = adapter

    }
}

package com.example.andrea.lab11


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.database.*
import java.util.*

class Chat : Fragment() {

    private val deBugTag = "Chat"
    private var query: Query? = null
    private var childListener: ChildEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //initialization
        val applicationContext = activity?.applicationContext
        val list = LinkedList<ChatPreviewModel>()

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        //get elements
        val recyclerView = view.findViewById<RecyclerView>(R.id.chat_rv)
        val noChatMessage = view.findViewById<TextView>(R.id.no_chat_message)

        //set adapter
        val adapter =  object : RecyclerView.Adapter <ChatPreview>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatPreview {

                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.view_holder_chat_preview, parent, false)
                return ChatPreview(view)
            }

            override fun onBindViewHolder(holder: ChatPreview, position: Int) {

                val chat = list[position]

                holder.bindData(chat.chatKey, chat.userId, chat.userName)

            }

            override fun getItemCount(): Int {
                return list.size
            }
        }

        //set Query
        query = FirebaseDatabase.getInstance().reference.child("usersChat").orderByKey().equalTo(MyUser(applicationContext).userID)

        query?.addListenerForSingleValueEvent( object: ValueEventListener{

            override fun onDataChange(p0: DataSnapshot?) {
                if(p0==null || !p0.exists())
                    noChatMessage.visibility = View.VISIBLE
            }

            override fun onCancelled(p0: DatabaseError?) {
                //do not handle this. The error will be handle by the ChildEventListener below
            }

        })

        childListener = object : ChildEventListener {

            override fun onChildAdded( dataSnapshot:DataSnapshot?,  s:String?) {

                Log.d(deBugTag,"onChildAdded")

                if(dataSnapshot == null)
                    return

                if(noChatMessage.visibility == View.VISIBLE)
                    noChatMessage.visibility = View.GONE

                for(data in dataSnapshot.children){

                    var model = ChatPreviewModel(data.key,data.child("userId").value.toString(),data.child("userName").value.toString())
                    list.add(model)
                    adapter.notifyDataSetChanged()
                }
            }


            override fun onChildChanged(dataSnapshot:DataSnapshot,  s:String?) {

                Log.d(deBugTag,"onChildChanged")

                if(dataSnapshot == null)
                    return

                list.clear()

                for(data in dataSnapshot.children){
                    var model = ChatPreviewModel(data.key,data.child("userId").value.toString(),data.child("userName").value.toString())
                    list.add(model)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(dataSnapshot:DataSnapshot) {


                Log.d(deBugTag,"onChildRemoved")
                list.clear()
                adapter.notifyDataSetChanged()
            }

            override fun onChildMoved(dataSnapshot:DataSnapshot, s: String) {

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Chat",databaseError.getMessage()+databaseError.getCode());
            }
        }

        query?.addChildEventListener(childListener)

        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = adapter

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        query?.removeEventListener(childListener)
    }

}

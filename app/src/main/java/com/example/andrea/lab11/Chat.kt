package com.example.andrea.lab11

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.BaseAdapter
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Chat.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Chat.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Chat : Fragment() {

    val deBugTag = "Chat"

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
        val noChatMessage = view.findViewById<TextView>(R.id.no_chat_message) //todo mattere visible questo messaggio se non ci sono chat attive

        //set adapter
        val adapter =  object : RecyclerView.Adapter <ChatPreview>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatPreview {

                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.view_holder_chat_preview, parent, false)
                return ChatPreview(view)
            }

            override fun onBindViewHolder(holder: ChatPreview, position: Int) {

                val chat = list[position]

                holder.bindData(chat.chatKey, chat.userId, chat.userName,chat.lastMessage)

            }

            override fun getItemCount(): Int {
                return list.size
            }
        }

        //set Query
        val query = FirebaseDatabase.getInstance().reference.child("usersChat").orderByKey().equalTo(MyUser(applicationContext).userID)

        query.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded( dataSnapshot:DataSnapshot?,  s:String?) {

                if(dataSnapshot == null)
                    return

                for(data in dataSnapshot.children){

                    var model = ChatPreviewModel(data.key,data.child("userId").value.toString(),data.child("userName").value.toString(),data.child("lastMessage").value.toString())
                    list.add(model)
                    adapter.notifyDataSetChanged()
                }
            }


            override fun onChildChanged(dataSnapshot:DataSnapshot,  s:String) {

                if(dataSnapshot == null)
                    return

                for(data in dataSnapshot.children){
                    var model = ChatPreviewModel(data.key,data.child("userId").value.toString(),data.child("userName").value.toString(),data.child("lastMessage").value.toString())
                    list.add(model)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(dataSnapshot:DataSnapshot) {

                list.clear()
                adapter.notifyDataSetChanged()
            }

            override fun onChildMoved(dataSnapshot:DataSnapshot, s: String) {

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Chat",databaseError.getMessage()+databaseError.getCode());
                //todo gestire
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = adapter

        return view
    }

}

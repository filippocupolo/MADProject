package com.example.andrea.lab11

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.firebase.ui.database.FirebaseListAdapter
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //initialization
        val applicationContext = activity?.applicationContext

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)

        //get elements
        val recyclerView = view.findViewById<RecyclerView>(R.id.chat_rv)
        val noChatMessage = view.findViewById<TextView>(R.id.no_chat_message)

        //set Query
        val query = FirebaseDatabase.getInstance().reference.child("usersChat").orderByKey().equalTo(MyUser(applicationContext).userID)

        //get and populate list
        val options = FirebaseRecyclerOptions.Builder<ChatPreviewModel>()
                .setQuery(query,ChatPreviewModel::class.java)
                .setLifecycleOwner(activity)
                .build()

        val adapter = object : FirebaseRecyclerAdapter<ChatPreviewModel, ChatPreview>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatPreview {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.view_holder_chat_preview, parent, false)

                return ChatPreview(view)
            }

            override fun onBindViewHolder(holder: ChatPreview, position: Int, model: ChatPreviewModel) {

                holder.binData(model.user, model.lastMessage)
            }

            override fun onDataChanged() {
                super.onDataChanged()
                noChatMessage.visibility = if (itemCount == 0) View.VISIBLE else View.INVISIBLE

            }

        }

        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.adapter = adapter

        return view
    }

}

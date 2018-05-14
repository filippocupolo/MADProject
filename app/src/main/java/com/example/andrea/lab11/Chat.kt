package com.example.andrea.lab11

import android.app.Activity
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_chat.view.*
import android.widget.ArrayAdapter
import android.widget.BaseAdapter


class Chat : android.support.v4.app.Fragment() {

    private var messageList: ArrayList<ChatMessage>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = inflater?.inflate(R.layout.activity_chat, container, false)

        //list view
        val lv = rootView.findViewById(R.id.list_of_messages) as ListView
        lv.adapter = MessageListAdapter(this.requireContext())

        /*
        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        var arrayAdapter = ArrayAdapter<ChatMessage>(
                this,
                lv,
                messageList)

        //set adapter to list view - ? means to do it if not null
        lv?.adapter = arrayAdapter*/


        //new message
        var newMessageButton = rootView.findViewById<ImageButton>(R.id.fab);
        newMessageButton.setOnClickListener{

        }


        return rootView

    }

    private class MessageListAdapter(context: Context) : BaseAdapter() {

        init {
            this.mInflator = LayoutInflater.from(context)
        }

        override fun getCount(): Int {
            return sList.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view: View?
            val vh: ListRowHolder
            if (convertView == null) {
                view = this.mInflator.inflate(R.layout., parent, false)
                vh = ListRowHolder(view)
                view.tag = vh
            } else {
                view = convertView
                vh = view.tag as ListRowHolder
            }

            vh.label.text = sList[position]
            return view
        }
    }

    private class ListRowHolder(row: View?) {
        public val label: TextView

        init {
            this.label = row?.findViewById(R.id.label) as TextView
        }
    }
}

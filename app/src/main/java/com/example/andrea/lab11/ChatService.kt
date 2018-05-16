package com.example.andrea.lab11

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.app.PendingIntent
import com.example.andrea.lab11.R.mipmap.ic_launcher
import android.content.Context.NOTIFICATION_SERVICE
import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.app.NotificationChannel




class ChatService : Service(){

    val deBugTag = "ChatService"

    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(deBugTag,"onCreate")

        val userId = MyUser(applicationContext).userID

        val dbRef = FirebaseDatabase.getInstance().reference;

        dbRef.child("usersChat").child(userId).addListenerForSingleValueEvent( object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var it = dataSnapshot.children.iterator()
                while(it.hasNext()){
                    dbRef.child("chat").child(it.next().key).addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            postNotification("title","messaggio")
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            //todo gestire
                        }
                    })
                }
                dbRef.child("usersChat").child(userId).addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        postNotification("title","messaggio")
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        //todo gestire
                    }
                })
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //todo gestire
            }
        })
    }

    fun postNotification(title: String, content: String) {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel("default",
                    "YOUR_CHANNEL_NAME",
                    NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "YOUR_NOTIFICATION_CHANNEL_DISCRIPTION"
            mNotificationManager.createNotificationChannel(channel)
        }
        val mBuilder = NotificationCompat.Builder(applicationContext, "default")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(content)// message for notification
                .setAutoCancel(true) // clear notification after click
        val intent = Intent(applicationContext, MainPageActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pi)
        mNotificationManager.notify(0, mBuilder.build())
    }
}
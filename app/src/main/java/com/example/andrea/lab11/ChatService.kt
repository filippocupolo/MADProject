package com.example.andrea.lab11

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.app.PendingIntent
import android.app.NotificationManager
import android.content.Context
import android.support.v4.app.NotificationCompat
import android.app.NotificationChannel
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*


class ChatService : Service(){

    val deBugTag = "ChatService"

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(deBugTag,"onBind")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //todo studia documentazione

        val userId = MyUser(applicationContext).userID
        Log.d(deBugTag,"onStartCommand()")

        FirebaseApp.initializeApp(this)
        val dbRef = FirebaseDatabase.getInstance().reference;

        dbRef.child("usersChat").child(userId).addChildEventListener( object : ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                if(p0==null)
                    return

                dbRef.child("chat").child(p0.key).orderByChild("messageReceived").equalTo(false).addValueEventListener( object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val it = dataSnapshot.children.iterator()
                        val chat = dataSnapshot.key
                        while (it.hasNext()){
                            val data = it.next()
                            if(!data.child("messageUserId").value!!.toString().equals(userId)){
                                dbRef.child("chat").child(chat).child(data.key).child("messageReceived").setValue(true)
                                postNotification(data.child("messageUser").value.toString(),data.child("messageText").value.toString())
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                        //todo gestire
                    }
                })
            }

            override fun onCancelled(p0: DatabaseError?) {
                //todo gestire
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot?) {

            }

        })
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onCreate() {
        super.onCreate()


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
        intent.putExtra("page",2)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pi)
        mNotificationManager.notify(0, mBuilder.build())
    }
}
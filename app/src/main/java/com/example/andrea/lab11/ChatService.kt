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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList


class ChatService : Service(){

    val deBugTag = "ChatService"
    var chilListener : ChildEventListener? = null
    var dbRef : DatabaseReference? = null
    var valueListeners : CopyOnWriteArrayList<ValueEventListener>? = null

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(deBugTag,"onBind")
        return null
    }

    override fun onCreate() {

        super.onCreate()
        val userId = MyUser(applicationContext).userID
        Log.d(deBugTag,"onCreate()")

        FirebaseApp.initializeApp(this)
        dbRef = FirebaseDatabase.getInstance().reference

        valueListeners = CopyOnWriteArrayList<ValueEventListener>()

        chilListener = dbRef!!.child("usersChat").child(userId).addChildEventListener( object : ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                if(p0==null)
                    return

                val l = dbRef!!.child("chat").child(p0.key).orderByChild("messageReceived").equalTo(false).addValueEventListener( object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val it = dataSnapshot.children.iterator()
                        val chat = dataSnapshot.key
                        while (it.hasNext()){
                            val data = it.next()
                            if(!data.child("messageUserId").value!!.toString().equals(userId)){
                                dbRef!!.child("chat").child(chat).child(data.key).child("messageReceived").setValue(true)
                                Log.d(deBugTag,"notifica")
                                postNotification(data.child("messageUser").value.toString(),data.child("messageText").value.toString())
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                        //todo gestire
                    }
                })

                valueListeners!!.add(l)
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

    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(deBugTag,"onDestroy")

        //remove all listeners
        dbRef!!.removeEventListener(chilListener)
        if(valueListeners == null)
            return
        val it = valueListeners!!.iterator()

        while (it.hasNext()){
            dbRef?.removeEventListener(it.next())
            Log.d(deBugTag,"rimosso")
        }

        stopSelf()
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
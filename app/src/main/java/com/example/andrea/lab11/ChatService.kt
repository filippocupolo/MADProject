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
    var newBookListener : ChildEventListener? = null
    var myBooksListener : ChildEventListener? = null
    var newCommentListener : ChildEventListener? = null
    var dbRef : DatabaseReference? = null
    var valueListeners : ArrayList<ValueEventListener>? = null
    var myBorrowedBooks : ArrayList<String>? = null

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

        valueListeners = ArrayList<ValueEventListener>()
        myBorrowedBooks = ArrayList<String>()

        //chat notification
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
                                postChatNotification(data.child("messageUser").value.toString(),data.child("messageText").value.toString())
                            }
                        }
                    }

                    override fun onCancelled(p0: DatabaseError?) {
                    }
                })

                valueListeners!!.add(l)
            }

            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot?) {

            }

        })

        //notification to the borrower when the lent is ended
        myBooksListener = dbRef!!.child("books").addChildEventListener( object : ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
            }

            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                if(p0 == null)
                    return

                Log.d(deBugTag, "changed: ${p0.key}")

                if(myBorrowedBooks?.contains(p0.key) ?: return) {
                    if (p0.child("status")?.value!!.toString().equals("0")) {
                        //i had this book and now it's terminated
                        postDoCommentNotification("Il tuo prestito è terminato", "Lascia un commento", p0.child("owner")?.value!!.toString())
                        myBorrowedBooks?.remove(p0.key)
                    }
                }
                else{
                    if (p0.hasChild("borrower"))
                        if (p0.child("borrower")?.value!!.toString().equals(userId))
                            myBorrowedBooks?.add(p0.key)
                }

            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot?) {

            }
        })

        //now check if there are requests for my books and send notification
        newBookListener = dbRef!!.child("bookRequests").orderByChild("bookOwner").equalTo(userId).addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                if(p0 == null)
                    return

                Log.d(deBugTag, "$p0");
                //if(myBooks?.contains(p0.key) ?: return)
                if(p0.child("notificationSent").value!!.toString().equals("false")) {
                    postNewBookNotification(getString(R.string.newBookRequest), p0.key)
                    p0.child("notificationSent").ref.setValue("true")
                }
            }

            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
            }
        })

        //new comments notification
        newCommentListener = dbRef!!.child("commentsDB").child(userId).child("comments").addChildEventListener(object : ChildEventListener{
            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                if(p0 == null)
                    return

                if(p0.child("commentRead").value?.equals(false) ?: return) {
                    postNewCommentNotification(getString(R.string.newCommentReceived),
                            p0.child("userNameSurname").value?.toString() ?: "", userId)
                    p0.child("commentRead").ref.setValue(true)
                }
            }

            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot?) {

            }
        })

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(deBugTag,"onDestroy")

        //remove all listeners
        dbRef!!.removeEventListener(chilListener)
        dbRef!!.removeEventListener(myBooksListener)
        dbRef!!.removeEventListener(newBookListener)
        dbRef!!.removeEventListener(newCommentListener)

        if(valueListeners == null)
            return
        val it = valueListeners!!.iterator()

        while (it.hasNext()){
            dbRef?.removeEventListener(it.next())
            Log.d(deBugTag,"rimosso")
        }

        myBorrowedBooks?.clear()

        FirebaseDatabase.getInstance().goOffline()
    }

    fun postChatNotification(title: String, content: String) {
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

    fun postNewBookNotification(title: String, bookRequestedKId: String){
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
                .setContentText(getString(R.string.checkBookRequest))// message for notification
                .setAutoCancel(true) // clear notification after click
        val intent = Intent(applicationContext, ShowBook::class.java)
        intent.putExtra("bookId",bookRequestedKId)
        intent.putExtra("showProfile", true)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pi)
        mNotificationManager.notify(0, mBuilder.build())
    }

    fun postNewCommentNotification(title: String, user: String, myUserId: String){
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
                .setContentTitle("$title $user") // title for notification
                .setContentText(getString(R.string.checkBookRequest))// message for notification
                .setAutoCancel(true) // clear notification after click
        val intent = Intent(applicationContext, showProfile::class.java)
        intent.putExtra("userId",myUserId)
        intent.putExtra("newComment", "true")
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pi)
        mNotificationManager.notify(0, mBuilder.build())
    }

    fun postDoCommentNotification(title: String, subtitle: String , userId: String){
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
                .setContentText(subtitle)// message for notification
                .setAutoCancel(true) // clear notification after click
        val intent = Intent(applicationContext, CommentActivity::class.java)
        intent.putExtra("userId",userId)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pi)
        mNotificationManager.notify(0, mBuilder.build())
    }
}
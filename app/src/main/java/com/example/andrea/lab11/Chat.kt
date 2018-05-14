package com.example.andrea.lab11

    import android.app.Activity
    import android.app.Fragment
    import android.support.v7.app.AppCompatActivity
    import android.os.Bundle
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup

    class Chat : Fragment() {

        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            var rootView = inflater?.inflate(R.layout.activity_chat, container, false)

            return rootView


            /*
            override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
                var rootView = inflater?.inflate(R.layout.activity_chat, container, false)

                return rootView
            }*/
        }
    }

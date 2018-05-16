package com.example.andrea.lab11

import java.util.*


class ChatMessageModel {

    var messageText: String? = null
    var messageUser: String? = null
    var messageTime: Long = 0

    constructor(messageText: String, messageUser: String) {
        this.messageText = messageText
        this.messageUser = messageUser

        // Initialize to current time
        messageTime = Date().getTime()
    }

    constructor(messageText: String, messageUser: String, messageTime:Long) {
        this.messageText = messageText
        this.messageUser = messageUser
        this.messageTime = messageTime
    }
}
package com.example.andrea.lab11

import java.util.*


class ChatMessageModel {

    var messageText: String? = null
    var messageUser: String? = null
    var messageTime: Long = 0
    var messageUserId: String? = null
    var messageRead: Boolean? = null

    constructor(messageText: String, messageUser: String,messageUserId: String) {
        this.messageText = messageText
        this.messageUser = messageUser
        this.messageUserId = messageUserId

        // Initialize to current time
        messageTime = Date().getTime()
        messageRead = false
    }

    constructor(messageText: String, messageUser: String, messageTime:Long,messageUserId: String, messageRead: Boolean) {
        this.messageText = messageText
        this.messageUser = messageUser
        this.messageTime = messageTime
        this.messageUserId = messageUserId
        this.messageRead = messageRead
    }
}
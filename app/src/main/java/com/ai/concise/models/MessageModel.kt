package com.ai.concise.models

data class MessageModel(

    var isUser : Boolean,
    var isImage : Boolean,
    var message : String,
    var SENT_BY_BOT: String? = "bot"
)

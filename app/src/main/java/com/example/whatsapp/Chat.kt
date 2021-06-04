package com.example.whatsapp

class Chat{


    private var sender:String=""
    private var reciver:String=""
    private var message:String=""
    private var isseen=false
    private var messageId:String=""
    private var url:String=""

    constructor()
    constructor(
        sender: String,
        reciver: String,
        message: String,
        isseen: Boolean,
        messageId: String,
        url: String
    ) {
        this.sender = sender
        this.reciver = reciver
        this.message = message
        this.isseen = isseen
        this.messageId = messageId
        this.url = url
    }

    fun getsender():String
    {
        return sender

    }
    fun setsender(sender: String)
    {
        this.sender=sender
    }
    fun getreciver():String
    {
        return reciver

    }
    fun setreciver(reciver: String)
    {
        this.reciver=reciver
    }
    fun getmessage():String
    {
        return message

    }
    fun setmessage(message: String)
    {
        this.message=message
    }
    fun getisseen():Boolean
    {
        return isseen

    }
    fun setisseen(isseen: Boolean)
    {
        this.isseen=isseen
    }
    fun getmessageId():String
    {
        return messageId

    }
    fun setmessageId(messageId: String)
    {
        this.messageId=messageId
    }
    fun geturl():String
    {
        return url

    }
    fun seturl(url: String)
    {
        this.url=url
    }


}
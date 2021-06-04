package com.example.whatsapp

class Users {

    private  var uid:String=""
    private  var profile:String=""
    private  var cover:String=""
    private  var username:String=""
    private  var status:String=""
    private  var search:String=""
    private  var facebook:String=""
    private  var instagram:String=""


    constructor()
    constructor(
        uid: String,
        profile: String,
        cover: String,
        username: String,
        status: String,
        search: String,
        facebook: String,
        instagram: String
    ) {
        this.uid = uid
        this.profile = profile
        this.cover = cover
        this.username = username
        this.status = status
        this.search = search
        this.facebook = facebook
        this.instagram = instagram
    }

    fun getuid():String
    {
        return  uid

    }
    fun setuid(uid: String)
    {
        this.uid=uid
    }
    fun getprofile():String
    {
        return  profile

    }
    fun setprofile(profile: String)
    {
        this.profile=profile
    }
    fun getcover():String
    {
        return  cover

    }
    fun setcover(cover: String)
    {
        this.cover=cover
    }
    fun getusername():String
    {
        return  username

    }
    fun setusername(username: String)
    {
        this.username=username
    }
    fun getstatus():String
    {
        return  status

    }
    fun setstatus(status: String)
    {
        this.status=status
    }
    fun getsearch():String
    {
        return  search

    }
    fun setsearch(search: String)
    {
        this.search=search
    }
    fun getfacebook():String
    {
        return  facebook

    }
    fun setfacebook(facebook: String)
    {
        this.facebook=facebook
    }
    fun getinstagram():String
    {
        return  instagram

    }
    fun setinstagram(instagram: String)
    {
        this.instagram=instagram
    }
}
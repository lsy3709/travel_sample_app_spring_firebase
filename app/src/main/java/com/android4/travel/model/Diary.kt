package com.android4.travel.model

data class Diary(
    var dno:Int,
    var title:String,
    var content:String,
    var date: String,
    var on_off: String,
    var hitcount:Int,
    var good:Int,
    var trip_id:String,
    var image_uri: String
)

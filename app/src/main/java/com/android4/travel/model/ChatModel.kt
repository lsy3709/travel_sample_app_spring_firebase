package com.android4.travel.model

//기존의 것 -> data class Comment 변경
class ChatModel(val member: ArrayList<String>,
//                val chatRoomId: HashMap<String, Boolean>,
                val comments: HashMap<String, Comment> = HashMap()){

    class Comment(val username: String? = null,
                  val message: String? = null,
                  val time: String? = null)
}
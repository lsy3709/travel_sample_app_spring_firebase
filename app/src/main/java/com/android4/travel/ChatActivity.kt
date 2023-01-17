package com.android4.travel

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android4.travel.adapter.DateAdapter
import com.android4.travel.adapter.InputAdapter
import com.android4.travel.databinding.ActivityChatBinding
import com.android4.travel.model.Comment
import com.android4.travel.model.Trip
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    lateinit var binding : ActivityChatBinding
    //Global field
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private var chatRoomId :String? = null
    private var destinationUid : String? = null
    private var memberList: List<String>? = null
    private var recyclerView : RecyclerView? = null
    //
    private var tripId: String? = null
    private lateinit var comments:ArrayList<Comment>
    lateinit var chatAdapt:ChatListAdapter
    lateinit var username: String
    var nickname: String? = null

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        comments = ArrayList<Comment>()
        chatAdapt = ChatListAdapter(comments)
//view binding
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
//
//        val database = Firebase.database
//        val myRef = database.getReference("username")
//
//        myRef.get().addOnCompleteListener {
//            username = it.result.value.toString()
//
//            Log.d("test","main==================================$$username")
//
//            val networkService = (applicationContext as MyApplication).networkService
//            var oneUserCall = networkService.doGetOneUser(username)
//            oneUserCall.enqueue(object: Callback<User> {
//                override fun onResponse(call: Call<User>, response: Response<User>) {
//                    nickname = response.body()?.nickname.toString()
//
//                    Log.d("test", "===================nickname: $nickname")
//                }
//
//                override fun onFailure(call: Call<User>, t: Throwable) {
//                    call.cancel()
//                }
//
//            })
//        }

        val intent = intent
        var title = intent.getStringExtra("title")
        nickname = intent.getStringExtra("nickname")

        Log.d("pjh","$title===================")
        val networkService = (applicationContext as MyApplication).networkService
        val oneTripCall = networkService.doGetOneTrip(title)
        oneTripCall.enqueue(object: Callback<Trip> {
            override fun onResponse(call: Call<Trip>, response: Response<Trip>) {

                var trip = response.body()

                var check = trip?.start_date?:0
                Log.d("pjh", "${convertLongToDate(check)}=========================")
                var dates = arrayListOf<Long>()

                do {
                    dates.add(check)

                    check += 86400000L

                } while( check <= (trip?.end_date?:0))

                val layoutManager = LinearLayoutManager(this@ChatActivity)
                layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                binding.recyclerView.layoutManager = layoutManager
                binding.recyclerView.adapter = DateAdapter(dates)

                binding.tvDate.text = convertLongToDate(trip?.start_date?:0)
                binding.tvDay.text = " ~ "+convertLongToDate(trip?.end_date?:0)
                binding.textView10.text = trip?.title

                var layoutManager2 = LinearLayoutManager(this@ChatActivity)
                layoutManager2.orientation = LinearLayoutManager.HORIZONTAL

                binding.recyclerView2.layoutManager = layoutManager2

                val member:List<String>? = trip?.member?.split(",")
//
//                var pref = getSharedPreferences("inputPref", Context.MODE_PRIVATE)
//                if(member?.get(0) ?:null ==pref.getString("userName","aaa"))
                binding.recyclerView2.adapter = InputAdapter(member,nickname)
                Log.d("test","chat------------------------$nickname")

                var place = trip?.place
                binding.textView2.text = place
                binding.button8.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.kr/maps/search/$place"))

                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<Trip>, t: Throwable) {
                call.cancel()
            }

        })
//chat
        var editMsgBox = findViewById<TextView>(R.id.chatMsgEdit).text
        recyclerView = findViewById(R.id.chatListRecyclerView)


        //메시지 보낸 시간
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("MM월dd일 hh:mm")//한국 날짜 시간 변경
        val curTime = dateFormat.format(Date(time)).toString()
//받아온 데이터 : 멤버리스트, 여행 id, 여행 제목
        memberList = intent.getStringExtra("member")?.split(",") //배열형으로 출력.[a1, a2, a3]
//        Log.d("test", "chat Mem.....$memberList")
        val chatTitle = intent.getStringExtra("title")
        binding.chatTitle.text = "$chatTitle"

//방에 입장하면, 파이어베이스에 저장되도록! - 채팅방 번호, 멤버
        tripId = intent.getStringExtra("title")
        Log.d("test", "..................$tripId")
        if(chatRoomId == null){
//            Log.d("test","fbase.1..${fireDatabase.child("chatroom")}")
//            Log.d("test","${fireDatabase.child("chatrooms").child(tripId.toString()).child("chatmembers").get()}")
            Log.d("test",memberList.toString())
            fireDatabase.child("chatrooms").child(tripId.toString()).child("chatmembers")
                .addListenerForSingleValueEvent(object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(member in memberList!!){
                            Log.d("test",">>>>> ${snapshot.children}")
                            for(data in snapshot.children){
                                Log.d("test",">>>>>${data.value} $member")
                                if(data.value==member){
                                    return
                                }
                                Log.d("test",data.toString())
                            }
                            fireDatabase.child("chatrooms").child(tripId.toString())
                                .child("chatmembers")
                                .push().setValue(member)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
        getMessageList() //List 호출

//메세지 전송 버튼 클릭 시
        binding.chatSendBtn.setOnClickListener {
            val member = nickname //멤버 지정 - 추후 로그인한 사용자(자신)
            val comment = Comment(member, editMsgBox.toString(), curTime)
            Log.d("test", "chat...cmt...$comment")
            Log.d("chat",".....$member")
            Log.d("chat",".....$editMsgBox")
            Log.d("chat",".....$curTime")
            Handler().postDelayed({
                fireDatabase.child("chatrooms").child(tripId.toString()).child("comments").addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                        }
                        override fun onDataChange(snapshot: DataSnapshot) {
                            fireDatabase.child("chatrooms").child(tripId.toString()).child("comments").push().setValue(comment)
                            getMessageList()
                            chatAdapt.notifyDataSetChanged()
                        }
                    })
            }, 500L)
        }
        //editText 입력된 문자열 초기화 ?
//        editMsgBox = null
//comment 저장하는 코드
//fireDatabase.child("chatrooms").child(tripId.toString()).child("comments").push().setValue(comment)

    }
    //메시지 리스트 뿌리기
    private fun getMessageList(){
        fireDatabase.child("chatrooms").child(tripId.toString()).child("comments")
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    comments.clear()
                    for(data in snapshot.children){
//                        val item = data.getValue<ChatModel.Comment>()
                        Log.d("chat", ".........$data")
                        //원래는 받아져야 하는데 Class
                        val item=data.getValue() as HashMap<String, Any?>

                        Log.d("chat", ">>>>>>>${item.get("time")}")
                        Log.d("chat", ">>>>>>>${item.get("message")}")
                        Log.d("chat", ">>>>>>>${item.get("username")}")
//                        comments.add(item.get())
//                        Log.d("chat", "item....."+item)
//                        val comment = Comment("", "", "")
                        val comment = Comment(item.get("username").toString(), item.get("message").toString(), item.get("time").toString())
                        comments.add(comment)
//                        comments.add(item.get().)
//                        comments.addAll(item as Collection<Comment>)
                        println(comments)
                    }
                    recyclerView?.layoutManager = LinearLayoutManager(this@ChatActivity)
                    chatAdapt = ChatListAdapter(comments)
                    recyclerView?.adapter = chatAdapt
//                    recyclerView?.addItemDecoration(DividerItemDecoration(this@ChatActivity,LinearLayoutManager.VERTICAL))
//                    chatAdapt.checkMsg() //전체 새로고침
                    Log.d("chat","comments..Suc...$comments")
                    recyclerView?.scrollToPosition(comments.size -1)
                }
            })
    }
    //recyclerViewAdapter
    inner class ChatListAdapter(val comments: List<Comment>): RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {
        //        private val comments = ArrayList<Comment>()
        //채팅 메시지 레이아웃 붙이기
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
            return ChatListViewHolder(view)
        }
        //원래 코드
//        fun checkMsg(){
//            notifyDataSetChanged() //전체 새로고침
//            recyclerView?.scrollToPosition(comments.size -1)
//        }
//
        fun checkMsg(){
            fireDatabase.child("chatrooms").child(tripId.toString()).child("comments")
                .addListenerForSingleValueEvent(object: ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        getMessageList()
                        notifyDataSetChanged()
                        recyclerView?.scrollToPosition(comments.size -1)
                    }
                })
        }
        override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
            val member = nickname
            Log.d("chat","member>>>>>>>$member")
            holder.msgUsername.text = comments[position].username
            holder.msgTxt.text = comments[position].message
            holder.msgTime.text = comments[position].time
//            if(comments[position].username.equals()){} //데이터 가져와서 본인 체크
            if(comments[position].username.equals(member)){ //본인 체크
//                holder.msgTxt.setBackgroundColor(Color.parseColor("#619CD6"))
                holder.msgTxt.setBackgroundResource(R.drawable.s_chat_ri)
                holder.msgTxt.setTextColor(Color.parseColor("#FFFFFF"))
                holder.msgProfileImg.visibility = View.INVISIBLE
                holder.msgUsername.visibility = View.VISIBLE
                holder.msgParentLayout.gravity = Gravity.RIGHT //최상위 부모 레이아웃 - 레이아웃 오른쪽
                holder.msgBgColor.gravity = Gravity.RIGHT
            }else{//상대
                Glide.with(holder.msgProfileImg)//.context
                    .load(R.drawable.apeach002)
//            .load(trip?.profileImg)
                    .override(50, 50)
                    .placeholder(R.drawable.apeach002)
                    .error(R.drawable.error)
                    .into(holder.msgProfileImg)
                holder.msgTxt.setBackgroundResource(R.drawable.s_chat_le)
                holder.msgProfileImg.visibility = View.VISIBLE
                holder.msgUsername.text = comments[position].username //채팅방에 있는 멤버들 중 채팅메시지-친구가 일치하는 거
                holder.msgUsername.visibility = View.VISIBLE
                holder.msgParentLayout.gravity = Gravity.LEFT
                holder.msgBgColor.gravity = Gravity.LEFT
            }
        }

        override fun getItemCount(): Int {
            return comments.size
        }
        //ViewHolder - 각각에 데이터 붙이기
        inner class ChatListViewHolder(view: View): RecyclerView.ViewHolder(view){
            val msgTxt: TextView = view.findViewById(R.id.chatMsg)//
            val msgUsername: TextView = view.findViewById(R.id.chatMsgUsername)
            val msgProfileImg: ImageView = view.findViewById(R.id.chatMsgImg)
            val msgLayout: LinearLayout = view.findViewById(R.id.chatMsgLayout)
            val msgParentLayout: LinearLayout = view.findViewById(R.id.chatMagParentLayout)
            val msgTime: TextView = view.findViewById(R.id.chatMsgTime)
            val msgBgColor: LinearLayout = view.findViewById(R.id.chatMsgLayoutBg)
        }
    }
    private fun convertLongToDate(time: Long):String {

        val date = Date(time)
        val format = SimpleDateFormat(
            "MM.dd",
            Locale.getDefault()
        )

        return format.format(date)
    }
}


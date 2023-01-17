package com.android4.travel.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android4.travel.ChatActivity
import com.android4.travel.R
import com.android4.travel.databinding.ItemTripListBinding
import com.android4.travel.fragment.ListFragment

import com.android4.travel.model.Trip
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyTripListViewHolder(val binding: ItemTripListBinding): RecyclerView.ViewHolder(binding.root)

class MyTripListAdapter(val context: ListFragment, val datas:List<Trip>?, val nickname: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var tripList: ArrayList<Trip>? = null
    private var num=datas?.size
    private var check_num=0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = MyTripListViewHolder(ItemTripListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

//text 가져올 때,
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyTripListViewHolder).binding
        //여행 정보 받아와야함

        var trip = datas?.get(position)
        var arrayTrip=trip?.member?.split(",")

        binding.tripListTitle.text = trip?.title
        binding.tripListMem.text = trip?.member
        Log.d("test", "${trip?.member}")
        binding.tripListStartDate.text = convertLongToDate(trip?.start_date)
        binding.tripListEndDate.text = convertLongToDate(trip?.end_date)

//Image 가져올 때,
        Glide.with(context)
            .load(R.drawable.apeach002)
//            .load(trip?.profileImg)
            .override(50, 50)
            .placeholder(R.drawable.apeach002)
            .error(R.drawable.error)
            .into(binding.tripListImg)

//터치시 채팅방으로 이동
        holder.binding.tripListItem.setOnClickListener {
                val intent = Intent(context.activity, ChatActivity::class.java) //fragment -> activity Intent
                intent.putExtra("title", datas?.get(position)?.title)
                intent.putExtra("member", trip?.member.toString())
                intent.putExtra("nickname",nickname)
            Log.d("test", "tAdapt.....Mem...${trip?.member}")
                context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return num ?:0
    }
}
private fun convertLongToDate(time: Long?):String {

    val date = Date(time?:0)
    val format = SimpleDateFormat(
        "yyyy.MM.dd",
        Locale.getDefault()
    )

    return format.format(date)
}
// item_trip_list.xml의 id : tripListImg  tripListTitle  tripListStartDate  tripListEndDate
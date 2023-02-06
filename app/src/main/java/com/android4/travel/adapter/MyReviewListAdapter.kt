package com.android4.travel.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android4.travel.ChatActivity
import com.android4.travel.DiaryFiles.DiaryDetailActivity
import com.android4.travel.R
import com.android4.travel.databinding.ItemDiaryListBinding
import com.android4.travel.databinding.ItemTripListBinding
import com.android4.travel.fragment.ListFragment
import com.android4.travel.fragment.ReviewFragment
import com.android4.travel.model.Diary

import com.android4.travel.model.Trip
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyReviewListViewHolder(val binding: ItemDiaryListBinding): RecyclerView.ViewHolder(binding.root)

class MyReviewListAdapter(val context: ReviewFragment, val datas:List<Diary>?, val nickname: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var diaryList: ArrayList<Trip>? = null
    private var num=datas?.size
    private var check_num=0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
            = MyReviewListViewHolder(ItemDiaryListBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    //text 가져올 때,
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyReviewListViewHolder).binding
        //여행 정보 받아와야함

        var diary = datas?.get(position)
        var tripId=diary?.trip_id

        binding.diaryListTitle.text = diary?.title
        binding.diaryListStartDate.text = diary?.date
        //binding.diaryListEndDate.text = convertLongToDate(diary?.end_date)

//Image 가져올 때,
        Glide.with(context)
            .load(R.drawable.apeach002)
//            .load(trip?.profileImg)
            .override(50, 50)
            .placeholder(R.drawable.apeach002)
            .error(R.drawable.error)
            .into(binding.diaryListImg)

//터치시 채팅방으로 이동
        holder.binding.diaryListItem.setOnClickListener {
            val intent = Intent(context.activity, DiaryDetailActivity::class.java) //fragment -> activity Intent
            intent.putExtra("title", datas?.get(position)?.title)
            intent.putExtra("tate", diary?.date)
            intent.putExtra("content",diary?.content)
//            Log.d("test", "tAdapt.....Mem...${trip?.member}")
            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return num ?:0
    }
}

// item_trip_list.xml의 id : tripListImg  tripListTitle  tripListStartDate  tripListEndDate
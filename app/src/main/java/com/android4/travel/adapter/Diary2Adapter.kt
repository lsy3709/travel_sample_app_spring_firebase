package com.android4.travel.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android4.travel.DiaryFiles.DiaryDetailActivity
import com.android4.travel.DiaryFiles.TripReviewActivity
import com.android4.travel.databinding.ItemDiary2Binding
import com.android4.travel.databinding.ItemDiaryBinding
import com.android4.travel.model.Diary
//import com.android4.travel.model.LoginList


class DiaryViewHolder2(val binding: ItemDiary2Binding): RecyclerView.ViewHolder(binding.root)

class Diary2Adapter(val context:Context, val datas:List<Diary>?):RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    //뷰홀더가 생성 되었을때
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        DiaryViewHolder2(
            ItemDiary2Binding.inflate(LayoutInflater.from(parent.context)
        ,parent,false))




//데이터와 레이아웃을 연결하는 함수,뷰와 뷰홀더가 묶였을 때
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as DiaryViewHolder2).binding
        val diary = datas?.get(position)


        binding.listTitleId2.text=diary?.title
        binding.listDateId2.text=diary?.date


    }
    //목록 아이템 수
    override fun getItemCount(): Int {

        return (datas?.size) ?:0
    }

}




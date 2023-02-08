package com.android4.travel.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android4.travel.DiaryFiles.DiaryDetailActivity
import com.android4.travel.DiaryFiles.TripReviewActivity
import com.android4.travel.databinding.ItemDiaryBinding
import com.android4.travel.model.Diary
//import com.android4.travel.model.LoginList


class DiaryViewHolder(val binding: ItemDiaryBinding): RecyclerView.ViewHolder(binding.root)

class DiaryAdapter(val context:Context, val datas:List<Diary>?):RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    //뷰홀더가 생성 되었을때
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        DiaryViewHolder(ItemDiaryBinding.inflate(LayoutInflater.from(parent.context)
        ,parent,false))




//데이터와 레이아웃을 연결하는 함수,뷰와 뷰홀더가 묶였을 때
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as DiaryViewHolder).binding
        val diary = datas?.get(position)


        binding.listTitleId.text=diary?.title
        binding.listDateId.text=diary?.date

    if(diary?.on_off=="비공개"){
        binding.listTitleId.setOnClickListener {
            val intent= Intent(holder.itemView?.context, DiaryDetailActivity::class.java)
            intent.putExtra("dno",diary?.dno)
            intent.putExtra("listTitle", diary?.title)
            intent.putExtra("listDate",diary?.date)
            intent.putExtra("listContent",diary?.content)
            intent.putExtra("listImage_url", diary?.image_uri )



            ContextCompat.startActivity(holder.itemView.context,intent,null)
        }
    }
    if(diary?.on_off=="공개"){
        binding.listTitleId.setOnClickListener {
            val intent= Intent(holder.itemView?.context,TripReviewActivity::class.java)
            intent.putExtra("listTitle", diary?.title)
            intent.putExtra("listDate",diary?.date)
            intent.putExtra("listContent",diary?.content)

            ContextCompat.startActivity(holder.itemView.context,intent,null)
        }
    }

//        binding.pictureExistsImageView.

    }
    //목록 아이템 수
    override fun getItemCount(): Int {

        return (datas?.size) ?:0
    }

}




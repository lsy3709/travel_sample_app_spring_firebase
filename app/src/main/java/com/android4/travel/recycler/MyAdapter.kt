package com.android4.travel.recycler

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.android4.travel.DiaryFiles.DiaryActivity
import com.android4.travel.DiaryFiles.FireDbDetailActivity
import com.android4.travel.MainActivity

import com.android4.travel.MyApplication
import com.android4.travel.MyApplication.Companion.storage
import com.android4.travel.MyApplication_FB
import com.android4.travel.databinding.ItemMainBinding
import com.android4.travel.model.ItemData
import com.android4.travel.util.deleteImage
import com.android4.travel.util.deleteStore
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MyViewHolder(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root)

class MyAdapter(val context: Context, val itemList: MutableList<ItemData>): RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MyViewHolder(ItemMainBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = itemList.get(position)

        holder.binding.run {
            itemEmailView.text=data.email
            itemDateView.text=data.date
            itemContentView.text=data.content
        }

        holder.binding.updateFBtn.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("게시글 수정")
                .setMessage("수정 하시겠습니까?")
                .setIcon(android.R.drawable.ic_menu_edit)
                .setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                    // "예"를 선택했을 때의 Action
                    //스토어 수정


                    //스토리지 수정


                    val intent = Intent(holder.itemView?.context, FireDbDetailActivity::class.java)
                    intent.putExtra("email","${data.email}")
                    intent.putExtra("date","${data.date}")
                    intent.putExtra("content","${data.content}")
                    intent.putExtra("docId","${data.docId}")
                    startActivity(holder.itemView.context,intent,null)


                })
                .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                    // "아니오"를 선택했을 때의 Action
                })
                .show()
        }

        holder.binding.deleteFBtn.setOnClickListener {

            AlertDialog.Builder(context)
                .setTitle("게시글 삭제")
                .setMessage("삭제하시겠습니까?")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                    // "예"를 선택했을 때의 Action
                    //스토어 삭제
                    data.docId?.let { it1 -> deleteStore(it1) }

                    //스토리지 삭제
                    data.docId?.let { it1 -> deleteImage(it1) }

                    val intent = Intent(context,MainActivity::class.java)
                    context.startActivity(intent)

                })
                .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                    // "아니오"를 선택했을 때의 Action
                })
                .show()
        }



        //스토리지 이미지 다운로드........................
        val imgRef = MyApplication.storage.reference.child("images/${data.docId}.jpg")
        imgRef.downloadUrl.addOnCompleteListener{ task ->
            if(task.isSuccessful){
                Glide.with(context)
                    .load(task.result)
                    .into(holder.binding.itemImageView)
            }
        }

        //스토리지 비디오 다운로드........................
        val videoViewRef = MyApplication.storage.reference.child("images/${data.docId}.mp4")
        videoViewRef.downloadUrl.addOnCompleteListener{ task ->
            if(task.isSuccessful){
                Log.d("lsy","비디오 경로1 task.result: ${task.result}")
                Log.d("lsy","비디오 경로2 task.result.toString: ${task.result.toString()}")
                val mc = MediaController(context) // 비디오 컨트롤 가능하게(일시정지, 재시작 등)
                holder.binding.itemVideoView.setMediaController(mc)
                holder.binding.itemVideoView.setVideoURI(task.result)

            }
        }

    }


 }


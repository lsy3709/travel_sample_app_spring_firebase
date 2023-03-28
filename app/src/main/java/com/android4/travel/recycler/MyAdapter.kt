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
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.android4.travel.DiaryFiles.DiaryActivity
import com.android4.travel.MainActivity

import com.android4.travel.MyApplication
import com.android4.travel.MyApplication.Companion.storage
import com.android4.travel.MyApplication_FB
import com.android4.travel.databinding.ItemMainBinding
import com.android4.travel.model.ItemData
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
    }

    fun deleteStore(docId: String){
        //delete............................
        MyApplication.db.collection("news")
            .document(docId)
            .delete()

    }
    fun deleteImage(docId: String){
        //add............................
        val storage = MyApplication.storage
        val storageRef = storage.reference
        val imgRef = storageRef.child("images/${docId}.jpg")
        imgRef.delete()

//            val file = Uri.fromFile(File(filePath))
//            imgRef.putFile(file)
//                .addOnSuccessListener {
//                    Toast.makeText(this, "save ok..", Toast.LENGTH_SHORT).show()
//                    finish()
//                }
//                .addOnFailureListener{
//                    Log.d("kkang", "file save error", it)
//                }

    }

}
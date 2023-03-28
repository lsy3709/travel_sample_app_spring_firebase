package com.android4.travel.DiaryFiles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android4.travel.MyApplication
import com.android4.travel.R
import com.android4.travel.databinding.ActivityFireDbDetailBinding
import com.android4.travel.databinding.ActivityLoginBinding
import com.bumptech.glide.Glide

class FireDbDetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityFireDbDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFireDbDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val email = intent.getStringExtra("email")
        val date = intent.getStringExtra("date")
        val content = intent.getStringExtra("content")
        val docId = intent.getStringExtra("docId")

        Log.d("lsy","값 확인: email : $email")
        Log.d("lsy","값 확인: email : $date")
        Log.d("lsy","값 확인: email : $content")
        Log.d("lsy","값 확인: email : $docId")

        binding.itemEmailView.text = email
        binding.itemDateView.text = date
        binding.itemContentView.hint = content

        //스토리지 이미지 다운로드........................
        val imgRef = MyApplication.storage.reference.child("images/${docId}.jpg")
        imgRef.downloadUrl.addOnCompleteListener{ task ->
            if(task.isSuccessful){
                Glide.with(this)
                    .load(task.result)
                    .into(binding.itemImageView)
            }
        }

    }
}
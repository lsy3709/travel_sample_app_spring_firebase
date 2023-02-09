package com.android4.travel.DiaryFiles

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android4.travel.MyApplication
import com.android4.travel.adapter.Diary2Adapter

import com.android4.travel.databinding.ActivityDiary2Binding

import com.android4.travel.model.Diary
import com.android4.travel.model.DiaryListModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Diary2Activity : AppCompatActivity(){
    lateinit var binding: ActivityDiary2Binding
    //lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiary2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rg2.setOnCheckedChangeListener { radioGroup, i ->
            var rb = findViewById<RadioButton>(i)
            if(rb!=null)
                binding.rsCheck.setText(rb.text.toString())

        }
    }

    //동기화
    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()
        binding.btnMyDiary2.setOnClickListener {
            Toast.makeText(this,"제목 클릭 자세히 보기",Toast.LENGTH_SHORT).show()

            val networkService = (applicationContext as MyApplication).networkService
            val loginSharedPref = getSharedPreferences("login_prof", Context.MODE_PRIVATE)
            val LoginId = loginSharedPref.getString("username","default")
            val diaryListCall = networkService.doGetDiaryList(LoginId)

            diaryListCall.enqueue(object: Callback<DiaryListModel>{
                override fun onResponse(call: Call<DiaryListModel>, response: Response<DiaryListModel>
                ) {
                    if(response.isSuccessful){
                        Log.d("test","======================================testtest")
//                        var DiaryAdapter = DiaryAdapter()
                        binding.recyclerDiaryView2.layoutManager = LinearLayoutManager(this@Diary2Activity)
                        binding.recyclerDiaryView2.adapter = Diary2Adapter(this@Diary2Activity,response.body()?.diarys)
                        binding.recyclerDiaryView2.addItemDecoration(DividerItemDecoration(this@Diary2Activity,LinearLayoutManager.VERTICAL))

                    }

                }

                override fun onFailure(call: Call<DiaryListModel>, t: Throwable) {
                    call.cancel()
                }

            })
        }
    }



}





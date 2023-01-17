package com.android4.travel.DiaryFiles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android4.travel.MyApplication
import com.android4.travel.databinding.ActivityDiaryDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiaryDetailActivity : AppCompatActivity() {
    lateinit var binding:ActivityDiaryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDiaryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("listTitle")
        val date=intent.getStringExtra("listDate")
        val content=intent.getStringExtra("listContent")

        binding.listDateId1.setText(date)
        binding.listTitleId1.setText(title)
        binding.contentsTextView.setText(content)



    }

    override fun onStart() {
        super.onStart()

        binding.btnDelete.setOnClickListener {
            val networkService=(applicationContext as MyApplication).networkService

            val dno =intent.getIntExtra("dno",0)

            val requestCall:Call<Unit> = networkService.delete(dno)
            requestCall.enqueue(object :Callback<Unit>{
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    Toast.makeText(this@DiaryDetailActivity,"success",Toast.LENGTH_SHORT).show()
                    val intent= Intent(this@DiaryDetailActivity, DiaryActivity::class.java)
                    startActivity(intent)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(this@DiaryDetailActivity,"fail",Toast.LENGTH_SHORT).show()
                }

            })


        }
    }
}







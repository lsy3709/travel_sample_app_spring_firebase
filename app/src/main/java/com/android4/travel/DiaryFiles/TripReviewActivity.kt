package com.android4.travel.DiaryFiles

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android4.travel.MyApplication
import com.android4.travel.databinding.ActivityTripReviewBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TripReviewActivity : AppCompatActivity() {
    lateinit var binding:ActivityTripReviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTripReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val title = intent.getStringExtra("listTitle")
        val date=intent.getStringExtra("listDate")
        val content=intent.getStringExtra("listContent")
        //val LoginId=intent.getStringExtra("LoginId")

        binding.TripDateId.setText(date)
        binding.TripTitleId.setText(title)
        binding.TripContentId.setText(content)
        //binding.LoginId.setText(LoginId)
    }

    override fun onStart() {
        super.onStart()

        binding.btnDelete.setOnClickListener {
            val networkService=(applicationContext as MyApplication).networkService

            val dno =intent.getIntExtra("dno",0)

            val requestCall: Call<Unit> = networkService.delete(dno)
            requestCall.enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    Toast.makeText(this@TripReviewActivity,"success", Toast.LENGTH_SHORT).show()
                    val intent= Intent(this@TripReviewActivity, DiaryActivity::class.java)
                    startActivity(intent)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(this@TripReviewActivity,"fail", Toast.LENGTH_SHORT).show()
                }

            })


        }
    }
}
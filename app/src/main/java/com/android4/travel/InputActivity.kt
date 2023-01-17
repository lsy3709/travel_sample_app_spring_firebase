package com.android4.travel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android4.travel.databinding.ActivityInputBinding

class InputActivity : AppCompatActivity() {
    lateinit var binding: ActivityInputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tripPersonalBtn.setOnClickListener {
            val intent = Intent(this, InputTripActivity::class.java)
            intent.putExtra("check",0)
            startActivity(intent)
        }
        binding.tripTeamBtn.setOnClickListener {
            val intent = Intent(this, InputTripActivity::class.java)
            intent.putExtra("check",1)
            startActivity(intent)
        }


    }
}
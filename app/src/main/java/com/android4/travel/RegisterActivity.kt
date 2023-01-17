package com.android4.travel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android4.travel.databinding.ActivityRegisterBinding
import com.android4.travel.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    lateinit var binding : ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//view binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerBtn.setOnClickListener {

            var user = User(binding.editUsername.text.toString(), binding.editPassword.text.toString(), binding.editNickname.text.toString())
            Log.d("test1", "===================================$user")
            val networkService = (applicationContext as MyApplication).networkService
            var userInsertCall = networkService.doInsertUser(user)
            userInsertCall.enqueue(object: Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if(response.isSuccessful) {
                        var user = response.body()

                        Log.d("test1", "===================================$user")

                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)

                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    call.cancel()
                }

            })
        }
    }
}
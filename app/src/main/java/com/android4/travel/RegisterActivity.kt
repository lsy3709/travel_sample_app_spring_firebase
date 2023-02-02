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
    //회원 가입시 백 서버에 디비에 저장

    lateinit var binding : ActivityRegisterBinding
    private  var TAG : String = "RegisterActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//view binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerBtn.setOnClickListener {

            var user = User(binding.editUsername.text.toString(), binding.editPassword.text.toString(), binding.editNickname.text.toString())
            Log.d(TAG, "1=========================registerBtn==========$user")
            val networkService = (applicationContext as MyApplication).networkService
            //스프링에 회원 가입 전달, mysql 저장 후 리턴.
            var userInsertCall = networkService.doInsertUser(user)
            userInsertCall.enqueue(object: Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if(response.isSuccessful) {
                        var user = response.body()

                        Log.d(TAG, "2===response.isSuccessful=====response.body()===========================$user")

                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)

                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    call.cancel()
                }

            })
        }
        // 앱 시작시 회원가입 창에서 back to Login  버튼 클릭시 해당 로그인 뷰로 이동
        binding.tvBackLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)

            startActivity(intent)
        }

    }
}
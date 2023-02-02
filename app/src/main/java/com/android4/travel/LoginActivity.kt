package com.android4.travel

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android4.travel.databinding.ActivityLoginBinding
import com.android4.travel.model.LoginDto
import com.android4.travel.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.HashMap

class LoginActivity : AppCompatActivity() {
    lateinit var binding : ActivityLoginBinding
    private val fireDatabase = FirebaseDatabase.getInstance().reference
    private  var TAG : String = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//view binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            var loginDto = LoginDto(
                username = binding.loginIdEt.text.toString(),
                password = binding.loginPassEt.text.toString()
                )

            //retrofit2 통신 스프링에 전달 부분
            val networkService = (applicationContext as MyApplication).networkService
            // 스프링에서 받아 온 유저
            var userInsertCall = networkService.login(loginDto)
            Log.d(TAG, "1===========loginDto.toString()의 값 : $loginDto")
            userInsertCall.enqueue(object: Callback<LoginDto> {
                override fun onResponse(call: Call<LoginDto>, response: Response<LoginDto>) {

                    if(response.isSuccessful) {
                        Log.d(TAG, "2===========response.toString()의 값 : $response")
                        val header = response.headers()
                        val auth = header.get("Authorization")
                        Log.d(TAG,"3===========auth.toString()의 값 "+auth.toString())

                        val username = response.body()?.username.toString()
                        val password = response.body()?.password.toString()

                        Log.d(TAG,"4============login=username======$username")

                        val loginSharedPref = getSharedPreferences("login_prof", Context.MODE_PRIVATE)
                        loginSharedPref.edit().run {
                            putString("Authorization", auth)
                            putString("username", username)
                            putString("password", password)
                            commit()
                        }

                        val database = Firebase.database
                        val myRef = database.getReference("username")

                        myRef.setValue(username)

                        var oneUserCall = networkService.doGetOneUser(username)
                        oneUserCall.enqueue(object: Callback<User> {
                            override fun onResponse(call: Call<User>, response: Response<User>) {
                                val user = response.body()

                                Log.d(TAG, "5=============loginUser======================$user")

                                fireDatabase.child("users").child(username.toString()).addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onCancelled(error: DatabaseError) {
                                    }
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        var check=0
                                        var inUsername:String
                                        for(data in snapshot.children){
                                            Log.d(TAG, "6============data.....$data")
                                            Log.d(TAG, "7===============key...........${data.children}")

                                            var item = data.getValue() as HashMap<String, Any?>
                                            inUsername = item.get("username").toString()

                                            if(inUsername == username) {
                                                check=1
                                            }
//
//                                            var arr = arrayListOf<String>()
//                                            data.children.forEach { user ->
//                                                var filed = user.value.toString()
//                                                Log.d("test", "filed==========$filed")
//                                                for(a in filed.trim('{','}').split(", ")) {
//                                                    Log.d("test", "a==========$a")
//                                                    arr.add(a)
//                                                }
//
//                                                inUsername = (arr[2].substring(arr[2].indexOf('=')+1))
//                                                Log.d("test", "inUsername==========$inUsername")
//                                                if(inUsername==username) {
//                                                    check=1
//                                                }
//                                            }

//                        fireDatabase.child("users").child(username.toString()).push().setValue(user)
                                        }
                                        if(check==1) {
                                            //username이 이미 저장되어 있음
                                        } else {
                                            fireDatabase.child("users").child(username.toString()).push().setValue(user)
                                        }

                                        //동일값 계속 생성
//                    if(fireDatabase.child("users").child(username.toString()) == username){
//                        return
//                    }
//                    fireDatabase.child("users").child(username.toString()).push().setValue(user)
//
                                    }
                                })
                            }

                            override fun onFailure(call: Call<User>, t: Throwable) {
                                TODO("Not yet implemented")
                            }
                        })

                        //로그인 후 메인 화면으로
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)

                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<LoginDto>, t: Throwable) {
                    call.cancel()
                }

            })
        }

        //회원가입 창에서 클릭시 로그인창으로 이동
        binding.loginSignUpBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)

            startActivity(intent)
        }
    }
}
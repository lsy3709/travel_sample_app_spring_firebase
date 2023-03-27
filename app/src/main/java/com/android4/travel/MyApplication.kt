package com.android4.travel

import android.app.Application
import androidx.multidex.MultiDexApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication: Application() {

    companion object {
        lateinit var auth: FirebaseAuth
        var email: String? = null
        lateinit var db: FirebaseFirestore
        lateinit var storage: FirebaseStorage
        fun checkAuth(): Boolean {
            var currentUser = auth.currentUser
            return currentUser?.let {
                email = currentUser.email
                currentUser.isEmailVerified
            } ?: let {
                false
            }
        }

    }

    override fun onCreate() {
        super.onCreate()
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()
        storage = Firebase.storage
    }

    var networkService: INetworkService
    val retrofit: Retrofit
    get() = Retrofit.Builder()
       // .baseUrl("http://192.168.0.21:8083/")
        .baseUrl("http://10.100.104.97:8083/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    init {
        networkService = retrofit.create(INetworkService::class.java)
    }
}
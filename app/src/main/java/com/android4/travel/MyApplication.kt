package com.android4.travel

import android.app.Application
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication: Application() {

    var networkService: INetworkService
    val retrofit: Retrofit
    get() = Retrofit.Builder()
        .baseUrl("http://10.100.103.13:8083/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    init {
        networkService = retrofit.create(INetworkService::class.java)
    }
}
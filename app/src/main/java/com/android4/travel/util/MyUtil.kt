package com.android4.travel.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.android4.travel.MainActivity
import com.android4.travel.MyApplication
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun myCheckPermission(activity: AppCompatActivity) {

    val requestPermissionLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(activity, "권한 승인", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "권한 거부", Toast.LENGTH_SHORT).show()
        }
    }

    if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) !== PackageManager.PERMISSION_GRANTED
    ) {
        requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}

fun dateToString(date: Date): String {
    val format = SimpleDateFormat("yyyy-MM-dd")
    return format.format(date)
}

//파이어 스토어 수정, 삭제 등
fun deleteStore(docId: String){
    //delete............................
    MyApplication.db.collection("news")
        .document(docId)
        .delete()

}

fun deleteImage(docId: String) {
    //add............................
    val storage = MyApplication.storage
    val storageRef = storage.reference
    val imgRef = storageRef.child("images/${docId}.jpg")
    imgRef.delete()
}

fun deleteVideo(docId: String) {
    //add............................
    val storage = MyApplication.storage
    val storageRef = storage.reference
    val videoRef = storageRef.child("images/${docId}.mp4")
    videoRef.delete()
}

 fun uploadImage(activity: AppCompatActivity,docId: String,filePath:String){
    //add............................
    val storage = MyApplication.storage
    val storageRef = storage.reference
    val imgRef = storageRef.child("images/${docId}.jpg")

    val file = Uri.fromFile(File(filePath))
    imgRef.putFile(file)
        .addOnSuccessListener {
            Toast.makeText(activity, "save ok..", Toast.LENGTH_SHORT).show()
            activity.finish()
        }
        .addOnFailureListener{
            Log.d("lsy", "file save error", it)
        }

}

 fun uploadVideo(activity: AppCompatActivity,docId: String,filePathVideo:String){
    //add............................
    val storage = MyApplication.storage
    val storageRef = storage.reference
    val imgRef = storageRef.child("images/${docId}.mp4")

    val file = Uri.fromFile(File(filePathVideo))
    imgRef.putFile(file)
        .addOnSuccessListener {
            Toast.makeText(activity, "save ok..", Toast.LENGTH_SHORT).show()
            activity.finish()
        }
        .addOnFailureListener{
            Log.d("lsy", "file save error", it)
        }

}

fun updateStore(docId: String){
    //delete............................
    MyApplication.db.collection("news")
        .document(docId)
        .delete()

}

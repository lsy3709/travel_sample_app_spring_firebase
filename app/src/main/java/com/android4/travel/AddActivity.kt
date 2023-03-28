package com.android4.travel

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat


import com.android4.travel.databinding.ActivityAddBinding
import com.android4.travel.util.dateToString
import com.android4.travel.util.uploadImage
import com.android4.travel.util.uploadVideo

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.util.*

class AddActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddBinding
    lateinit var filePath: String
    lateinit var filePathVideo: String
    var imgStatus = 0
    var videoStatus = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("PLAN TALK")

    }

    //사진
    val requestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
        if(it.resultCode === android.app.Activity.RESULT_OK){
            imgStatus = 1
            binding.addImageView.visibility = View.VISIBLE
            Glide
                .with(getApplicationContext())
                .load(it.data?.data)
                .apply(RequestOptions().override(250, 200))
                .centerCrop()
                .into(binding.addImageView)


            val cursor = contentResolver.query(it.data?.data as Uri,
                arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null);
            cursor?.moveToFirst().let {
                filePath=cursor?.getString(0) as String
            }
        }
    }

    //비디오
    val requestVideoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
        if(it.resultCode === android.app.Activity.RESULT_OK){
            videoStatus = 1
            binding.addVideoView.visibility = View.VISIBLE
            val mc = MediaController(this) // 비디오 컨트롤 가능하게(일시정지, 재시작 등)

            binding.addVideoView.setMediaController(mc)

            val fileUri: Uri = it.data!!.data!!
            val file : String = fileUri.toString()

            binding.addVideoView.setVideoPath(file) // 선택한 비디오 경로 비디오뷰에 셋
            binding.addVideoView.start() // 비디오뷰 시작


            val cursor = contentResolver.query(it.data?.data as Uri,
                arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null);
            cursor?.moveToFirst().let {
                filePathVideo=cursor?.getString(0) as String
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId === R.id.menu_add_gallery){
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            requestLauncher.launch(intent)
        }else if(item.itemId === R.id.menu_add_save){
            if(imgStatus === 1 && videoStatus === 1 && binding.addEditView.text.isNotEmpty()){
                //store 에 먼저 데이터를 저장후 document id 값으로 업로드 파일 이름 지정
                saveStore()
            } else if (imgStatus === 1 && videoStatus === 0 && binding.addEditView.text.isNotEmpty()){
                saveStoreNoVideo()
            }
            else if (imgStatus === 0 && videoStatus === 1 && binding.addEditView.text.isNotEmpty()){
                saveStoreNoImg()
            }
            else if (imgStatus === 0 && videoStatus === 0 &&binding.addEditView.text.isNotEmpty()){
                saveStoreNoImgNoVideo()
            }
            else {
                Toast.makeText(this, "데이터가 모두 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }

        }
        else if(item.itemId === R.id.menu_main_auth){
            val intent = Intent(this@AddActivity,AuthActivity::class.java)
            startActivity(intent)
        } else if(item.itemId === R.id.menu_logout){

                //로그아웃...........
                MyApplication.auth.signOut()
                MyApplication.email = null
            val intent = Intent(this@AddActivity,AuthActivity::class.java)
            startActivity(intent)

        } else if(item.itemId === R.id.menu_add_video){
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "video/*"
            requestVideoLauncher.launch(intent)
        }
        return super.onOptionsItemSelected(item)
    }
    //....................

    private fun saveStoreNoImgNoVideo(){
        //add............................
        val data = mapOf(
            "email" to MyApplication.email,
            "content" to binding.addEditView.text.toString(),
            "date" to dateToString(Date())
        )

        MyApplication.db.collection("news")
            .add(data)
       goToMain()

    }

    private fun saveStore(){
        //add............................
        val data = mapOf(
            "email" to MyApplication.email,
            "content" to binding.addEditView.text.toString(),
            "date" to dateToString(Date())
        )

        MyApplication.db.collection("news")
            .add(data)
            .addOnSuccessListener {

                uploadImage(this@AddActivity,it.id,filePath)
                uploadVideo(this@AddActivity,it.id,filePathVideo)
            }
            .addOnFailureListener{
                Log.d("kkang", "data save error", it)
            }
        goToMain()
    }

    private fun saveStoreNoVideo(){
        //add............................
        val data = mapOf(
            "email" to MyApplication.email,
            "content" to binding.addEditView.text.toString(),
            "date" to dateToString(Date())
        )

        MyApplication.db.collection("news")
            .add(data)
            .addOnSuccessListener {
                uploadImage(this@AddActivity,it.id,filePath)
            }
            .addOnFailureListener{
                Log.d("kkang", "data save error", it)
            }
        goToMain()

    }

    private fun saveStoreNoImg(){
        //add............................
        val data = mapOf(
            "email" to MyApplication.email,
            "content" to binding.addEditView.text.toString(),
            "date" to dateToString(Date())
        )

        MyApplication.db.collection("news")
            .add(data)
            .addOnSuccessListener {
                uploadVideo(this@AddActivity,it.id,filePathVideo)
            }
            .addOnFailureListener{
                Log.d("kkang", "data save error", it)
            }
        goToMain()
    }


    private fun goToMain() {
        val intent = Intent(this@AddActivity, MainActivity::class.java)
        startActivity(intent)
    }
}
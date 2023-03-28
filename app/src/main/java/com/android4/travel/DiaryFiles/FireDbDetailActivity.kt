package com.android4.travel.DiaryFiles

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.android4.travel.MainActivity
import com.android4.travel.MyApplication
import com.android4.travel.R
import com.android4.travel.databinding.ActivityFireDbDetailBinding
import com.android4.travel.databinding.ActivityLoginBinding
import com.android4.travel.util.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.io.File
import java.util.*

class FireDbDetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityFireDbDetailBinding
    lateinit var email : String
    lateinit var date : String
    lateinit var content : String
    lateinit var docId : String
    lateinit var filePath: String
    lateinit var filePathVideo: String
    var videoStatus = 0
    var imgStatus = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFireDbDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = intent.getStringExtra("email").toString()
        date = intent.getStringExtra("date").toString()
        content = intent.getStringExtra("content").toString()
        docId = intent.getStringExtra("docId").toString()

        binding.itemEmailView.text = email
        binding.itemDateView.text = date
        binding.itemContentView.hint = content

        //스토리지 이미지 다운로드........................
        val imgRef = MyApplication.storage.reference.child("images/${docId}.jpg")
        imgRef.downloadUrl.addOnCompleteListener{ task ->
            if(task.isSuccessful){
                binding.itemImageView.visibility = View.VISIBLE
                Glide.with(this)
                    .load(task.result)
                    .into(binding.itemImageView)
            }
        }

        //스토리지 비디오 다운로드........................
        val videoViewRef = MyApplication.storage.reference.child("images/${docId}.mp4")
        videoViewRef.downloadUrl.addOnCompleteListener{ task ->
            if(task.isSuccessful){
                binding.itemVideoView.visibility = View.VISIBLE
                Log.d("lsy","비디오 경로1 task.result: ${task.result}")
                Log.d("lsy","비디오 경로2 task.result.toString: ${task.result.toString()}")
                val mc = MediaController(this) // 비디오 컨트롤 가능하게(일시정지, 재시작 등)
                binding.itemVideoView.setMediaController(mc)
                binding.itemVideoView.setVideoURI(task.result)

            }
        }

        // 글만
        fun updateStoreNoImgNoVideo(){
            MyApplication.db.collection("news")
                .document(docId)
                .update(mapOf("content" to binding.itemContentView.text.toString()))

            goToMain()
        }

        // 이미지 만
        fun updateStoreWithImg(){
            MyApplication.db.collection("news")
                .document(docId)
                .update(mapOf("content" to binding.itemContentView.text.toString()))
                .addOnSuccessListener {
                    deleteImage(docId)
                    uploadImage(this,docId,filePath)
                    goToMain()
                }
                .addOnFailureListener{
                    Log.d("lsy", "data save error", it)
                }

        }

        // 비디오, 사진 둘다
        fun updateStoreWithImgVideo(){
            MyApplication.db.collection("news")
                .document(docId)
                .update(mapOf("content" to binding.itemContentView.text.toString()))
                .addOnSuccessListener {
                    deleteImage(docId)
                    uploadImage(this,docId,filePath)
                    deleteVideo(docId)
                    uploadVideo(this,docId,filePathVideo)
                    goToMain()
                }
                .addOnFailureListener{
                    Log.d("lsy", "data save error", it)
                }

        }
        // 비디오 만
        fun updateStoreWithVideo(){
            MyApplication.db.collection("news")
                .document(docId)
                .update(mapOf("content" to binding.itemContentView.text.toString()))
                .addOnSuccessListener {
                    deleteVideo(docId)
                    uploadVideo(this,docId,filePathVideo)
                    goToMain()
                }
                .addOnFailureListener{
                    Log.d("lsy", "data save error", it)
                }

        }
        //게시글 수정 적용

        binding.updateFBtn.setOnClickListener {
            if(imgStatus === 1 && videoStatus === 1 && binding.itemContentView.text.isNotEmpty()){
                //store 에 먼저 데이터를 저장후 document id 값으로 업로드 파일 이름 지정
                AlertDialog.Builder(this)
                    .setTitle("게시글 진짜 수정")
                    .setMessage("진짜 수정 하시겠습니까?")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                        // "예"를 선택했을 때의 Action
                        updateStoreWithImgVideo()

                    })
                    .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                        // "아니오"를 선택했을 때의 Action
                    })
                    .show()

            } else if(imgStatus === 1 && videoStatus === 0 && binding.itemContentView.text.isNotEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("게시글 진짜 수정")
                    .setMessage("진짜 수정 하시겠습니까?")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                        // "예"를 선택했을 때의 Action
                        updateStoreWithImg()

                    })
                    .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                        // "아니오"를 선택했을 때의 Action
                    })
                    .show()
            }
            else if(imgStatus === 0 && videoStatus === 1 && binding.itemContentView.text.isNotEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("게시글 진짜 수정")
                    .setMessage("진짜 수정 하시겠습니까?")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                        // "예"를 선택했을 때의 Action
                        updateStoreWithVideo()

                    })
                    .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                        // "아니오"를 선택했을 때의 Action
                    })
                    .show()
            }
            else if(imgStatus === 0 && videoStatus === 0 && binding.itemContentView.text.isNotEmpty()) {
                AlertDialog.Builder(this)
                    .setTitle("게시글 진짜 수정")
                    .setMessage("진짜 수정 하시겠습니까?")
                    .setIcon(android.R.drawable.ic_delete)
                    .setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                        // "예"를 선택했을 때의 Action
                        updateStoreNoImgNoVideo()

                    })
                    .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                        // "아니오"를 선택했을 때의 Action
                    })
                    .show()
            }
            else {
                Toast.makeText(this, "데이터가 모두 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }



        // 사진 선택 후 처리
        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        {
            if(it.resultCode === android.app.Activity.RESULT_OK){
                imgStatus = 1
                binding.itemImageView.visibility = View.VISIBLE
                Glide
                    .with(getApplicationContext())
                    .load(it.data?.data)
                    .apply(RequestOptions().override(250, 200))
                    .centerCrop()
                    .into(binding.itemImageView)


                val cursor = contentResolver.query(it.data?.data as Uri,
                    arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null);
                cursor?.moveToFirst().let {
                    filePath=cursor?.getString(0) as String
                }
            }
        }

        //사진첩 선택.
        binding.imageUpBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            requestLauncher.launch(intent)
        }

        //비디오
        val requestVideoLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        {
            if(it.resultCode === android.app.Activity.RESULT_OK){
                videoStatus = 1
                binding.itemVideoView.visibility = View.VISIBLE
                val mc = MediaController(this) // 비디오 컨트롤 가능하게(일시정지, 재시작 등)

                binding.itemVideoView.setMediaController(mc)

                val fileUri: Uri = it.data!!.data!!
                val file : String = fileUri.toString()

                binding.itemVideoView.setVideoPath(file) // 선택한 비디오 경로 비디오뷰에 셋
                binding.itemVideoView.start() // 비디오뷰 시작


                val cursor = contentResolver.query(it.data?.data as Uri,
                    arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null);
                cursor?.moveToFirst().let {
                    filePathVideo=cursor?.getString(0) as String
                }
            }
        }

        binding.videoUpBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "video/*"
            requestVideoLauncher.launch(intent)
        }


        //삭제 확인 완료
        binding.deleteFBtn.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("게시글 삭제")
                .setMessage("삭제하시겠습니까?")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                    // "예"를 선택했을 때의 Action
                    //스토어 삭제
                 deleteStore(docId)

                    //스토리지 삭제
                deleteImage(docId)

                    goToMain()

                })
                .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                    // "아니오"를 선택했을 때의 Action
                })
                .show()
        }


        //




    }

    private fun saveStoreNoImgNoVideo(){
        //add............................
        val data = mapOf(
            "email" to MyApplication.email,
            "content" to binding.itemContentView.text.toString(),
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
            "content" to binding.itemContentView.text.toString(),
            "date" to dateToString(Date())
        )

        MyApplication.db.collection("news")
            .add(data)
            .addOnSuccessListener {

                uploadImage(this@FireDbDetailActivity,it.id,filePath)
                uploadVideo(this@FireDbDetailActivity,it.id,filePathVideo)
            }
            .addOnFailureListener{
                Log.d("lsy", "data save error", it)
            }
        goToMain()
    }

    private fun saveStoreNoVideo(){
        //add............................
        val data = mapOf(
            "email" to MyApplication.email,
            "content" to binding.itemContentView.text.toString(),
            "date" to dateToString(Date())
        )

        MyApplication.db.collection("news")
            .add(data)
            .addOnSuccessListener {
                uploadImage(this@FireDbDetailActivity,it.id,filePath)
            }
            .addOnFailureListener{
                Log.d("lsy", "data save error", it)
            }
        goToMain()

    }

    private fun saveStoreNoImg(){
        //add............................
        val data = mapOf(
            "email" to MyApplication.email,
            "content" to binding.itemContentView.text.toString(),
            "date" to dateToString(Date())
        )

        MyApplication.db.collection("news")
            .add(data)
            .addOnSuccessListener {
                uploadVideo(this@FireDbDetailActivity,it.id,filePathVideo)
            }
            .addOnFailureListener{
                Log.d("lsy", "data save error", it)
            }
        goToMain()
    }

    private fun goToMain() {
        val intent = Intent(this@FireDbDetailActivity, MainActivity::class.java)
        startActivity(intent)
    }
}
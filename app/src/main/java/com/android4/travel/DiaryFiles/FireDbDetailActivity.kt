package com.android4.travel.DiaryFiles

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.android4.travel.MainActivity
import com.android4.travel.MyApplication
import com.android4.travel.R
import com.android4.travel.databinding.ActivityFireDbDetailBinding
import com.android4.travel.databinding.ActivityLoginBinding
import com.android4.travel.util.deleteImage
import com.android4.travel.util.deleteStore
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class FireDbDetailActivity : AppCompatActivity() {
    lateinit var binding : ActivityFireDbDetailBinding
    lateinit var email : String
    lateinit var date : String
    lateinit var content : String
    lateinit var docId : String
    lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFireDbDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = intent.getStringExtra("email").toString()
        date = intent.getStringExtra("date").toString()
        content = intent.getStringExtra("content").toString()
        docId = intent.getStringExtra("docId").toString()

        Log.d("lsy","값 확인: email : $email")
        Log.d("lsy","값 확인: email : $date")
        Log.d("lsy","값 확인: email : $content")
        Log.d("lsy","값 확인: email : $docId")

        binding.itemEmailView.text = email
        binding.itemDateView.text = date
        binding.itemContentView.hint = content

        //스토리지 이미지 다운로드........................
        val imgRef = MyApplication.storage.reference.child("images/${docId}.jpg")
        imgRef.downloadUrl.addOnCompleteListener{ task ->
            if(task.isSuccessful){
                Glide.with(this)
                    .load(task.result)
                    .into(binding.itemImageView)
            }
        }

        val requestLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult())
        {
            if(it.resultCode === android.app.Activity.RESULT_OK){
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


        // 메인으로
        binding.mainBtn.setOnClickListener {
            val intent = Intent(this@FireDbDetailActivity,MainActivity::class.java)
            startActivity(intent)
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

                    val intent = Intent(this@FireDbDetailActivity, MainActivity::class.java)
                    startActivity(intent)

                })
                .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                    // "아니오"를 선택했을 때의 Action
                })
                .show()
        }


        //

    }
}
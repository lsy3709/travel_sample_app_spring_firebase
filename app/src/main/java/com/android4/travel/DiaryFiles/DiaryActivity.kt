package com.android4.travel.DiaryFiles

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android4.travel.MyApplication
import com.android4.travel.adapter.DiaryAdapter

import com.android4.travel.databinding.ActivityDiaryBinding
import com.android4.travel.model.Diary
import com.android4.travel.model.DiaryListModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiaryActivity : AppCompatActivity(){
    lateinit var binding: ActivityDiaryBinding
    //lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val calval = intent.getStringExtra("year")
        binding.calenderView.setText(calval)

        binding.rg1.setOnCheckedChangeListener { radioGroup, i ->
            var rb = findViewById<RadioButton>(i)
            if(rb!=null)
                binding.rsCheck.setText(rb.text.toString())

        }







        binding.btnReview.setOnClickListener {
            binding.DiaryListOn.visibility = View.GONE
            Toast.makeText(this,"제목 클릭  자세히 보기",Toast.LENGTH_SHORT).show()

            val networkService = (applicationContext as MyApplication).networkService
            val diaryListCall = networkService.doGetTripDiaryList()

            diaryListCall.enqueue(object: Callback<DiaryListModel>{
                override fun onResponse(call: Call<DiaryListModel>, response: Response<DiaryListModel>
                ) {
                    if(response.isSuccessful){
//                        var DiaryAdapter = DiaryAdapter()
                        binding.recyclerDiaryView.layoutManager = LinearLayoutManager(this@DiaryActivity)
                        binding.recyclerDiaryView.adapter = DiaryAdapter(this@DiaryActivity,response.body()?.diarys)
                        binding.recyclerDiaryView.addItemDecoration(DividerItemDecoration(this@DiaryActivity,LinearLayoutManager.VERTICAL))

                    }

                }

                override fun onFailure(call: Call<DiaryListModel>, t: Throwable) {
                    call.cancel()
                }

            })
        }

        binding.btnGallery.setOnClickListener {
            openGallery()
        }


    }

    //동기화
    override fun onStart() {
        super.onStart()
        binding.btnWrite.setOnClickListener {
            //일기 쓰기
            val loginId=intent.getStringExtra("LoginId")
                binding.LoginId.setText(loginId)

            var diary= Diary(
                dno =0,
                title =binding.titleId.text.toString(),
                content =binding.contentId.text.toString(),
                date =binding.calenderView.text.toString(),
                on_off =binding.rsCheck.text.toString(),
                hitcount = 0,
                good = 0,
                trip_id =binding.LoginId.text.toString(),
                image_url = "NULL"
                //binding.GalleryImage.setImageURI(data?.data).toString()
            )

            val networkService=(applicationContext as MyApplication).networkService
            val diaryInsertCall = networkService.insert(diary)
            diaryInsertCall.enqueue(object: Callback<String>{
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    println(response.body().toString())
                    Toast.makeText(applicationContext,"내일기에 저장 성공했습니다!",Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<String>, t: Throwable) {
                    call.cancel()
                }

            })
            finish()

        }


    }

    override fun onResume() {
        super.onResume()
        binding.btnMyDiary.setOnClickListener {
            binding.DiaryListOn.visibility = View.GONE
            Toast.makeText(this,"제목 클릭 자세히 보기",Toast.LENGTH_SHORT).show()

            val networkService = (applicationContext as MyApplication).networkService
            val diaryListCall = networkService.doGetDiaryList()

            diaryListCall.enqueue(object: Callback<DiaryListModel>{
                override fun onResponse(call: Call<DiaryListModel>, response: Response<DiaryListModel>
                ) {
                    if(response.isSuccessful){
                        Log.d("test","======================================testtest")
//                        var DiaryAdapter = DiaryAdapter()
                        binding.recyclerDiaryView.layoutManager = LinearLayoutManager(this@DiaryActivity)
                        binding.recyclerDiaryView.adapter = DiaryAdapter(this@DiaryActivity,response.body()?.diarys)
                        binding.recyclerDiaryView.addItemDecoration(DividerItemDecoration(this@DiaryActivity,LinearLayoutManager.VERTICAL))

                    }

                }

                override fun onFailure(call: Call<DiaryListModel>, t: Throwable) {
                    call.cancel()
                }

            })
        }
    }



    private val OPEN_GALLERY = 2

    private fun openGallery() {
        val intent :Intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        startActivityForResult(intent,OPEN_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode !=Activity.RESULT_OK){
            return
        }

        when(requestCode){
            OPEN_GALLERY -> {

                if(resultCode == Activity.RESULT_OK && requestCode == OPEN_GALLERY){
                    binding.GalleryImage.setImageURI(data?.data)

                //Toast.makeText(this, binding.GalleryImage.setImageURI(data?.data).toString(),Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this,"이미지를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

//    private fun openGallery(){
//        val intent = Intent(Intent.ACTION_PICK)
//
//        intent.type = MediaStore.Images.Media.CONTENT_TYPE
//        intent.type = "image/*"
//        startActivityForResult(intent, 102)
//    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
//        super.onActivityResult(requestCode, resultCode, intent)
//
//        if (requestCode == 102 && resultCode == Activity.RESULT_OK){
//            currentImageURL = intent?.data
//            // Base64 인코딩부분
//            val ins: InputStream? = currentImageURL?.let {
//                applicationContext.contentResolver.openInputStream(
//                    it
//                )
//            }
//            val img: Bitmap = BitmapFactory.decodeStream(ins)
//            ins?.close()
//            val resized = Bitmap.createScaledBitmap(img, 256, 256, true)
//            val byteArrayOutputStream = ByteArrayOutputStream()
//            resized.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream)
//            val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
//            val outStream = ByteArrayOutputStream()
//            val res: Resources = resources
//            profileImageBase64 = Base64.encodeToString(byteArray, NO_WRAP)
//            // 여기까지 인코딩 끝
//
//            // 이미지 뷰에 선택한 이미지 출력
//            val imageview: ImageView = findViewById(id.pet_image)
//            imageview.setImageURI(currentImageURL)
//            try {
//                //이미지 선택 후 처리
//            }catch (e: Exception){
//                e.printStackTrace()
//            }
//        } else{
//            Log.d("ActivityResult", "something wrong")
//        }
//    }

}





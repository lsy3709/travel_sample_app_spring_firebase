package com.android4.travel.DiaryFiles

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android4.travel.MyApplication
import com.android4.travel.R
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
    val TAG : String = "DiaryActivity"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //DiaryCalFragment에서 intent.putExtra("year",dateString)
        val calval = intent.getStringExtra("year")
        binding.calenderView.setText(calval)


        // 전체 공유 프리퍼런스에 값을 가져와서 확인중.
        val loginSharedPref = getSharedPreferences("login_prof", Context.MODE_PRIVATE)
        val loginId = loginSharedPref.getString("username","default")
        binding.LoginId.setText(loginId)

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
//            val loginSharedPref = getSharedPreferences("login_prof", Context.MODE_PRIVATE)
//            val loginId = loginSharedPref.getString("username","default")
//            binding.LoginId.setText(loginId)
//            Log.d(TAG,"1===============loginId : $loginId")

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
//                binding.GalleryImage.setImageURI(data?.data).toString()
            )
            Log.d(TAG,"3===============loginId : $binding.LoginId.text.toString()")
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


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        requestGalleryLauncher.launch(intent)
    }

    //gallery request launcher..................
    val requestGalleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
        try {
            val calRatio = calculateInSampleSize(
                it.data!!.data!!,
                resources.getDimensionPixelSize(R.dimen.imgSize),
                resources.getDimensionPixelSize(R.dimen.imgSize)
            )

            val option = BitmapFactory.Options()
            option.inSampleSize = calRatio

            var inputStream = contentResolver.openInputStream(it.data!!.data!!)
            val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
            inputStream!!.close()
            inputStream = null

            bitmap?.let {
                binding.GalleryImage.setImageBitmap(bitmap)
            } ?: let{
                Log.d("test", "bitmap null")
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun calculateInSampleSize(fileUri: Uri, reqWidth: Int, reqHeight: Int): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        try {
            var inputStream = contentResolver.openInputStream(fileUri)

            //inJustDecodeBounds 값을 true 로 설정한 상태에서 decodeXXX() 를 호출.
            //로딩 하고자 하는 이미지의 각종 정보가 options 에 설정 된다.
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream!!.close()
            inputStream = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //비율 계산........................
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        //inSampleSize 비율 계산
        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }


//    private val OPEN_GALLERY = 2
//
//    private fun openGallery() {
//        val intent :Intent = Intent(Intent.ACTION_PICK)
//        intent.setType("image/*")
//        startActivityForResult(intent,OPEN_GALLERY)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(resultCode !=Activity.RESULT_OK){
//            return
//        }
//
//        when(requestCode){
//            OPEN_GALLERY -> {
//
//                if(resultCode == Activity.RESULT_OK && requestCode == OPEN_GALLERY){
//                    binding.GalleryImage.setImageURI(data?.data)
//
//                //Toast.makeText(this, binding.GalleryImage.setImageURI(data?.data).toString(),Toast.LENGTH_SHORT).show()
//                }else{
//                    Toast.makeText(this,"이미지를 선택하지 않았습니다.",Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }


    //==============================================================

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





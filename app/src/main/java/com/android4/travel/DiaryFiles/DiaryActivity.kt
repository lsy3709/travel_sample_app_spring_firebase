package com.android4.travel.DiaryFiles

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
    // 1 인텐트로 갤러리 앱의 사진 목록을 출력하는 코드
    // 예제
    // 인텐트의 액션 문자열 : Intent.ACTION_PICK
    // 데이터 : MediaStore.Images.Media.CONTENT_TYPE
    // 타입 : "image/*"
//    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//    intent.type = "image/*"
//    requestGalleyLancher.lanch(intent)

//    선택한 사진 액티비티 화면에 출력 OOM(out of memory) 발생 주의 위해서 사이즈 변경.
//     BitmapFactory.Option 객체의 inSampleSize 값 지정 또는
//     원본 데이터의 크기와 화면에 출력되는 이미지의 크기를 비교해서 적절한 비율로 만드는 함수
//     깡쌤 샘플 코드 활용
    //
//    private fun calculateInSampleSize(fileUri: Uri, reqWidth: Int, reqHeight: Int): Int {
//        val options = BitmapFactory.Options()
//        //옵션만 설정하기 위해서 설정.
//        options.inJustDecodeBounds = true
//        try {
//            var inputStream = contentResolver.openInputStream(fileUri)
//
//            //inJustDecodeBounds 값을 true 로 설정한 상태에서 decodeXXX() 를 호출.
//            // 실제로 Bitmap 객체가 만들어지지 않고 아래에
//            //로딩 하고자 하는 이미지의 각종 정보가 options 에 설정 된다.
//            BitmapFactory.decodeStream(inputStream, null, options)
//            inputStream!!.close()
//            inputStream = null
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        //비율 계산........................
//        val (height: Int, width: Int) = options.run { outHeight to outWidth }
//        var inSampleSize = 1
//        //inSampleSize 비율 계산
//        if (height > reqHeight || width > reqWidth) {
//
//            val halfHeight: Int = height / 2
//            val halfWidth: Int = width / 2
//
//            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
//                inSampleSize *= 2
//            }
//        }
//        return inSampleSize
//    }

    // 갤러리 앱의 목록에서 사용자가 사진을 하나 선택해서 되돌아왔을 때 위의
    // calculateInSampleSize() 함수를 이용해 이미지를 불러오는 코드.
    //
    //gallery request launcher..................
//    val requestGalleryLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult())
//    {
//        try {
//            //inSampleSize 비율 계산, 지정
//            val calRatio = calculateInSampleSize(
//                it.data!!.data!!,
//                //출력할 이미지의 화면 크기를 나타냄
//                // dimen 리소스값을 얻어 지정. 임의의 숫자를 지정해도 됨.
//                resources.getDimensionPixelSize(R.dimen.imgSize),
//                resources.getDimensionPixelSize(R.dimen.imgSize)
//            )
//            val option = BitmapFactory.Options()
//            option.inSampleSize = calRatio
//
//            //이미지 로딩
//            // 갤러리 앱의 콘텐츠 프로바이더가 제공하는 InputStream 객체를 가져옴.
//            // 이 객체에 사용자가 갤러리 앱에서 선택한 사진 데이터가 담겨 있음.
//            // contentResolver.openInputStream(it.data!!.data!!)
//            //
//            var inputStream = contentResolver.openInputStream(it.data!!.data!!)
//            val bitmap = BitmapFactory.decodeStream(inputStream, null, option)
//            inputStream!!.close()
//            inputStream = null
//
//            bitmap?.let {
//                binding.userImageView.setImageBitmap(bitmap)
//            } ?: let{
//                Log.d("test", "bitmap null")
//            }
//        }catch (e: Exception){
//            e.printStackTrace()
//        }
//    }
    //주의사항
    // 카메라로 찍은 사진 이미지는 갤러리 앱의 콘텐츠 프로바이더를 이용해 사진의 경로를 얻은 뒤
    // 그 경로의 이미지를 decodeFile() 함수로 가저오는 것은 API 레벨 29에서 deprecated 됨.
    // 물론 지금 사용 가능하지만, 비추.
    // 구글은 파일을 직접 읽는 방식이 아니라 갤러리 앱의 콘텐츠 프로바이더에서 제공하는
    // InputStream 사용 권고.

    //2 카메라 앱 연동하기
    //


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





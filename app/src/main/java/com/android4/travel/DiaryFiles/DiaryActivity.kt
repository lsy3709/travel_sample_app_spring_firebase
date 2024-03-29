package com.android4.travel.DiaryFiles

import android.R
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android4.travel.MainActivity
import com.android4.travel.MyApplication
import com.android4.travel.adapter.DiaryAdapter
import com.android4.travel.databinding.ActivityDiaryBinding
import com.android4.travel.model.Diary
import com.android4.travel.model.DiaryListModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class DiaryActivity : AppCompatActivity(){
    lateinit var binding: ActivityDiaryBinding
    //lateinit var filePath: String
    val TAG : String = "DiaryActivity"
    var inputStream : InputStream? = null
    var inputStream2 : InputStream? = null


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

        // sample.mp4 설정
        // 비디오 영상 샘플 플레이 해보기.
        val uri = Uri.parse("android.resource://$packageName/raw/sample")
        binding.VideoImage.setVideoURI(uri)
// 비디오 뷰 테스트
        binding.VideoImage.setOnPreparedListener {
                mp -> // 준비 완료되면 비디오 재생
            mp.start()
        }


        binding.btnMainView.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }

        binding.btnVideo.setOnClickListener{
            openVideo()
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

            if(inputStream != null) {
                try {
                    val timeStamp: String =
                        SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                    val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    val file2 = File.createTempFile(
                        "MP4_${timeStamp}_",
                        ".mp4",
                        storageDir
                    )
                    var filePath = file2.absolutePath
                    Log.d("file2 filePath.toString() :4=================== ", filePath.toString())

                    try {
                        val buff = ByteArray(1024 * 8)
                        val os: OutputStream = FileOutputStream(file2)
                        while (true) {
                            val readed: Int
                            readed = inputStream!!.read(buff);

                            if (readed == -1) {
                                break;
                            }
                            os.write(buff, 0, readed);
                            //write buff
//                    downloaded += readed;
                        }
                        os.flush();
                        os.close();

                    } catch (e: IOException) {
                        e.printStackTrace();
                    } finally {
                        inputStream?.close()
                    }


                    ////test 111 filePath.toString()  ㅎㅐ당 경로에 파일 쓰기. 잘됨.
//////////////////////////////////////////////////////////////////
                    //base64 인코딩 테스트 완료. 주석. 해당 내용 디비에 저장시 많이 느림.
                    // val base64EncodedFile = Base64Util.mp4ToBase64(filePath)
                    binding.videouri.setText(filePath)
                    val loginSharedPref = getSharedPreferences("video_data", Context.MODE_PRIVATE)
                    loginSharedPref.edit().run {
                        // putString("base64EncodedFile", base64EncodedFile)
                        putString("filePath2", filePath)
                        commit()
                    }


                } catch (e: IOException) {

                }
            }

            var diary= Diary(
                dno =0,
                title =binding.titleId.text.toString(),
                content =binding.contentId.text.toString(),
                date =binding.calenderView.text.toString(),
                on_off =binding.rsCheck.text.toString(),
                hitcount = 0,
                good = 0,
                trip_id =binding.LoginId.text.toString(),
                image_uri = binding.imageuri.text.toString(),
                video_uri = binding.videouri.text.toString(),
//                binding.GalleryImage.setImageURI(data?.data).toString()
            )

            val networkService=(applicationContext as MyApplication).networkService
            val diaryInsertCall = networkService.insert(diary)
            diaryInsertCall.enqueue(object: Callback<Unit>{
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
//                    println(response.body().toString())
//                    Log.d(TAG,"4===============response.body().toString() : $response.body().toString()")
                    Toast.makeText(applicationContext,"내일기에 저장 성공했습니다!",Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {
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
            val loginSharedPref = getSharedPreferences("login_prof", Context.MODE_PRIVATE)
            val LoginId = loginSharedPref.getString("username","default")
            val diaryListCall = networkService.doGetDiaryList(LoginId)

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

    //샘플 비디오 관련 메서드
    private fun openVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "video/*"
        requestVideoLauncherncher2.launch(intent)
    }

    val requestVideoLauncherncher2 = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
        val mc = MediaController(this) // 비디오 컨트롤 가능하게(일시정지, 재시작 등)

        binding.VideoImage2.setMediaController(mc)

        val fileUri: Uri = it.data!!.data!!
        Log.d("video","fileUri 1==============: "+fileUri.toString())
        val file : String = fileUri.toString()
        Log.d("video","file.length: 2==================="+file.length)
        Log.d("video","file. fileUri.toString(): 3==================="+file)
        binding.VideoImage2.setVideoPath(file) // 선택한 비디오 경로 비디오뷰에 셋
        binding.VideoImage2.start() // 비디오뷰 시작

        inputStream = contentResolver.openInputStream(fileUri)!!




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
                    resources.getDimensionPixelSize(com.android4.travel.R.dimen.imgSize),
                    resources.getDimensionPixelSize(com.android4.travel.R.dimen.imgSize)
                )

                val option = BitmapFactory.Options()
                option.inSampleSize = calRatio

                var inputStream = contentResolver.openInputStream(it.data!!.data!!)
                val bitmap = BitmapFactory.decodeStream(inputStream, null, option)

                val thumbnailSize = 100 // set the desired thumbnail size in pixels

//                val originalBitmap = BitmapFactory.decodeResource(
//                    resources,
//                    R.drawable.my_image
//                ) // load the original Bitmap

                val thumbnailBitmap = bitmap?.let { it1 ->
                    Bitmap.createScaledBitmap(
                        it1,
                        thumbnailSize,
                        thumbnailSize,
                        false
                    )
                } // create the thumbnail Bitmap


//                val outputStream = ByteArrayOutputStream()
//                if (bitmap != null) {
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, outputStream)
//                }
//                val bitmapData = outputStream.toByteArray()
//
//                inputStream2 = ByteArrayInputStream(bitmapData)
//                val bitmap2 = BitmapFactory.decodeStream(inputStream2, null, option)

                inputStream!!.close()
                inputStream = null

                bitmap?.let {
//                    Glide.with(this)
//                        .load(thumbnailBitmap)
//                        .into(binding.GalleryImage);
                    binding.GalleryImage.setImageBitmap(bitmap)
                    //이미지 비트맵 -> base64 인코딩 결과 문자열
                    // 프리퍼런스에 저장 테스트

                    var imgInfo :String = Base64Util.bitMapToBase64(thumbnailBitmap)
                    binding.imageuri.setText(imgInfo)

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

}






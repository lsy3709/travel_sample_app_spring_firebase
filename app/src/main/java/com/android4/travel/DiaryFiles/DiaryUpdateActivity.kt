package com.android4.travel.DiaryFiles

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity
import com.android4.travel.MyApplication
import com.android4.travel.R
import com.android4.travel.databinding.ActivityDiaryDetailBinding
import com.android4.travel.databinding.ActivityDiaryUpdateBinding
import com.android4.travel.model.Diary
import kotlinx.android.synthetic.main.activity_diary_update.*
import kotlinx.android.synthetic.main.fragment_diary_cal.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class DiaryUpdateActivity : AppCompatActivity() {
    lateinit var binding: ActivityDiaryUpdateBinding
    val TAG : String = "DiaryUpdateActivity"
    var dateString : String =""
    var inputStream : InputStream? = null
    var status_img: Int = 0
    var status_video: Int = 0
    var diary : Diary? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //프리퍼런스에 저장된 로그인 유저, 중간 저장소로
        // 프리퍼런스, sqlite, firebase 등.
        val loginSharedPref = getSharedPreferences("login_prof", Context.MODE_PRIVATE)
        val LoginId = loginSharedPref.getString("username","default")

        val dno = intent.getIntExtra("dno", 0)
        val title = intent.getStringExtra("listTitle")
        val date = intent.getStringExtra("listDate")
        val content = intent.getStringExtra("listContent")
        val listImage_url = intent.getStringExtra("listImage_url")
        val listVideo_url=intent.getStringExtra("listVideo_url")
//        Log.d(TAG,"1======시작시---dno 의 값 : $dno")
//        Log.d(TAG,"1======시작시---title 의 값 : $title")
//        Log.d(TAG,"1======시작시---date 의 값 : $date")
//        Log.d(TAG,"1======시작시---content 의 값 : $content")
//        Log.d(TAG,"1======시작시---listImage_url 의 값 : $listImage_url")


        if (listImage_url != null && !listImage_url.isBlank()) {
            val imageUri1 = Base64Util.stringToBitMap(listImage_url)
            binding.picture1.setImageBitmap(imageUri1)
            binding.picture1.visibility = View.VISIBLE
            binding.picturelabel1.visibility = View.VISIBLE
            var imgInfo :String = Base64Util.bitMapToBase64(imageUri1)
            binding.imageuri.setText(imgInfo)
            status_img = 1
        }

        if (listVideo_url != null && !listVideo_url.isBlank()) {
            val uri = Uri.parse(listVideo_url)
            val mc = MediaController(this) // 비디오 컨트롤 가능하게(일시정지, 재시작 등)

            binding.VideoImage2.setMediaController(mc)
            binding.VideoImage2.setVideoPath(uri.toString()) // 선택한 비디오 경로 비디오뷰에 셋
            binding.VideoImage2.start() // 비디오뷰 시작

            binding.videouri.setText(listVideo_url)

            status_video = 1
//            binding.VideoImage2.setVideoURI(uri)
//            binding.VideoImage2.setOnPreparedListener {
//                    mp -> // 준비 완료되면 비디오 재생
//                mp.start()
//            }
        }

        //뷰에 숫자 입력시 방법 아래 샘플.
        binding.dno.setText(""+dno)
        binding.LoginId.setText(LoginId)
        binding.listTitleId1.setText(title)
        binding.listDateId1.setText(date)
        binding.contentsTextView.setText(content)


    }

    override fun onStart() {
        super.onStart()

        binding.rg1.setOnCheckedChangeListener { radioGroup, i ->
            var rb = findViewById<RadioButton>(i)
            if(rb!=null)
                binding.rsCheck.setText(rb.text.toString())

        }

        binding.listDateId1.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth  ->
                dateString = "${year}년 ${month+1}월 ${dayOfMonth}일"
                binding.listDateId1.setText("날짜 :" +dateString)
            }

                DatePickerDialog(
                    this, dateSetListener, cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()


        }

        binding.btnPic.setOnClickListener {
            openGallery()
        }

        binding.btnVideo.setOnClickListener{
            openVideo()
        }


        binding.btnUpdate.setOnClickListener {

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
            val dno = intent.getIntExtra("dno", 0)
            AlertDialog.Builder(this)
                .setTitle("일기 수정")
                .setMessage("일기를 수정하시겠습니까?")
                .setIcon(android.R.drawable.btn_star_big_on)
                .setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                    // "예"를 선택했을 때의 Action
                    if(status_img == 1 && status_video == 1){
                        diary= Diary(
                            dno =dno,
                            title =binding.listTitleId1.text.toString(),
                            content =binding.contentsTextView.text.toString(),
                            date =binding.listDateId1.text.toString(),
                            on_off =binding.rsCheck.text.toString(),
                            hitcount = 0,
                            good = 0,
                            trip_id =binding.LoginId.text.toString(),
                            image_uri = binding.imageuri.text.toString(),
                            video_uri = binding.videouri.text.toString()
                        )
                    } else {
                        diary= Diary(
                            dno =dno,
                            title =binding.listTitleId1.text.toString(),
                            content =binding.contentsTextView.text.toString(),
                            date =binding.listDateId1.text.toString(),
                            on_off =binding.rsCheck.text.toString(),
                            hitcount = 0,
                            good = 0,
                            trip_id =binding.LoginId.text.toString(),
                            image_uri = binding.imageuri.text.toString(),
                            video_uri = binding.videouri.text.toString()
                        )
                    }

                    val networkService = (applicationContext as MyApplication).networkService

                    val dno = intent.getIntExtra("dno", 0)

                    //update(diary)
                    val requestCall: Call<Unit> = networkService.update(diary!!)
                    requestCall.enqueue(object : Callback<Unit> {
                        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                            Toast.makeText(this@DiaryUpdateActivity, "success", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this@DiaryUpdateActivity, DiaryActivity::class.java)
                            startActivity(intent)
                        }

                        override fun onFailure(call: Call<Unit>, t: Throwable) {
                            Toast.makeText(this@DiaryUpdateActivity, "fail", Toast.LENGTH_SHORT)
                                .show()
                        }

                    })
                })
                .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                    // "아니오"를 선택했을 때의 Action
                })
                .show()

        }

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("일기 삭제")
                .setMessage("일기를 삭제하시겠습니까?")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                    // "예"를 선택했을 때의 Action
                    val networkService = (applicationContext as MyApplication).networkService

                    val dno = intent.getIntExtra("dno",0)
                    Log.d(TAG,"3=======삭제 버튼 클릭시 dno 값 : $dno")

                    val requestCall: Call<Unit> = networkService.delete(dno)
                    requestCall.enqueue(object : Callback<Unit> {
                        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                            Toast.makeText(this@DiaryUpdateActivity, "success", Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this@DiaryUpdateActivity, DiaryActivity::class.java)
                            startActivity(intent)
                        }

                        override fun onFailure(call: Call<Unit>, t: Throwable) {
                            Toast.makeText(this@DiaryUpdateActivity, "fail", Toast.LENGTH_SHORT)
                                .show()
                        }

                    })
                })
                .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                    // "아니오"를 선택했을 때의 Action
                })
                .show()


        }
    }

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
        Log.d("video","fileUri: "+fileUri.toString())
        val file : String = fileUri.toString()
        Log.d("video","file.length: "+file.length)
        binding.VideoImage2.setVideoPath(fileUri.toString()) // 선택한 비디오 경로 비디오뷰에 셋
        binding.VideoImage2.start() // 비디오뷰 시작

        inputStream = contentResolver.openInputStream(fileUri)!!
        //var inputStream = contentResolver.openInputStream(it.data!!.data!!)
        // val bitmap = BitmapFactory.decodeStream(inputStream, null, option)


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

            val thumbnailSize = 100 // set the desired thumbnail size in pixels

            val thumbnailBitmap = bitmap?.let { it1 ->
                Bitmap.createScaledBitmap(
                    it1,
                    thumbnailSize,
                    thumbnailSize,
                    false
                )
            } // create the thumbnail Bitmap

            inputStream!!.close()
            inputStream = null

            bitmap?.let {
                binding.picture1.setImageBitmap(thumbnailBitmap)
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
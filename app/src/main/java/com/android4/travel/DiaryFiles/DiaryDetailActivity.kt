package com.android4.travel.DiaryFiles

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaParser
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.android4.travel.MainActivity
import com.android4.travel.MyApplication
import com.android4.travel.databinding.ActivityDiaryDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class DiaryDetailActivity : AppCompatActivity() {
    lateinit var binding: ActivityDiaryDetailBinding

    private var videoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiaryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 앱 내부 프리퍼런스에 임시로 저장해서 불러오기 테스트 완료
      //  val loginSharedPref = getSharedPreferences("video_data", Context.MODE_PRIVATE)
      //  val listVideo_url = loginSharedPref.getString("base64EncodedFile", "default")
        //임시로 앱 내부에 파일 쓰기한 위치의 Uri 위치. 테스트 완료
       // val filePath2 = loginSharedPref.getString("filePath2", "default")



        val dno = intent.getIntExtra("dno", 0)
        val title = intent.getStringExtra("listTitle")
        val date = intent.getStringExtra("listDate")
        val content = intent.getStringExtra("listContent")
        val listImage_url = intent.getStringExtra("listImage_url")
        val listVideo_url=intent.getStringExtra("listVideo_url")


        if (listImage_url != null && !listImage_url.isBlank()) {
            val imageUri1 = Base64Util.stringToBitMap(listImage_url)

            binding.picture1.setImageBitmap(imageUri1)
            binding.picture1.visibility = View.VISIBLE
            binding.picturelabel1.visibility = View.VISIBLE
        }
        if (listVideo_url != null && !listVideo_url.isBlank()) {
//            val timeStamp: String =
//                SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
//            val file3 = File.createTempFile(
//                "MP4_${timeStamp}_",
//                ".mp4",
//                storageDir
//            )
//            var filePath3 = file3.absolutePath
//
//                Base64Util.Base64Tomp4(listVideo_url,filePath3)
//
//            Log.d("filePath===========",filePath3.toString())
            val uri = Uri.parse(listVideo_url)
            if (listVideo_url != null) {
                Log.d("filePath55===========",listVideo_url)
            }
            binding.VideoImage2.setVideoURI(uri)
// 비디오 뷰 테스트
            binding.VideoImage2.setOnPreparedListener {
                    mp -> // 준비 완료되면 비디오 재생
                mp.start()
            }
        }

            binding.listTitleId1.setText(title)
            binding.listDateId1.setText(date)
            binding.contentsTextView.setText(content)




    }

    override fun onStart() {
        super.onStart()
        binding.btnMainView.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)
        }

        binding.btnUpdate.setOnClickListener {
            val dno = intent.getIntExtra("dno",0)
            val title = intent.getStringExtra("listTitle")
            val date=intent.getStringExtra("listDate")
            val content=intent.getStringExtra("listContent")
            val listImage_url=intent.getStringExtra("listImage_url")

            val intent = Intent(this, DiaryUpdateActivity::class.java)
            intent.putExtra("dno",dno)
            intent.putExtra("listTitle", title)
            intent.putExtra("listDate",date)
            intent.putExtra("listContent",content)
            intent.putExtra("listImage_url", listImage_url )
            startActivity(intent)

        }

        binding.btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("일기 삭제")
                .setMessage("일기를 삭제하시겠습니까?")
                .setIcon(R.drawable.ic_delete)
                .setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                    // "예"를 선택했을 때의 Action
                    val networkService=(applicationContext as MyApplication).networkService

                    val dno =intent.getIntExtra("dno",0)

                    val requestCall:Call<Unit> = networkService.delete(dno)
                    requestCall.enqueue(object :Callback<Unit>{
                        override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                            Toast.makeText(this@DiaryDetailActivity,"success",Toast.LENGTH_SHORT).show()
                            val intent= Intent(this@DiaryDetailActivity, DiaryActivity::class.java)
                            startActivity(intent)
                        }

                        override fun onFailure(call: Call<Unit>, t: Throwable) {
                            Toast.makeText(this@DiaryDetailActivity,"fail",Toast.LENGTH_SHORT).show()
                        }

                    })
                })
                .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->
                    // "아니오"를 선택했을 때의 Action
                })
                .show()


        }
    }
}







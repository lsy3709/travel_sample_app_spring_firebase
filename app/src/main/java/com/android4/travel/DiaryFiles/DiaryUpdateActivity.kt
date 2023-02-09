package com.android4.travel.DiaryFiles

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity
import com.android4.travel.MyApplication
import com.android4.travel.R
import com.android4.travel.databinding.ActivityDiaryDetailBinding
import com.android4.travel.databinding.ActivityDiaryUpdateBinding
import com.android4.travel.model.Diary
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class DiaryUpdateActivity : AppCompatActivity() {
    lateinit var binding: ActivityDiaryUpdateBinding
    val TAG : String = "DiaryUpdateActivity"
    var dateString : String =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDiaryUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dno = intent.getIntExtra("dno", 0)
        val title = intent.getStringExtra("listTitle")
        val date = intent.getStringExtra("listDate")
        val content = intent.getStringExtra("listContent")
        val listImage_url = intent.getStringExtra("listImage_url")
        Log.d(TAG,"1======시작시---dno 의 값 : $dno")
        Log.d(TAG,"1======시작시---title 의 값 : $title")
        Log.d(TAG,"1======시작시---date 의 값 : $date")
        Log.d(TAG,"1======시작시---content 의 값 : $content")
        Log.d(TAG,"1======시작시---listImage_url 의 값 : $listImage_url")


        if (listImage_url != null && !listImage_url.isBlank()) {
            val imageUri1 = Base64Util.stringToBitMap(listImage_url)
            binding.picture1.setImageBitmap(imageUri1)
            binding.picture1.visibility = View.VISIBLE
            binding.picturelabel1.visibility = View.VISIBLE
        }



        binding.listTitleId1.setText(title)
        binding.listDateId1.setText(date)
        binding.contentsTextView.setText(content)


    }

    override fun onStart() {
        super.onStart()

        binding.listDateId1.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth  ->
                dateString = "${year}년 ${month+1}월 ${dayOfMonth}일"
                binding.listDateId1.setText("날짜 :" +dateString)
            }
        }

        binding.btnPic.setOnClickListener {
            openGallery()
        }

        binding.btnUpdate.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("일기 수정")
                .setMessage("일기를 수정하시겠습니까?")
                .setIcon(android.R.drawable.btn_star_big_on)
                .setPositiveButton("예", DialogInterface.OnClickListener { dialog, which ->
                    // "예"를 선택했을 때의 Action
                    var diary= Diary(
                        dno =0,
                        title =binding.listTitleId1.text.toString(),
                        content =binding.contentsTextView.text.toString(),
                        date =binding.listDateId1.text.toString(),
                        on_off =binding.rsCheck.text.toString(),
                        hitcount = 0,
                        good = 0,
                        trip_id =binding.LoginId.text.toString(),
                        image_uri = binding.imageuri.text.toString()
//                binding.GalleryImage.setImageURI(data?.data).toString()
                    )
                    val networkService = (applicationContext as MyApplication).networkService

                    val dno = intent.getIntExtra("dno", 0)

                    //update(dno)
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
                binding.picture1.setImageBitmap(bitmap)
                //이미지 비트맵 -> base64 인코딩 결과 문자열
                // 프리퍼런스에 저장 테스트

                var imgInfo :String = Base64Util.bitMapToBase64(bitmap)
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
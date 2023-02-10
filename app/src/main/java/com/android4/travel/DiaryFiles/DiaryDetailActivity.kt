package com.android4.travel.DiaryFiles

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.android4.travel.MainActivity
import com.android4.travel.R.drawable
import com.android4.travel.MyApplication
import com.android4.travel.RegisterActivity
import com.android4.travel.databinding.ActivityDiaryDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DiaryDetailActivity : AppCompatActivity() {
    lateinit var binding:ActivityDiaryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDiaryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dno = intent.getIntExtra("dno",0)
        val title = intent.getStringExtra("listTitle")
        val date=intent.getStringExtra("listDate")
        val content=intent.getStringExtra("listContent")
        val listImage_url=intent.getStringExtra("listImage_url")

      if(listImage_url != null && !listImage_url.isBlank()) {
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







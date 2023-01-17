package com.android4.travel

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.android4.travel.adapter.InputAdapter
import com.android4.travel.databinding.ActivityInputTripBinding
import com.android4.travel.model.Trip
import com.android4.travel.model.User
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class InputTripActivity : AppCompatActivity() {
    lateinit var binding: ActivityInputTripBinding
    lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    lateinit var intentLauncher: ActivityResultLauncher<Intent>
    lateinit var adapter: InputAdapter
    lateinit var username: String
    lateinit var nickname: String

    var memberName = arrayListOf<String>("나")
    var memberPhone = arrayListOf<Int>(11)
    var date:Long=0L
    var date2:Long=0L
    var strDate:String =""
    var strDate2:String =""
    var test:String = ""
    var place:String =""
    var title:String = ""
    var on_off=0
    var phone=0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInputTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = Firebase.database
        val myRef = database.getReference("username")
        nickname = ""
        myRef.get().addOnCompleteListener {
            username = it.result.value.toString()

            Log.d("pjh","main==================================$$username")

            val networkService = (applicationContext as MyApplication).networkService
            var oneUserCall = networkService.doGetOneUser(username)
            oneUserCall.enqueue(object: Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    nickname = response.body()?.nickname.toString()
                    Log.d("pjh", "===================nickname: $nickname")
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    call.cancel()
                }

            })
        }
        Log.d("pjh","input=======================$nickname")


        Log.d("pjh","input=======================$memberName")

        val intent = intent

//        val pref = getSharedPreferences("inputPref", Context.MODE_PRIVATE)
//        var userName = pref.getString("userName","aaa")
//        if (userName != null) {
//            memberName.add(userName)
//        }

        var checkNum = intent.getIntExtra("check", 0)

        if(checkNum==0) {
            binding.switch1.isVisible=false
            binding.textView6.isVisible=false
            binding.button6.isVisible=false
        } else {
            binding.switch1.setOnCheckedChangeListener { compoundButton, b ->
                when(b) {
                    true -> on_off = 1
                }
            }
        }

        binding.button.setOnClickListener {
            val datePickerDialog = DatePickerDialog.OnDateSetListener{
                    view, year, month, dayOfMonth ->
                var month = month+1
                strDate = makeDateString(dayOfMonth, month, year).toString()

                binding.button.text = strDate
            }

            var str:List<String> = convertLongToDate(date).split(".")

            var year = str[0].toInt()
            var month = str[1].toInt()-1
            var day = str[2].toInt()

            val style = AlertDialog.THEME_HOLO_LIGHT

            DatePickerDialog(this, style, datePickerDialog,year, month, day).show()
        }

        binding.button2.setOnClickListener {
            val datePickerDialog = DatePickerDialog.OnDateSetListener{
                    view, year, month, dayOfMonth ->
                var month = month+1
                strDate2 = makeDateString(dayOfMonth, month, year).toString()

                binding.button2.text = strDate2
            }

            var str:List<String> = convertLongToDate(date2).split(".")

            var year = str[0].toInt()
            var month = str[1].toInt()-1
            var day = str[2].toInt()

            val style = AlertDialog.THEME_HOLO_LIGHT

            DatePickerDialog(this, style, datePickerDialog,year, month, day).show()
        }

        binding.button5.setOnClickListener {

            showDateRangePicker()

            binding.button.text
            binding.button2.text
        }

        binding.button3.setOnClickListener {
            memberName.set(0,nickname)
//            val intent = Intent(this, Test11Activity::class.java)
            intent.putExtra("startDate",date)
            intent.putExtra("endDate",date2)
            title = binding.editTitle.text.toString()
            intent.putExtra("title",title)
            intent.putExtra("place",place)

            intent.putExtra("memberName",memberName)
            intent.putExtra("memberPhone",memberPhone)

            var str =""
            var str2 =""

            for(i in memberName)
                str+=i+","
            for(i in memberPhone)
                str2+=i.toString()+","

            var trip = Trip(0, title, date, date2, place, str, str2, on_off)

            val networkService = (applicationContext as MyApplication).networkService
            val tripInsertCall = networkService.doInsertTrip(trip)
            Log.d("pjh","================================${trip}")
            tripInsertCall.enqueue(object: Callback<Trip> {
                override fun onResponse(call: Call<Trip>, response: Response<Trip>) {
                    if(response.isSuccessful) {
                        Log.d("pjh","================================${response}")
                        val intent = Intent(this@InputTripActivity, MainActivity::class.java)
                        val sharePref = getSharedPreferences("inputPref", Context.MODE_PRIVATE)

                        sharePref.edit().run {
                            putInt("input", 1)
                            commit()
                        }

                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<Trip>, t: Throwable) {
                    call.cancel()
                }
            })
        }

        binding.button4.setOnClickListener {
            finish()
        }

        binding.button6.setOnClickListener {
            if(ContextCompat.checkSelfPermission(this, "android.permission.READ_CONTACTS")!= PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(arrayOf("android.permission.READ_CONTACTS"))
            } else {
                val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                intentLauncher.launch(intent)
            }
        }

        binding.button7.setOnClickListener {
            place = binding.editPlace.text.toString()
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.co.kr/maps/search/$place"))

            startActivity(intent)

//            binding.editPlace.isVisible = false

            binding.editTitle.setText(place)
        }


        var layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL

        adapter = InputAdapter(memberName, nickname)

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) {

            for(entry in it.entries) {
                if(entry.key == "android.permisson.READ_CONTACTS" && entry.value) {
                    val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                    intentLauncher.launch(intent)
                } else {
                    Toast.makeText(this, "required permission..", Toast.LENGTH_SHORT).show()
                }
            }
        }

        intentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode== RESULT_OK) {
                val cursor = contentResolver.query(
                    it!!.data!!.data!!,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER
                    ), null, null, null)
                if(cursor!!.moveToNext()) {
                    var name = cursor.getString(0)
                    Log.d("pjh", "==============$name")
                    memberName.add(name).let {
                        adapter.notifyDataSetChanged()
                        for(i in 0 until memberName.size) {
                            Log.d("pjh", "========${memberName[i]}")
                        }
                    }
                    var phone = cursor.getString(1).toInt()
                    memberPhone.add(phone).let {
                        for(i in 0 until memberName.size) {
                            Log.d("pjh", "========${memberPhone[i]}")
                        }
                    }
                }
            }
        }

    }

    private fun makeDateString(dayOfMonth: Int, month: Int, year: Int): Any {

        return year.toString()+"."+month.toString()+"."+dayOfMonth.toString()
    }

    private fun showDateRangePicker() {
        val dateRangePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTitleText("Select Date")
            .build()

        dateRangePicker.show(
            supportFragmentManager,
            "date_range_picker"
        )

        dateRangePicker.addOnPositiveButtonClickListener { datePicked->

            val startDate = datePicked.first
            val endDate = datePicked.second

//            Toast.makeText(this,"$startDate $endDate", Toast.LENGTH_SHORT).show()

            date = startDate
            date2 = endDate

            binding.button.text = convertLongToDate(startDate)
            binding.button2.text = convertLongToDate(endDate)

        }
    }

    private fun convertLongToDate(time: Long):String {

        val date = Date(time)
        val format = SimpleDateFormat(
            "yyyy.MM.dd",
            Locale.getDefault()
        )

        return format.format(date)
    }
}
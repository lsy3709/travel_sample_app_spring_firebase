package com.android4.travel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android4.travel.databinding.ActivityMainBinding
import com.android4.travel.fragment.DiaryCalFragment
import com.android4.travel.fragment.ListFragment
import com.android4.travel.fragment.TripFragment
import com.android4.travel.model.TripListModel
import com.android4.travel.model.User
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var username: String
    lateinit var nickname: String
    private  var TAG : String = "MainActivity"

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//view binding
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //SharedPreference를 이용하여 간단한 데이터들을 저장하고 불러올 수 있다.
        val pref = getSharedPreferences("inputPref", Context.MODE_PRIVATE)
        var check = pref.getInt("input", 0)

        if(check==1) {
            pref.edit().run {
                putInt("input", 0)
                commit()
            }
        }

//        pref.edit().run {
//            putString("userName","유저명")
//            commit()
//        }

        val database = Firebase.database
        val myRef = database.getReference("username")

        myRef.get().addOnCompleteListener {
            username = it.result.value.toString()
            Log.d(TAG,"1=====main==================================$$username")

            //레트로핏 통신 객체
            val networkService = (applicationContext as MyApplication).networkService
            //
            var oneUserCall = networkService.doGetOneUser(username)
            oneUserCall.enqueue(object: Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    nickname = response.body().toString()

                    Log.d("test", "===================nickname: $nickname")
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    call.cancel()
                }

            })
        }

        binding.fab.setOnClickListener {
            val intent = Intent(this, InputActivity::class.java)

            startActivity(intent)
        }

//tabLayout binding
//        binding.tabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                val transaction = supportFragmentManager.beginTransaction()
//                when(tab?.text){
//                    "여행" -> {
//                        transaction.replace(R.id.linearTest, TripFragment())
//                    }
//                    "일정" -> {
//                        transaction.replace(R.id.linearTest, ListFragment())
//                    }
//                    "일기" -> {
//                        transaction.replace(R.id.linearTest, DiaryFragment())
//                    }
//                }
//                transaction.addToBackStack(null)
//                transaction.commit()
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//
//            }
//        })
//tabLayoutManager
//        mViewPager = findViewById(R.id.viewPager)
//        mViewPager!!.adapter = MainViewPagerAdapter(this)
        binding.tabs.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                //탭 선택되었을 때,
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //탭이 선택되지 않은 상태로 변경되었을 때,
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //이미 선택된 탭이 다시 선택되었을 때,
            }
        })
//viewPager - adapter 연결
        binding.viewPager.adapter = MainViewPagerAdapter(this)
        TabLayoutMediator(binding.tabs, binding.viewPager){tab, position ->
            when(position){
                0 -> tab.text = "여행"
                1 -> tab.text = "일정"
                2 -> tab.text = "일기"
            }
        }.attach()

//toolbar binding
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle("PLAN TALK")

        val networkService = (applicationContext as MyApplication).networkService
        val tripListCall = networkService.doGetTripList()

        tripListCall.enqueue(object: Callback<TripListModel> {
            override fun onResponse(call: Call<TripListModel>, response: Response<TripListModel>) {
                if(response.isSuccessful) {
//                    binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
//                    binding.recyclerView.adapter = MyAdapter(this@MainActivity, response.body()?.trips)
//                    binding.recyclerView.addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
                }
            }
            override fun onFailure(call: Call<TripListModel>, t: Throwable) {
                call.cancel()
            }
        })
    }

    override fun onStart() {
        super.onStart()

        val pref = getSharedPreferences("inputPref", Context.MODE_PRIVATE)
        var check = pref.getInt("input", 0)

        Log.d("pjh","$check==============")

        if(check==1){
            pref.edit().run {
                putInt("input", 0)
                commit()
            }

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.linearTest, ListFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout ->{
                Toast.makeText(this@MainActivity, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                Intent(this@MainActivity, LoginActivity::class.java).run {
                    startActivity(this)}

            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
//viewpager adapter
class MainViewPagerAdapter(fragment: FragmentActivity): FragmentStateAdapter(fragment){
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> TripFragment()
            1 -> ListFragment()
            else -> DiaryCalFragment()
        }
    }
}
//class MainViewPagerAdapter(val trip: TripFragment, val list: ListFragment, val diary: DiaryFragment): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
//            RecyclerView.ViewHolder = MainViewPagerHolder(ItemViewPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val binding = (holder as MainViewPagerHolder).binding
//        binding
//    }
//
//    override fun getItemCount(): Int {
//        return datas.size
//    }
//}


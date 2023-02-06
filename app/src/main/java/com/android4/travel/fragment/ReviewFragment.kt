package com.android4.travel.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.android4.travel.MyApplication
import com.android4.travel.R
import com.android4.travel.adapter.MyReviewListAdapter
import com.android4.travel.adapter.MyTripListAdapter
import com.android4.travel.databinding.FragmentListBinding
import com.android4.travel.databinding.FragmentReviewBinding
import com.android4.travel.model.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ReviewFragment : Fragment() {
    // review -> diary  trip 리스트 조회, 일기 조회로 테스트
    lateinit var binding: FragmentReviewBinding
    lateinit var adapter: MyReviewListAdapter
    val TAG : String = "ReviewFragment";
    var username=""
    var nickname=""

    companion object { //Java Static 유사. 멤버 변수나 함수를 클래스 이름으로 접근
        fun newInstance(): ReviewFragment {
            return ReviewFragment()
        }
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReviewBinding.inflate(inflater, container, false)

        //networkService로 데이터 가져오기 //activity는 그냥 써도 되는데 fragment는 액티비티에 붙이는 거라서 부모까지 호출해줘야 함.
        val database = Firebase.database
        val myRef = database.getReference("username")

        myRef.get().addOnSuccessListener {
            username=it.value.toString()
            Log.d(TAG,"firebase===========================${it.value.toString()}")
            val networkService = (context?.applicationContext as MyApplication).networkService

            var oneUserCall = networkService.doGetOneUser(username)
            oneUserCall.enqueue(object: Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    nickname = response.body()?.nickname.toString()
                    Log.d(TAG, "===================nickname: $nickname")

                   // val tripListCall = networkService.doGetTripList()
                    val diaryListCall = networkService.doGetDiaryList()
                    diaryListCall.enqueue(object: Callback<DiaryListModel> {
                        override fun onResponse(call: Call<DiaryListModel>, response: Response<DiaryListModel>) {

                            val diaryList = response.body() //responsebody에 있는 값을 가져옴
                            Log.d(TAG, "$diaryList")

//                binding.tripListRecyclerView.adapter = MyTripListAdapter(this@ListFragment, tripList?.trips)
//                binding.tripListRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

//Diary List Spinner
                            // strings.xml 에 추가 하기  diaryListOnOffItem
                            var check = 0
                            val spinner = binding.reviewListSpinner
                            spinner.adapter =
                                context?.let { ArrayAdapter.createFromResource(it, R.array.diaryListOnOffItem, android.R.layout.simple_spinner_dropdown_item) }
                            spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                                override fun onNothingSelected(p0: AdapterView<*>?) {
                                    //아무 것도 선택 안 했을 때,
                                }

                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    var memberCheck: List<String>?
                                    when(position){
                                        0 -> { //all

                                            adapter = MyReviewListAdapter(this@ReviewFragment, diaryList?.diarys, nickname)
                                            adapter.notifyDataSetChanged()

                                        }
                                        1 -> { //personal
                                            var tripListModel = arrayListOf<Diary>()
//                                            for(i in 0 until (diaryList?.diarys?.size ?:0)){
//                                                memberCheck = diaryList?.diarys?.get(i). ?: null
//                                                Log.d(TAG,"$i========================${memberCheck?.size}")
//                                                if(memberCheck?.size?.minus(1) == 1){
//                                                    tripList?.trips?.get(i)?.let { tripListModel?.add(it) }
//                                                    Log.d(TAG,"$i================================")
//                                                    Log.d(TAG,"========================${tripList?.trips?.get(i)}")
//                                                }
//                                                adapter = MyTripListAdapter(this@ListFragment, tripListModel, nickname)
//                                            }
                                        }
                                        2 -> { //people
//                                            var tripListModel = arrayListOf<Trip>()
//                                            for(i in 0 until (tripList?.trips?.size ?:0)){
//                                                memberCheck = tripList?.trips?.get(i)?.member?.split(",") ?: null
//                                                if(memberCheck?.size?.minus(1)!! > 1){
//                                                    Log.d(TAG,"$i================================")
//                                                    tripList?.trips?.get(i)?.let { tripListModel?.add(it) }
//                                                }
//                                                adapter = MyTripListAdapter(this@ListFragment, tripListModel, nickname)
//                                            }
                                        }
                                    }
                                    binding.reviewListRecyclerView.adapter = adapter
                                    binding.reviewListRecyclerView.addItemDecoration(
                                        DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
                                    )
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                        override fun onFailure(call: Call<DiaryListModel>, t: Throwable) {
                        }
                    })

                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    call.cancel()
                }

            })


        }

        return binding.root
    }


}
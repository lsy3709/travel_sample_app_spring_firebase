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
import com.android4.travel.adapter.MyTripListAdapter
import com.android4.travel.databinding.FragmentListBinding
import com.android4.travel.model.Trip
import com.android4.travel.model.TripListModel
import com.android4.travel.model.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListFragment : Fragment() {
    lateinit var binding: FragmentListBinding
    lateinit var adapter: MyTripListAdapter
    var username=""
    var nickname=""

    companion object { //Java Static 유사. 멤버 변수나 함수를 클래스 이름으로 접근
        fun newInstance(): ListFragment {
            return ListFragment()
        }
    }
//    //메모리 올라갔을 때,
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
//
//    //프래그먼트를 포함하고 있는 액티비티에 붙었을 때,
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//    }

    //뷰 생성됐을 때, 프래그먼트와 레이아웃 연결
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//viewBinding
        binding = FragmentListBinding.inflate(inflater, container, false)
//spinner : 여행 리스트 조건 조회 / values triplistonoffitem, fragment_list
//        val items:Array<String> = resources.getStringArray(R.array.tripListOnOffItem)
//        val tripListSelectAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
//        spinner.adapter = tripListSelectAdapter
//        val tripListSelectAdapter = ArrayAdapter(this, R.layout.)
//        val tripListSpinner = binding.tripListSpinner
//        tripListSpinner.adapter = ArrayAdapter.createFromResource(this@ListFragment, R.array.tripListOnOffItem, R.layout.fragment_list)
//spinner
//        fun tripListSpinnerSetUp(){
//            val onOff = resources.getStringArray(R.array.tripListOnOffItem)
//            val adapter = ArrayAdapter(this, R.array.tripListOnOffItem, android.R.layout.simple_spinner_dropdown_item, onOff)
//            binding.tripListSpinner.adapter = adapter
//        }
//networkService로 데이터 가져오기 //activity는 그냥 써도 되는데 fragment는 액티비티에 붙이는 거라서 부모까지 호출해줘야 함.
        val database = Firebase.database
        val myRef = database.getReference("username")

        myRef.get().addOnSuccessListener {
            username=it.value.toString()
            Log.d("test","firebase===========================${it.value.toString()}")
            val networkService = (context?.applicationContext as MyApplication).networkService

            var oneUserCall = networkService.doGetOneUser(username)
            oneUserCall.enqueue(object: Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    nickname = response.body()?.nickname.toString()
                    Log.d("pjh", "===================nickname: $nickname")

                    val tripListCall = networkService.doGetTripList()
                    tripListCall.enqueue(object: Callback<TripListModel>{
                        override fun onResponse(call: Call<TripListModel>, response: Response<TripListModel>) {

                            val tripList = response.body() //responsebody에 있는 값을 가져옴
                            Log.d("wsy", "$tripList")

//                binding.tripListRecyclerView.adapter = MyTripListAdapter(this@ListFragment, tripList?.trips)
//                binding.tripListRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

//Trip List Spinner
                            var check = 0
                            val spinner = binding.tripListSpinner
                            spinner.adapter =
                                context?.let { ArrayAdapter.createFromResource(it, R.array.tripListOnOffItem, android.R.layout.simple_spinner_dropdown_item) }
                            spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                                override fun onNothingSelected(p0: AdapterView<*>?) {
                                    //아무 것도 선택 안 했을 때,
                                }

                                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                    var memberCheck: List<String>?
                                    when(position){
                                        0 -> { //all

                                            adapter = MyTripListAdapter(this@ListFragment, tripList?.trips, nickname)
                                            adapter.notifyDataSetChanged()

                                        }
                                        1 -> { //personal
                                            var tripListModel = arrayListOf<Trip>()
                                            for(i in 0 until (tripList?.trips?.size ?:0)){
                                                memberCheck = tripList?.trips?.get(i)?.member?.split(",") ?: null
                                                Log.d("test","$i========================${memberCheck?.size}")
                                                if(memberCheck?.size?.minus(1) == 1){
                                                    tripList?.trips?.get(i)?.let { tripListModel?.add(it) }
                                                    Log.d("test","$i================================")
                                                    Log.d("test","========================${tripList?.trips?.get(i)}")
                                                }
                                                adapter = MyTripListAdapter(this@ListFragment, tripListModel, nickname)
                                            }
                                        }
                                        2 -> { //people
                                            var tripListModel = arrayListOf<Trip>()
                                            for(i in 0 until (tripList?.trips?.size ?:0)){
                                                memberCheck = tripList?.trips?.get(i)?.member?.split(",") ?: null
                                                if(memberCheck?.size?.minus(1)!! > 1){
                                                    Log.d("test","$i================================")
                                                    tripList?.trips?.get(i)?.let { tripListModel?.add(it) }
                                                }
                                                adapter = MyTripListAdapter(this@ListFragment, tripListModel, nickname)
                                            }
                                        }
                                    }
                                    binding.tripListRecyclerView.adapter = adapter
                                    binding.tripListRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                                    adapter.notifyDataSetChanged()
                                }
                            }
                        }
                        override fun onFailure(call: Call<TripListModel>, t: Throwable) {
                        }
                    })

                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    call.cancel()
                }

            })


        }
        Log.d("test", "2222222222=========================$username")

        val networkService = (context?.applicationContext as MyApplication).networkService
        val tripListCall = networkService.doGetMyTripList(username)
        tripListCall.enqueue(object: Callback<TripListModel>{
            override fun onResponse(call: Call<TripListModel>, response: Response<TripListModel>) {

                val tripList = response.body() //responsebody에 있는 값을 가져옴
                Log.d("wsy", "$tripList")

//                binding.tripListRecyclerView.adapter = MyTripListAdapter(this@ListFragment, tripList?.trips)
//                binding.tripListRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

//Trip List Spinner
                var check = 0
                val spinner = binding.tripListSpinner
                spinner.adapter =
                    context?.let { ArrayAdapter.createFromResource(it, R.array.tripListOnOffItem, android.R.layout.simple_spinner_dropdown_item) }
                spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        //아무 것도 선택 안 했을 때,
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        var memberCheck: List<String>?
                        when(position){
                            0 -> { //all

                                adapter = MyTripListAdapter(this@ListFragment, tripList?.trips, nickname)
                                adapter.notifyDataSetChanged()

                            }
                            1 -> { //personal
                                var tripListModel = arrayListOf<Trip>()
                                for(i in 0 until (tripList?.trips?.size ?:0)){
                                    memberCheck = tripList?.trips?.get(i)?.member?.split(",") ?: null
                                    Log.d("test","$i========================${memberCheck?.size}")
                                    if(memberCheck?.size?.minus(1) == 1){
                                        tripList?.trips?.get(i)?.let { tripListModel?.add(it) }
                                        Log.d("test","$i================================")
                                        Log.d("test","========================${tripList?.trips?.get(i)}")
                                    }
                                    adapter = MyTripListAdapter(this@ListFragment, tripListModel, nickname)
                                }
                            }
                            2 -> { //people
                                var tripListModel = arrayListOf<Trip>()
                                for(i in 0 until (tripList?.trips?.size ?:0)){
                                    memberCheck = tripList?.trips?.get(i)?.member?.split(",") ?: null
                                    if(memberCheck?.size?.minus(1)!! > 1){
                                        Log.d("test","$i================================")
                                        tripList?.trips?.get(i)?.let { tripListModel?.add(it) }
                                    }
                                    adapter = MyTripListAdapter(this@ListFragment, tripListModel, nickname)
                                }
                            }
                        }
                        binding.tripListRecyclerView.adapter = adapter
                        binding.tripListRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                        adapter.notifyDataSetChanged()
                    }
                }
            }
            override fun onFailure(call: Call<TripListModel>, t: Throwable) {
            }
        })
        return binding.root
    }

}
package com.android4.travel.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android4.travel.ChatActivity
import com.android4.travel.InputTripActivity
import com.android4.travel.MyApplication
import com.android4.travel.R
import com.android4.travel.databinding.FragmentTripBinding
import com.android4.travel.databinding.ItemTripIconBinding
import com.android4.travel.model.Trip
import com.android4.travel.model.TripListModel
import com.android4.travel.model.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TripFragment : Fragment() {
    val TAG : String = "TripFragment";
    lateinit var binding : FragmentTripBinding
    lateinit var adapter: MyTripAdapter
    var username=""
    var nickname=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTripBinding.inflate(inflater, container, false)

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


                    val networkService = (context?.applicationContext as MyApplication).networkService
                    val tripListCall = networkService.doGetTripList()
                    tripListCall.enqueue(object: Callback<TripListModel> {
                        override fun onResponse(call: Call<TripListModel>, response: Response<TripListModel>) {
                            var trip = response.body()?.trips
                            val tripImg = arrayOf(R.drawable.trip1, R.drawable.trip2, R.drawable.trip3)

                            val layoutManager = LinearLayoutManager(context)
                            layoutManager.orientation = LinearLayoutManager.HORIZONTAL
                            binding.tripRecyclerView.layoutManager = layoutManager

                            //binding
//        binding.tripRecyclerView.layoutManager = LinearLayoutManager(context)
                            adapter = MyTripAdapter(this@TripFragment, tripImg, trip, nickname)
                            binding.tripRecyclerView.adapter = adapter
                            binding.tripRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
                            adapter.notifyDataSetChanged()
                        }

                        override fun onFailure(call: Call<TripListModel>, t: Throwable) {
                            call.cancel()
                        }

                    })
//
//        val tripImg = arrayOf(R.drawable.pic1, R.drawable.pic2, R.drawable.pic3)
//        val tripTitle = arrayOf("벚꽃 축제 갈 사람들 모여", "바다보러 갈 사람 모여", "같이 놀러갈 사람?!")
//        val tripNowMem = arrayOf(4, 6, 1)
//        val tripTotalMem = arrayOf(10, 7, 4)
//
//        val layoutManager = LinearLayoutManager(context)
//        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
//        binding.tripRecyclerView.layoutManager = layoutManager
//
//        //binding
////        binding.tripRecyclerView.layoutManager = LinearLayoutManager(context)
//        binding.tripRecyclerView.adapter = MyTripAdapter(tripImg, tripTitle, tripNowMem, tripTotalMem)
//        binding.tripRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

                    binding.tripPersonalBtn.setOnClickListener {
                        val intent = Intent(context, InputTripActivity::class.java)
                        intent.putExtra("check",0)
                        startActivity(intent)
                    }
                    binding.tripTeamBtn.setOnClickListener {
                        val intent = Intent(context, InputTripActivity::class.java)
                        intent.putExtra("check",1)
                        startActivity(intent)
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    call.cancel()
                }
            })
        }


        return binding.root
    }
}

class MyTripViewHolder(val binding: ItemTripIconBinding): RecyclerView.ViewHolder(binding.root)

class MyTripAdapter(val context: TripFragment,val tripImg:Array<Int>, val trip:List<Trip>?, val nickname:String): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    val TAG : String = "MyTripAdapter"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyTripViewHolder(ItemTripIconBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyTripViewHolder).binding
        Log.d(TAG,"position===========================$position")
        binding.imgTripRecruitTeam.setImageResource(tripImg[(0..2).random()])
        binding.tvTripRecruitTeamTitle.text = trip?.get(position)?.title
        var tripNowMem = trip?.get(position)?.member?.split(",")
        Log.d(TAG,"tripNowMem===========================$tripNowMem")
        binding.tvTripTeamNowMem.text = tripNowMem?.size?.minus(1).toString()

        holder.binding.btnTripEnter.setOnClickListener {
            val intent = Intent(context.activity, ChatActivity::class.java) //fragment -> activity Intent
            intent.putExtra("title", trip?.get(position)?.title)
            intent.putExtra("member", trip?.get(position)?.member)
            intent.putExtra("nickname",nickname)

            context?.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        Log.d(TAG,"itemSize===============================${trip?.size}")
        return trip?.size?:0
    }

}
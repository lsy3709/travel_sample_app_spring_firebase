package com.android4.travel.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android4.travel.AddActivity
import com.android4.travel.AuthActivity


import com.android4.travel.MyApplication_FB

import com.android4.travel.R

import com.android4.travel.databinding.FragmentFireBaseStoreDBBinding
import com.android4.travel.model.ItemData
import com.android4.travel.recycler.MyAdapter
import com.android4.travel.util.myCheckPermission



class FireBaseStoreDBFragment : Fragment() {
    lateinit var binding: FragmentFireBaseStoreDBBinding
    lateinit var adapter: MyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFireBaseStoreDBBinding.inflate(inflater, container, false)

//        myCheckPermission(activity as AppCompatActivity)
        binding.addFab.setOnClickListener {
            if(MyApplication_FB.checkAuth()){
                startActivity(Intent(activity, AddActivity::class.java))
            }else {
                Toast.makeText(activity, "인증진행해주세요..", Toast.LENGTH_SHORT).show()
            }
        }


        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if(!MyApplication_FB.checkAuth()){
            binding.logoutTextView.visibility= View.VISIBLE
            binding.mainRecyclerView.visibility=View.GONE
        }else {
            binding.logoutTextView.visibility= View.GONE
            binding.mainRecyclerView.visibility=View.VISIBLE
            makeRecyclerView()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
//        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        startActivity(Intent(activity, AuthActivity::class.java))
        return super.onOptionsItemSelected(item)
    }

    private fun makeRecyclerView(){
        MyApplication_FB.db.collection("news")
            .get()
            .addOnSuccessListener {result ->
                val itemList = mutableListOf<ItemData>()
                for(document in result){
                    val item = document.toObject(ItemData::class.java)
                    item.docId=document.id
                    itemList.add(item)
                }
                binding.mainRecyclerView.layoutManager = LinearLayoutManager(activity)
                binding.mainRecyclerView.adapter = context?.let { MyAdapter(it, itemList) }
            }
            .addOnFailureListener{exception ->
                Log.d("kkang", "error.. getting document..", exception)
                Toast.makeText(activity, "서버 데이터 획득 실패", Toast.LENGTH_SHORT).show()
            }
    }


}
package com.android4.travel.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android4.travel.DiaryFiles.Diary2Activity
import com.android4.travel.DiaryFiles.DiaryActivity
import com.android4.travel.databinding.FragmentDiaryCal2Binding
import java.util.*

//, View.OnClickListener
class DiaryCal2Fragment : Fragment() {
    lateinit var binding: FragmentDiaryCal2Binding
    lateinit var recyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiaryCal2Binding.inflate(inflater, container, false)


        binding.btnCal2.setOnClickListener {
            //호출시 문제 발생
            val intent = Intent(activity, Diary2Activity::class.java)
            startActivity(intent)
        }
        return binding.root
    }


}
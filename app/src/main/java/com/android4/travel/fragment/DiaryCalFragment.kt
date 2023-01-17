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
import com.android4.travel.DiaryFiles.DiaryActivity
import com.android4.travel.databinding.FragmentDiaryCalBinding
import java.util.*

//, View.OnClickListener
class DiaryCalFragment : Fragment() {
    lateinit var binding: FragmentDiaryCalBinding
    lateinit var recyclerView: RecyclerView
    var dateString = ""
    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDiaryCalBinding.inflate(inflater, container, false)


        binding.date.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth  ->
                dateString = "${year}년 ${month+1}월 ${dayOfMonth}일"
                binding.calendarData.setText("날짜 :" +dateString)
            }

            activity?.let { it1 ->
                DatePickerDialog(
                    it1, dateSetListener, cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            binding.btnCal.visibility=View.VISIBLE

        }

        binding.btnCal.setOnClickListener {
            val intent = Intent(activity, DiaryActivity::class.java)
            intent.putExtra("year",dateString)
            startActivity(intent)
        }
        return binding.root
    }


}
package com.android4.travel.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android4.travel.databinding.ItemCalendarBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DateHolder(val binding: ItemCalendarBinding): RecyclerView.ViewHolder(binding.root)

class DateAdapter(val dates:ArrayList<Long>?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    = DateHolder(ItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as DateHolder).binding

        binding.tvDateCalendarItem.text = convertLongToDate2(dates?.get(position) ?:0)
        binding.tvDayCalendarItem.text = convertLongToDate3(dates?.get(position) ?:0)
    }

    override fun getItemCount(): Int {
        return (dates?.size) ?:0
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
private fun convertLongToDate2(time: Long):String {

    val date = Date(time)
    val format = SimpleDateFormat(
        "dd",
        Locale.getDefault()
    )

    return format.format(date)
}
private fun convertLongToDate3(time: Long):String {

    val date = Date(time)
    val format = SimpleDateFormat(
        "E",
        Locale.getDefault()
    )

    return format.format(date)
}
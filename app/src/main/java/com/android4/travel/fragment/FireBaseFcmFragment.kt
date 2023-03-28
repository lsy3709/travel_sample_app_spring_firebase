package com.android4.travel.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android4.travel.databinding.FragmentFireBaseFcmBinding
import com.android4.travel.datingTest.auth.JoinActivity
import com.android4.travel.datingTest.auth.LoginDateActivity


class FireBaseFcmFragment : Fragment() {
    lateinit var binding: FragmentFireBaseFcmBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFireBaseFcmBinding.inflate(inflater, container, false)

        binding.joinBtn.setOnClickListener {

            startActivity(Intent(activity, JoinActivity::class.java))
        }

        binding.loginBtn.setOnClickListener {

            startActivity(Intent(activity, LoginDateActivity::class.java))

        }

        return binding.root
    }
}
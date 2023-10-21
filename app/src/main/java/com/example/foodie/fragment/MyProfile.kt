package com.example.foodie.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.foodie.R


class MyProfile : Fragment() {
    lateinit var Image: ImageView
    lateinit var name : TextView
    lateinit var MobileNum : TextView
    lateinit var EmailAdd : TextView
    lateinit var address : TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        Image = view.findViewById(R.id.imgProfileImage)
        name = view.findViewById(R.id.txtProName)
        MobileNum = view.findViewById(R.id.txtProMobileNum)
        EmailAdd = view.findViewById(R.id.txtProEmailAdd)
        address = view.findViewById(R.id.txtProAddress)

        val pre = this.requireActivity()
            .getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        name.text = pre.getString("UName","Name")
        MobileNum.text = pre.getString("Num","Mobile Number")
        address.text = pre.getString("address","Address")
        EmailAdd.text = pre.getString("EAddress","Email Address")
        return view
    }
}
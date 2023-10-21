package com.example.foodie.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodie.R
import com.example.foodie.adapter.History1_adapter
import com.example.foodie.model.History1
import com.example.foodie.util.ConnectionManager
import org.json.JSONException

class OrderHistory : Fragment() {
    lateinit var Rec_History1 : RecyclerView
    lateinit var layoutManager:LayoutManager
    lateinit var Rec_adapter: History1_adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        Rec_History1 = view.findViewById(R.id.Rec_History)

        activity?.setTitle("OrderHistory")



        val share = this.requireActivity()
            .getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val user_id = share?.getString("user_id", "0")
        Log.i("This is OrderHistory", "The user id is $user_id")

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$user_id"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val firstArray = ArrayList<History1>()

            val Jsom = object :JsonObjectRequest(Request.Method.GET,url,null,Response.Listener {
                  try {
                      val data = it.getJSONObject("data")
                      val success = data.getBoolean("success")
                      if(success){
                          val nesdata = data.getJSONArray("data")
                          for(i in 0 until nesdata.length()){
                              val obj = nesdata.getJSONObject(i)
                              val value = History1(obj.getString("restaurant_name"),obj.getString("order_placed_at").substring(0,9))
                              firstArray.add(value)
                          }
                          Rec_adapter  = History1_adapter(activity as Context,firstArray, user_id!!.toInt())
                          layoutManager = LinearLayoutManager(activity as Context)
                          Rec_History1.layoutManager = layoutManager
                          Rec_History1.adapter = Rec_adapter
                          Rec_History1.addItemDecoration(DividerItemDecoration(Rec_History1.context,(layoutManager as LinearLayoutManager).orientation))
                      }
                      else{
                          Toast.makeText(context,"some error occurred 1", Toast.LENGTH_SHORT).show()
                      }
                  }catch (e:JSONException){
                      Toast.makeText(context,"some error occurred2 ", Toast.LENGTH_SHORT).show()
                  }

            },Response.ErrorListener {
                Toast.makeText(context,"some error occurred3 ", Toast.LENGTH_SHORT).show()

            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val header = HashMap<String, String>()
                    header["content"] = "application/json"
                    header["token"] = "2cd8da1c3b65d3"
                    return header
                }

            }
            queue.add(Jsom)

        }
        else{
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("No Internet")
            dialog.setMessage("Internet connection not found!")
            dialog.setPositiveButton("Open Settings"){text,listener->
                val settingIntent  = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingIntent)

            }
            dialog.setNegativeButton("Exit"){text,listener->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view

    }
}
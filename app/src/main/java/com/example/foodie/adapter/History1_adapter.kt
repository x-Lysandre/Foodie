package com.example.foodie.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodie.R
import com.example.foodie.model.History1
import com.example.foodie.util.ConnectionManager
import org.json.JSONException

class History1_adapter(val context:Context,val arrayList: ArrayList<History1>,val user_id:Int) : RecyclerView.Adapter<History1_adapter.History1ViewHolder>() {

    class History1ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val Rec_History2 : RecyclerView = view.findViewById(R.id.Rec_History2)
        lateinit var layoutManager: LayoutManager
        lateinit var adapter2 : History2_adapter
        val RestaurantName : TextView = view.findViewById(R.id.Name2)
        val Date : TextView = view.findViewById(R.id.date)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): History1ViewHolder {
        val view  = LayoutInflater.from(parent.context).inflate(R.layout.history_single_view,parent,false)
        return History1ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: History1ViewHolder, position: Int) {
        val obj = arrayList[position]
        holder.RestaurantName.text = obj.Res_Name
        holder.Date.text = obj.Date

        val queue = Volley.newRequestQueue(context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$user_id"
        if (ConnectionManager().checkConnectivity(context)) {
            val firstArray = ArrayList<History1>()

            val Jsom = object : JsonObjectRequest(Request.Method.GET,url,null, Response.Listener {
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if(success){
                        val nesdata = data.getJSONArray("data")
                        for(i in 0 until nesdata.length()){
                            val obj = nesdata.getJSONObject(i)
                            val value = obj.getJSONArray("food_items")
                            for(j in 0 until value.length()){
                                val obj2 = value.getJSONObject(j)
                                val result = History1(obj2.getString("name"),obj2.getString("cost"))

                                firstArray.add(result)
                            }
                        }
                        holder.adapter2 = History2_adapter(context,firstArray)
                        holder.layoutManager = LinearLayoutManager(context)
                        holder.Rec_History2.layoutManager = holder.layoutManager
                        holder.Rec_History2.adapter = holder.adapter2

                    }
                    else{
                        Toast.makeText(context,"some error occurred ",Toast.LENGTH_SHORT).show()
                    }
                }catch (e: JSONException){
                    Toast.makeText(context,"some error occurred ",Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                Toast.makeText(context,"some error occurred ",Toast.LENGTH_SHORT).show()
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

    }
}
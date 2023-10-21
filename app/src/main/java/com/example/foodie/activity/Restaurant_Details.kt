package com.example.foodie.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodie.R
import com.example.foodie.adapter.Details_Adapter
import com.example.foodie.model.Details
import com.example.foodie.util.ConnectionManager
import org.json.JSONException

class Restaurant_Details : AppCompatActivity() {

    lateinit var proceedToCartLayout: RelativeLayout
    lateinit var ButtonProCart: Button
    lateinit var RecyclerView : RecyclerView
    lateinit var layoutManager : LayoutManager
    lateinit var CustomToolbar: androidx.appcompat.widget.Toolbar

    var Details_Array = ArrayList<Details>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_details)
        val Res_Name = intent.getStringExtra("Res_Name")
        val Res_id = intent.getStringExtra("Res_id")

        proceedToCartLayout = findViewById(R.id.ProceedToCartLayout)
        ButtonProCart = findViewById(R.id.btnProCart)
        RecyclerView = findViewById(R.id.Recycler_Details)
        layoutManager = LinearLayoutManager(this)
        proceedToCartLayout.visibility = View.INVISIBLE
        CustomToolbar = findViewById(R.id.appbar)

        setSupportActionBar(CustomToolbar)
        supportActionBar?.title = Res_Name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        if(ConnectionManager().checkConnectivity(this)){
            val queue = Volley.newRequestQueue(this)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/$Res_id"
            val jsonObjectRequest = object: JsonObjectRequest(Request.Method.GET,
                url,
                null,
                Response.Listener{
                                 try{
                                     val data = it.getJSONObject("data")
                                     val success = data.getBoolean("success")
                                     if(success){
                                         val nesData = data.getJSONArray("data")
                                         for(i in 0 until nesData.length()){
                                             val resObject = nesData.getJSONObject(i)
                                             val arrayItem = Details(
                                                 resObject.getString("id"),
                                                 resObject.getString("name"),
                                                 resObject.getString("cost_for_one"),
                                             )
                                             Details_Array.add(arrayItem)
                                         }
                                         val detailsAdapter = Details_Adapter(this,Details_Array,
                                             ButtonProCart,proceedToCartLayout,Res_Name,Res_id,RecyclerView)
                                         RecyclerView.layoutManager = layoutManager
                                         RecyclerView.adapter = detailsAdapter
                                     }

                                 }catch(e:JSONException){
                                     Toast.makeText(this,"some error occurred", Toast.LENGTH_SHORT).show()
                                 }

                },Response.ErrorListener {
                    Toast.makeText(this,"some error occurred", Toast.LENGTH_SHORT).show()
                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val header = HashMap<String, String>()
                    header["content"] = "application/json"
                    header["token"] = "2cd8da1c3b65d3"
                    return header
                }
            }
            queue.add(jsonObjectRequest)
        }else{
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("INTERNET connection not found !")
            dialog.setPositiveButton("Open Settings"){text,listener->
                val settingIntent  = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingIntent)
                finish()

            }
            dialog.setNegativeButton("Exit"){text,listener->
                ActivityCompat.finishAffinity(this)

            }
            dialog.create()
            dialog.show()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this,Login::class.java)
                startActivity(intent)
                this.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
package com.example.foodie.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodie.R
import com.example.foodie.adapter.Cart_Adapter
import com.example.foodie.model.Cart_Items
import com.example.foodie.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class Cart : AppCompatActivity() {

    lateinit var customToolbar: androidx.appcompat.widget.Toolbar
    lateinit var HeadingText: TextView
    lateinit var PlaceOrder: Button
    lateinit var Rec_cart: RecyclerView
    lateinit var layoutManager: LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        customToolbar = findViewById(R.id.appbar)
        HeadingText = findViewById(R.id.txtCartRestaurantName)
        PlaceOrder = findViewById(R.id.btnPlaceOrder)
        Rec_cart = findViewById(R.id.Recycler_cart)

        val Items = intent.getStringArrayListExtra("ItemsIds")
        val ResName = intent.getStringExtra("Res_Name")
        val ResID = intent.getStringExtra("Res_id")
        val total = intent.getIntExtra("total", 0)

        Log.i("This is cart activity", "the array is : " + Items.toString())
        Log.i("This is cart total", "total is : $total")
        Log.i("This is cart total", "restaurnat : $ResName")

        val sharepref = getSharedPreferences("Restaurant file", Context.MODE_PRIVATE)
        val id = sharepref.getString("user_id", "1")

        setSupportActionBar(customToolbar)
        supportActionBar?.setTitle("CART")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        HeadingText.text = ResName
        PlaceOrder.text = "Place order(total: $total)"

        fetchData(ResID!!.toInt(), Items)

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"

        PlaceOrder.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {
                try {
                    val foodItem = JSONObject()
                    if (Items != null) {
                        for (i in Items) {
                            foodItem.put("food_item_id", i)
                        }
                    } else {
                        Toast.makeText(this, "The array is empty", Toast.LENGTH_SHORT).show()
                    }
                    val param = JSONObject()
                    param.put("user_id", id)
                    param.put("restaurant_id", ResID)
                    param.put("total_cost", total)
                    param.put("food", foodItem)

                    val JsonObjectRequest = object :
                        JsonObjectRequest(Request.Method.POST, url, param, Response.Listener {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                val intent = Intent(this,Order_placed::class.java)
                                startActivity(intent)
                                Toast.makeText(this, "Order Placed successfully ", Toast.LENGTH_SHORT).show()

                            } else {
                                Toast.makeText(this, "some error occurred", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }, Response.ErrorListener {
                            Toast.makeText(this, "some error occurred", Toast.LENGTH_SHORT).show()

                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["content"] = "application/json"
                            headers["token"] = "2cd8da1c3b65d3"
                            return headers
                        }
                    }
                    queue.add(JsonObjectRequest)

                } catch (e: java.lang.Exception) {
                    Toast.makeText(this, "Some error Occurred", Toast.LENGTH_SHORT).show()
                }


            } else {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("INTERNET connection not found !")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingIntent = Intent(Settings.ACTION_SETTINGS)
                    startActivity(settingIntent)
                    finish()

                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this)

                }
                dialog.create()
                dialog.show()
            }
        }
    }

    fun fetchData(ResID: Int, Items: java.util.ArrayList<String>?) {

        if (ConnectionManager().checkConnectivity(this)) {
            try {
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/restaurants/fetch_result/$ResID"
                val cartListItems = ArrayList<Cart_Items>()

                val jsonObjectRequest = object : JsonObjectRequest(
                    Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val response = it.getJSONObject("data")
                        val success = response.getBoolean("success")
                        if (success) {
                            val data = response.getJSONArray("data")

                            for (i in 0 until data.length()) {
                                val cartItem = data.getJSONObject(i)
                                if (Items!!.contains(cartItem.getString("id"))) {
                                    val menuObject = Cart_Items(
                                        cartItem.getString("id"),
                                        cartItem.getString("name"),
                                        cartItem.getString("cost_for_one"),
                                        cartItem.getString("restaurant_id")
                                    )

                                    cartListItems.add(menuObject)

                                }
                                val recyleradapter = Cart_Adapter(this, cartListItems)
                                layoutManager = LinearLayoutManager(this)
                                Rec_cart.layoutManager = layoutManager
                                Rec_cart.adapter = recyleradapter

                            }

                        }
                    },
                    Response.ErrorListener {

                        Toast.makeText(
                            this,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "2cd8da1c3b65d3"
                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(
                    this,
                    "Some Unexpected error occurred!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Check Internet Connection!")
            alterDialog.setPositiveButton("Open Settings") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }
            alterDialog.setNegativeButton("Exit") { _, _ ->
                finishAffinity()
            }
            alterDialog.setCancelable(false)
            alterDialog.create()
            alterDialog.show()
        }
    }
}
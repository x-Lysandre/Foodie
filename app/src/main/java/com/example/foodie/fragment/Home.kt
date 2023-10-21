package com.example.foodie.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodie.R
import com.example.foodie.adapter.Home_adapter
import com.example.foodie.database.Food_Entity
import com.example.foodie.database.ResDatabase
import com.example.foodie.model.Restaurant
import com.example.foodie.util.ConnectionManager
import kotlinx.coroutines.launch
import org.json.JSONException
import java.util.Collections

class Home : Fragment() {
    lateinit var layoutManager: LayoutManager
    lateinit var RecyclerAdapter: Home_adapter
    lateinit var rec_home: RecyclerView
    lateinit var Progresslayout : RelativeLayout

    val RestaurantList = arrayListOf <Restaurant>()

    var RatingComparator = Comparator<Restaurant>{Res1,Res2 ->
        if(Res1.Rating.compareTo(Res2.Rating)==0){
            Res1.Name.compareTo(Res2.Name,true)
        }
        else{
            Res1.Rating.compareTo(Res2.Rating,true)
        }
    }
    val NameComparator = Comparator<Restaurant>{Res1,Res2 ->
        Res1.Name.compareTo(Res2.Name,true)
    }
    val PriceComparator = Comparator<Restaurant>{Res1,Res2 ->
        if(Res1.Price.compareTo(Res2.Price,true)==0){
            Res1.Name.compareTo(Res2.Name,true)
        }
        else{
            Res1.Price.compareTo(Res2.Price,true)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val queue = Volley.newRequestQueue(activity as Context)

        layoutManager = LinearLayoutManager(activity)
        rec_home = view.findViewById(R.id.recycler_home)
        Progresslayout = view.findViewById(R.id.ProgressLayout)
        Progresslayout.visibility = View.INVISIBLE

        setHasOptionsMenu(true)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if(ConnectionManager().checkConnectivity(activity as Context)){
            Progresslayout.visibility = View.VISIBLE
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                try{
                    val response = it.getJSONObject("data")
                    val success = response.getBoolean("success")
                    if(success){
                        val data = response.getJSONArray("data")
                        for(i in 0 until data.length()  ){
                            val ResOject = data.getJSONObject(i)
                            val  res_obj = Restaurant(
                                ResOject.getString("id"),
                                ResOject.getString("name"),
                                ResOject.getString("rating"),
                                ResOject.getString("cost_for_one"),
                                ResOject.getString("image_url")
                            )
                            RestaurantList.add(res_obj)
                        }
                        RecyclerAdapter = Home_adapter(activity as Context,RestaurantList)
                        rec_home.layoutManager = layoutManager
                        rec_home.adapter = RecyclerAdapter
                        Progresslayout.visibility = View.INVISIBLE
                    }else{
                        Progresslayout.visibility = View.INVISIBLE
                        Toast.makeText(activity as Context,"some error occurred",Toast.LENGTH_SHORT).show()
                    }

                }catch (e: JSONException){
                    Progresslayout.visibility = View.INVISIBLE
                    Toast.makeText(activity as Context,"Something unexpected happened!!",Toast.LENGTH_SHORT).show()
                }

            }, Response.ErrorListener {
                Progresslayout.visibility = View.INVISIBLE
                Toast.makeText(activity as Context ,"some error occurred",Toast.LENGTH_SHORT).show()

            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val header = HashMap<String, String>()
                    header["content"] = "application/json"
                    header["token"] = "2cd8da1c3b65d3"
                    return header
                }

            }
            queue.add(jsonObjectRequest)

        }else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("INTERNET connection not found !")
            dialog.setPositiveButton("Open Settings"){text,listener->
                val settingIntent  = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()

            }
            dialog.setNegativeButton("Exit"){text,listener->
                ActivityCompat.finishAffinity(activity as Activity)

            }
            dialog.create()
            dialog.show()
        }

        return view
    }
    class dbAsyncTask(val context:Context,val foodEntity: Food_Entity,val mode:Int) : AsyncTask<Void,Void,Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {

            val db = Room.databaseBuilder(context,ResDatabase::class.java,"food-db").build()

            when(mode){
                1->{
                    val food : Food_Entity? = db.resDao().getResById(foodEntity.Res_id.toString())
                    db.close()
                    return food != null
                }
                2->{
                    db.resDao().insert_Res(foodEntity)
                    db.close()
                    return true
                }
                3->{
                    db.resDao().delete_Res(foodEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.sort,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item?.itemId
        if(id == R.id.RatingSort){
            Collections.sort(RestaurantList,RatingComparator)
            RestaurantList.reverse()
        }
        else if(id == R.id.NameSort){
            Collections.sort(RestaurantList,NameComparator)
            RestaurantList.reverse()
        }
        else if(id == R.id.PriceSort){
            Collections.sort(RestaurantList,PriceComparator)
            RestaurantList.reverse()
        }
        RecyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }


}
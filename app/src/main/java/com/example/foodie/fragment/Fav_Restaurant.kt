package com.example.foodie.fragment

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log.i
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.toolbox.Volley
import com.example.foodie.R
import com.example.foodie.adapter.Favourite_Adapter
import com.example.foodie.database.Food_Entity
import com.example.foodie.database.ResDatabase
import com.example.foodie.util.ConnectionManager


class Fav_Restaurant : Fragment() {

    lateinit var Rec_fav:RecyclerView
    lateinit var progressLayout: RelativeLayout
    lateinit var LayoutManager: RecyclerView.LayoutManager
    lateinit var RecyclerAdapter:Favourite_Adapter
    var list = listOf<Food_Entity>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_fav__restaurant, container, false)

        Rec_fav = view.findViewById(R.id.recycler_fav)
        progressLayout = view.findViewById(R.id.ProgressLayoutFav)
        progressLayout.visibility = View.VISIBLE

        list = DBAsyncTask(activity as Context).execute().get()

        if( activity != null ){
            progressLayout.visibility = View.GONE
            RecyclerAdapter = Favourite_Adapter(activity as Context , list)
            LayoutManager = LinearLayoutManager(activity)
            Rec_fav.layoutManager = LayoutManager
            Rec_fav.adapter = RecyclerAdapter

        }
        return view
    }
    class DBAsyncTask(val context: Context):AsyncTask<Void,Void,List<Food_Entity>>(){
        override fun doInBackground(vararg params: Void?): List<Food_Entity> {
            val db = Room.databaseBuilder(context,ResDatabase::class.java,"food-db").build()
            return db.resDao().getAllRes()
        }
    }
}
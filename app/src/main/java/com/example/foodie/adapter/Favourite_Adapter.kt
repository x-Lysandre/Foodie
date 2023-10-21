package com.example.foodie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.foodie.R
import com.example.foodie.database.Food_Entity
import com.example.foodie.fragment.Home
import com.squareup.picasso.Picasso

class Favourite_Adapter(val context: Context,val ResList: List<Food_Entity>):
    RecyclerView.Adapter<Favourite_Adapter.FavouriteViewHolder>() {
    class FavouriteViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtText: TextView = view.findViewById(R.id.txtText)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val imgFoodImg: ImageView = view.findViewById(R.id.imgFoodImg)
        val cbfav: CheckBox = view.findViewById(R.id.cbFav)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.home_single_row, parent, false)
        return Favourite_Adapter.FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return ResList.size

    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val Res = ResList[position]
        holder.txtPrice.text = Res.Res_cost_for_one
        holder.txtText.text = Res.Res_Name
        holder.txtText.tag = Res.Res_id
        holder.txtRating.text = Res.Res_Rating
        Picasso.get().load(Res.Res_Image).error(R.drawable.image_default).into(holder.imgFoodImg)
        holder.cbfav.isChecked = true

        val singleEntity = Food_Entity(Res.Res_id,Res.Res_Name,Res.Res_Rating,Res.Res_cost_for_one,Res.Res_Image)
        holder.cbfav.setOnCheckedChangeListener { buttonView, isChecked ->
            Home.dbAsyncTask(context, singleEntity,3)
            Toast.makeText(context, "${Res.Res_Name} removed from Favorites", Toast.LENGTH_SHORT).show()
        }

    }
}
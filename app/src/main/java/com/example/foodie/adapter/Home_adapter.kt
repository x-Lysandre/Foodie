package com.example.foodie.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.foodie.R
import com.example.foodie.activity.Restaurant_Details
import com.example.foodie.database.Food_Entity
import com.example.foodie.fragment.Home
import com.example.foodie.model.Restaurant
import com.squareup.picasso.Picasso

class Home_adapter(val context: Context, val itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<Home_adapter.HomeViewHolder>() {
    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtText: TextView = view.findViewById(R.id.txtText)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val imgFoodImg: ImageView = view.findViewById(R.id.imgFoodImg)
        val Content: LinearLayout = view.findViewById(R.id.Clickable)
        val cbfav: CheckBox = view.findViewById(R.id.cbFav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.home_single_row, parent, false)
        return HomeViewHolder(view)

    }

    override fun getItemCount(): Int {
        return itemList.size

    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val Res = itemList[position]
        val foodEntity = Food_Entity(
            Res.id.toInt(),
            Res.Name,
            Res.Rating,
            Res.Price,
            Res.Imageview
        )
        holder.txtPrice.text = Res.Price
        holder.txtText.text = Res.Name
        holder.txtText.tag = Res.id + ""
        holder.txtRating.text = Res.Rating
//        holder.imgFoodImg.setImageResource(Res.Imageview)
        Picasso.get().load(Res.Imageview).error(R.drawable.image_default).into(holder.imgFoodImg)

        holder.Content.setOnClickListener {
            val intent = Intent(context, Restaurant_Details::class.java)
            intent.putExtra("Res_id", holder.txtText.tag.toString())
            intent.putExtra("Res_Name", holder.txtText.text.toString())
            context.startActivity(intent)
        }

        val value = Home.dbAsyncTask(context,foodEntity,1).execute().get()
        if(value){
            holder.cbfav.isChecked = true
        }
        holder.cbfav.setOnClickListener {
            if (!Home.dbAsyncTask(context, foodEntity, 1).execute().get()) {
                val result = Home.dbAsyncTask(context, foodEntity, 2).execute().get()

                if (result) {
                    Toast.makeText(context, "${Res.Name} added to Favorites", Toast.LENGTH_SHORT).show()
                    holder.cbfav.tag = "liked"

                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }

            } else {
                val result = Home.dbAsyncTask(context, foodEntity, 3).execute().get()
                if (result) {

                    Toast.makeText(context, "${Res.Name} removed from Favorites", Toast.LENGTH_SHORT).show()
                    holder.cbfav.tag = "unliked"


                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}
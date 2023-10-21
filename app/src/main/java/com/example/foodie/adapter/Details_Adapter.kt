package com.example.foodie.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Adapter
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodie.R
import com.example.foodie.activity.Cart
import com.example.foodie.model.Details
import kotlin.properties.Delegates

class Details_Adapter (val context: Context,
                       val itemList:ArrayList<Details>,
                       val buttonProCart:Button,
                       val proceedToCartLayout:RelativeLayout,
                       val RestaurantName: String?,
                       val RestaurantID:String?,
val RecyclerView:RecyclerView) : RecyclerView.Adapter<Details_Adapter.DetailsViewHolder>(){

    var itemC:Int = 0
    var ItemIds = arrayListOf<String>()
    var total:Int = 0

    class DetailsViewHolder(view: View):RecyclerView.ViewHolder(view){
        val Count: TextView =  view.findViewById(R.id.txtCount)
        val ItemName: TextView = view.findViewById(R.id.txtItemName)
        val ItemPrice: TextView = view.findViewById(R.id.txtItemPrice)
        val addToCart:Button= view.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.details_single_row,parent,false)
        return DetailsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size

    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        val Res = itemList[position]
        holder.Count.text = (position+1).toString()
        holder.ItemName.text = Res.name
        holder.ItemPrice.text = Res.cost_for_one
        val id = Res.id

        buttonProCart.setOnClickListener{
            val intent = Intent(context, Cart::class.java)
            intent.putStringArrayListExtra("ItemsIds",ItemIds)
            intent.putExtra("Res_id",RestaurantID)
            intent.putExtra("Res_Name",RestaurantName)
            intent.putExtra("total",total)
            context.startActivity(intent)
            Log.i("This is deatials adapter ","the array is : " + ItemIds.toString())
            Log.i("This the details adapter","The res is $RestaurantName")

        }


        holder.addToCart.setOnClickListener{
            if(holder.addToCart.text.toString() == "Remove"){
                itemC--
                holder.addToCart.text = "ADD"
                holder.addToCart.setBackgroundColor(ContextCompat.getColor(context,R.color.cyan_a400))
                total -= Res.cost_for_one.toInt()
                ItemIds.remove(id)
            }
            else{
                holder.addToCart.text = "Remove"
                holder.addToCart.setBackgroundColor(Color.RED)
                itemC++
                ItemIds.add(id)
                total += Res.cost_for_one.toInt()
            }
            if(itemC>0){
                proceedToCartLayout.visibility = View.VISIBLE
                val param = RecyclerView.layoutParams as MarginLayoutParams
                param.setMargins(0,0,0,130)
                RecyclerView.layoutParams = param
            }
            else{
                proceedToCartLayout.visibility = View.INVISIBLE
                val param = RecyclerView.layoutParams as MarginLayoutParams
                param.setMargins(0,0,0,0)
                RecyclerView.layoutParams = param
            }
        }

    }
}
package com.example.foodie.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodie.R
import com.example.foodie.database.Food_Entity
import com.example.foodie.database.ResDatabase
import com.example.foodie.model.Cart_Items

class Cart_Adapter(val context: Context, val Cart_Array: java.util.ArrayList<Cart_Items>):RecyclerView.Adapter< Cart_Adapter.CartViewHolder>() {

    class CartViewHolder(val view: View):RecyclerView.ViewHolder(view){
        var CartCount: TextView = view.findViewById(R.id.txtCartCount)
        var CartItemName: TextView = view.findViewById(R.id.txtCartItemName)
        var CartItemPrice: TextView = view.findViewById(R.id.txtCartItemPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_single_row,parent,false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Cart_Array!!.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cart = Cart_Array[position]
        holder.CartCount.text = (position + 1).toString()
        holder.CartItemName.text = cart.name
        holder.CartItemPrice.text = cart.price



    }
}
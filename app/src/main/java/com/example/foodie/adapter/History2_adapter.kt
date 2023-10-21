package com.example.foodie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodie.R
import com.example.foodie.model.History1
import org.w3c.dom.Text

class History2_adapter(val context: Context, val array:ArrayList<History1>):RecyclerView.Adapter<History2_adapter.History2ViewHolder>() {

    class History2ViewHolder(val view: View):RecyclerView.ViewHolder(view){
        val itemName:TextView =view.findViewById(R.id.item_name_history2)
        val price:TextView = view.findViewById(R.id.price_history2)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): History2ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.history2_single_view,parent,false)
        return History2ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return array.size
    }

    override fun onBindViewHolder(holder: History2ViewHolder, position: Int) {
        val obj = array[position]
        holder.itemName.text = obj.Res_Name
        holder.price.text = obj.Date


    }
}
package com.ai.concise.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ai.concise.R
import com.ai.concise.models.RulesModel

class RulesAdapter(private val userList: ArrayList<RulesModel>) : RecyclerView.Adapter<RulesAdapter.MyViewHolder>(){

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title : TextView = itemView.findViewById(R.id.title_terms)
        val disc : TextView = itemView.findViewById(R.id.description_terms)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.terms_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = userList[position].text
        holder.disc.text = userList[position].text1
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
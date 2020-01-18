package com.example.warmhearts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.warmhearts.activities.CallBackInterface
import com.example.warmhearts.models.Post
import com.squareup.picasso.Picasso

class MainRwAdapter(private val mainDataList: ArrayList<Post>, private val callBackInterface: CallBackInterface): RecyclerView.Adapter<MainRwAdapter.MainRwViewHolder>() {
    private var dataList = ArrayList(mainDataList)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainRwViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rw_list_item, parent, false)
        return MainRwViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MainRwViewHolder, position: Int) {
        holder.header.text = dataList[position].header
        holder.age.text = "yaş: ${dataList[position].age}"
        holder.location.text = dataList[position].location
        Picasso.get()
            .load(dataList[position].photo)
            .centerInside()
            .resize(100,0)
            .into(holder.mainImage)

        holder.itemView.setOnClickListener {
            callBackInterface.clicked(position)
        }
    }
    fun updateData(dataList: ArrayList<Post>){
        this.dataList = dataList
        notifyDataSetChanged()
    }


    class MainRwViewHolder(view:View): RecyclerView.ViewHolder(view){
        val mainImage = view.findViewById<ImageView>(R.id.imageView_rw_list_item_header)
        val header = view.findViewById<TextView>(R.id.txt_rw_list_item_header)
        val location = view.findViewById<TextView>(R.id.txt_rw_list_item_location)
        val age = view.findViewById<TextView>(R.id.txt_rw_list_item_age)
    }
}
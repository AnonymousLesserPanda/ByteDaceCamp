package com.example.bytedancecamplab2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bytedancecamplab2.NoteDataBaseHelper.InfoCard

class InfoCardAdapter(private val infoCardList: List<InfoCard>) : RecyclerView.Adapter<InfoCardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.info_card, parent, false)
        val viewHolder = InfoCardViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(
        holder: InfoCardViewHolder,
        position: Int
    ) {
        holder.title.text = infoCardList[position].title
        holder.brief.text = infoCardList[position].brief
        holder.time.text = infoCardList[position].time
    }

    override fun getItemCount(): Int {
        return infoCardList.size
    }
}
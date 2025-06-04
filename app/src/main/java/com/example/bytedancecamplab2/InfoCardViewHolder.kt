package com.example.bytedancecamplab2

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bytedancecamplab2.NoteDataBaseHelper.InfoCard

class InfoCardViewHolder(infoCardView: View) : RecyclerView.ViewHolder(infoCardView) {
    var title: TextView = infoCardView.findViewById<TextView>(R.id.title)
    var brief: TextView = infoCardView.findViewById<TextView>(R.id.brief)
    var time: TextView = infoCardView.findViewById<TextView>(R.id.time)

    fun bind(card: InfoCard) {
        title.text = card.title
        brief.text = card.brief
        time.text = card.time
    }
}
package com.example.bytedancecamplab2

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bytedancecamplab2.NoteDataBaseHelper.InfoCard

class InfoCardViewHolder(infoCardView: View) : RecyclerView.ViewHolder(infoCardView) {
    private var title = infoCardView.findViewById<TextView>(R.id.title)
    private var brief = infoCardView.findViewById<TextView>(R.id.brief)
    private var time = infoCardView.findViewById<TextView>(R.id.time)

    fun bind(card: InfoCard) {
        title.text = card.title
        brief.text = card.brief
        time.text = card.time
    }
}
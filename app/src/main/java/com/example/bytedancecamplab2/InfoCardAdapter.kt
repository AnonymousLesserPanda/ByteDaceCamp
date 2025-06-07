package com.example.bytedancecamplab2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import com.example.bytedancecamplab2.NoteDataBaseHelper.InfoCard

class InfoCardAdapter : ListAdapter<InfoCard, InfoCardViewHolder>(InfoCardDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoCardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.info_card, parent, false)
        return InfoCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoCardViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class InfoCardDiffCallBack : ItemCallback<InfoCard>() {
    override fun areContentsTheSame(oldItem: InfoCard, newItem: InfoCard): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: InfoCard, newItem: InfoCard): Boolean {
        return oldItem == newItem
    }
}
package com.example.douyinexperiencesection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FeedAdapter(private val items: List<FeedItem>) :
    RecyclerView.Adapter<FeedAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCover: ImageView = view.findViewById(R.id.ivCover)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = items[position]
        holder.tvDescription.text = currentItem.title

        holder.ivCover.setImageResource(android.R.color.darker_gray)

        if (currentItem.imageUrl.isNotEmpty()) {
            ImageLoader.loadImage(holder.itemView.context, currentItem.imageUrl, holder.ivCover)
        }

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = android.content.Intent(context, DetailActivity::class.java).apply {
                putExtra("EXTRA_IMAGE_URL", currentItem.imageUrl)
                putExtra("EXTRA_TITLE", currentItem.title)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
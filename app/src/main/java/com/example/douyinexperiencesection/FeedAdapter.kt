package com.example.douyinexperiencesection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

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

        if (currentItem.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(currentItem.imageUrl)
                .placeholder(android.R.color.darker_gray)
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.ivCover)
        } else {
            holder.ivCover.setImageResource(android.R.color.darker_gray)
        }

        androidx.core.view.ViewCompat.setTransitionName(holder.ivCover, currentItem.imageUrl)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = android.content.Intent(context, DetailActivity::class.java).apply {
                putExtra("EXTRA_IMAGE_URL", currentItem.imageUrl)
                putExtra("EXTRA_TITLE", currentItem.title)
            }
            
            val activity = context as? android.app.Activity
            if (activity != null) {
                // 告诉系统：把当前的 holder.ivCover 平滑过渡到下一个页面中名为 currentItem.imageUrl 的控件上
                val options = androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    holder.ivCover,
                    currentItem.imageUrl 
                )
                context.startActivity(intent, options.toBundle())
            } else {
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
package com.example.douyinexperiencesection

import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

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
        holder.ivCover.setTag(R.id.tag_image_loaded, false)

        if (currentItem.thumbnailUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(currentItem.thumbnailUrl)
                .placeholder(android.R.color.darker_gray)
                .priority(Priority.NORMAL)
                .override(500, 500)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .listener(object : RequestListener<android.graphics.drawable.Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<android.graphics.drawable.Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d(
                            PerformanceConfig.LOG_TAG,
                            "thumbnail list failed url=${currentItem.thumbnailUrl} error=${e?.rootCauses?.firstOrNull()?.javaClass?.simpleName}"
                        )
                        holder.ivCover.setTag(R.id.tag_image_loaded, false)
                        return false
                    }

                    override fun onResourceReady(
                        resource: android.graphics.drawable.Drawable,
                        model: Any,
                        target: Target<android.graphics.drawable.Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.ivCover.setTag(R.id.tag_image_loaded, true)
                        return false
                    }
                })
                .into(holder.ivCover)
        } else {
            holder.ivCover.setImageResource(android.R.color.darker_gray)
        }

        androidx.core.view.ViewCompat.setTransitionName(holder.ivCover, currentItem.thumbnailUrl)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val clickAt = SystemClock.elapsedRealtime()
            val canUseSharedElement = PerformanceConfig.ENABLE_SHARED_ELEMENT_TRANSITION &&
                    holder.ivCover.getTag(R.id.tag_image_loaded) == true
            val intent = android.content.Intent(context, DetailActivity::class.java).apply {
                putExtra("EXTRA_THUMBNAIL_URL", currentItem.thumbnailUrl)
                putExtra("EXTRA_DETAIL_URL", currentItem.detailUrl)
                putExtra("EXTRA_TITLE", currentItem.title)
                putExtra("EXTRA_CLICK_AT", clickAt)
                putExtra(
                    "EXTRA_TRANSITION_MODE",
                    if (canUseSharedElement) "shared" else "plain_fallback"
                )
            }
            Log.d(
                PerformanceConfig.LOG_TAG,
                "card click mode=${if (canUseSharedElement) "shared" else "plain_fallback"} loaded=${holder.ivCover.getTag(R.id.tag_image_loaded)} url=${currentItem.thumbnailUrl}"
            )
            
            val activity = context as? android.app.Activity
            if (activity != null && canUseSharedElement) {
                // 把当前缩略图平滑过渡到详情页中使用同一 transitionName 的控件上。
                val options = androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    holder.ivCover,
                    currentItem.thumbnailUrl 
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

    override fun onViewRecycled(holder: MyViewHolder) {
        Glide.with(holder.itemView).clear(holder.ivCover)
        holder.ivCover.setTag(R.id.tag_image_loaded, false)
        super.onViewRecycled(holder)
    }
}

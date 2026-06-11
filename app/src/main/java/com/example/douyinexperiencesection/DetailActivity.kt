package com.example.douyinexperiencesection

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

/**
 * 详情页控制器
 */
class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        setContentView(R.layout.activity_detail)

        val ivDetailCover = findViewById<ImageView>(R.id.ivDetailCover)
        val tvDetailTitle = findViewById<TextView>(R.id.tvDetailTitle)

        val imageUrl = intent.getStringExtra("EXTRA_IMAGE_URL") ?: ""
        val title = intent.getStringExtra("EXTRA_TITLE") ?: ""

        tvDetailTitle.text = title

        val tvLikeBtn = findViewById<TextView>(R.id.tvLikeBtn)
        var isLiked = false

        tvLikeBtn.setOnClickListener {
            isLiked = !isLiked
            if (isLiked) {
                tvLikeBtn.text = "❤️"
                tvLikeBtn.setTextColor(android.graphics.Color.parseColor("#FF2442"))
                tvLikeBtn.setBackgroundColor(android.graphics.Color.parseColor("#33FF2442"))
            } else {
                tvLikeBtn.text = "🤍"
                tvLikeBtn.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
                tvLikeBtn.setBackgroundColor(android.graphics.Color.parseColor("#33FFFFFF"))
            }
        }

        if (imageUrl.isNotEmpty()) {
            androidx.core.view.ViewCompat.setTransitionName(ivDetailCover, imageUrl)
            
            Glide.with(this)
                .load(imageUrl)
                .dontAnimate() 
                // 强制开启全磁盘缓存复用
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                .thumbnail(
                    Glide.with(this)
                        .load(imageUrl)
                        .centerCrop()
                )
                .into(ivDetailCover)
        }
    }
}

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
            
            supportPostponeEnterTransition()
            
            window.decorView.postDelayed({
                supportStartPostponedEnterTransition()
            }, 100)
            
            Glide.with(this)
                .load(imageUrl)
                .dontAnimate() 
                .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                    override fun onLoadFailed(
                        e: com.bumptech.glide.load.engine.GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(
                        resource: android.graphics.drawable.Drawable,
                        model: Any,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }
                })
                .into(ivDetailCover)
        }
    }
}

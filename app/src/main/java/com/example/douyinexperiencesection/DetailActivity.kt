package com.example.douyinexperiencesection

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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

        if (imageUrl.isNotEmpty()) {
            ImageLoader.loadImage(this, imageUrl, ivDetailCover)
        }
    }
}

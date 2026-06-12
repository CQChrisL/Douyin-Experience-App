package com.example.douyinexperiencesection

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

/**
 * 详情页控制器
 */
class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onCreateAt = SystemClock.elapsedRealtime()
        val clickAt = intent.getLongExtra("EXTRA_CLICK_AT", onCreateAt)
        val transitionMode = intent.getStringExtra("EXTRA_TRANSITION_MODE") ?: "unknown"
        val useSharedElement = transitionMode == "shared"
        Log.d(
            PerformanceConfig.LOG_TAG,
            "detail onCreate mode=$transitionMode clickToOnCreate=${onCreateAt - clickAt}ms"
        )

        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        setContentView(R.layout.activity_detail)
        window.decorView.doOnPreDraw {
            Log.d(
                PerformanceConfig.LOG_TAG,
                "detail firstPreDraw mode=$transitionMode clickToFirstPreDraw=${SystemClock.elapsedRealtime() - clickAt}ms"
            )
        }

        val ivDetailCover = findViewById<ImageView>(R.id.ivDetailCover)
        val tvDetailTitle = findViewById<TextView>(R.id.tvDetailTitle)
        ivDetailCover.setBackgroundColor(android.graphics.Color.parseColor("#202124"))

        val thumbnailUrl = intent.getStringExtra("EXTRA_THUMBNAIL_URL") ?: ""
        val detailUrl = intent.getStringExtra("EXTRA_DETAIL_URL") ?: thumbnailUrl
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

        if (thumbnailUrl.isNotEmpty()) {
            androidx.core.view.ViewCompat.setTransitionName(ivDetailCover, thumbnailUrl)

            if (useSharedElement) {
                supportPostponeEnterTransition()
            }
            var transitionStarted = false
            fun startTransitionOnce() {
                if (!transitionStarted) {
                    transitionStarted = true
                    Log.d(
                        PerformanceConfig.LOG_TAG,
                        "transition released mode=$transitionMode elapsed=${SystemClock.elapsedRealtime() - clickAt}ms"
                    )
                    if (useSharedElement) {
                        supportStartPostponedEnterTransition()
                    }
                    loadDetailImageAfterTransition(ivDetailCover, detailUrl, transitionMode, clickAt)
                }
            }

            window.decorView.postDelayed({
                startTransitionOnce()
            }, PerformanceConfig.TRANSITION_WAIT_TIMEOUT_MS)
            
            Glide.with(this)
                .load(thumbnailUrl)
                .dontAnimate() 
                .placeholder(android.R.color.darker_gray)
                .error(android.R.color.darker_gray)
                .onlyRetrieveFromCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .override(500, 500)
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
                            "thumbnail cache miss mode=$transitionMode elapsed=${SystemClock.elapsedRealtime() - clickAt}ms error=${e?.rootCauses?.firstOrNull()?.javaClass?.simpleName}"
                        )
                        startTransitionOnce()
                        return false
                    }

                    override fun onResourceReady(
                        resource: android.graphics.drawable.Drawable,
                        model: Any,
                        target: Target<android.graphics.drawable.Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d(
                            PerformanceConfig.LOG_TAG,
                            "thumbnail ready mode=$transitionMode elapsed=${SystemClock.elapsedRealtime() - clickAt}ms source=$dataSource first=$isFirstResource"
                        )
                        startTransitionOnce()
                        return false
                    }
                })
                .into(ivDetailCover)
        }
    }

    private fun loadDetailImageAfterTransition(
        imageView: ImageView,
        detailUrl: String,
        transitionMode: String,
        clickAt: Long
    ) {
        if (detailUrl.isEmpty()) return

        imageView.post {
            Log.d(
                PerformanceConfig.LOG_TAG,
                "detail image request start mode=$transitionMode elapsed=${SystemClock.elapsedRealtime() - clickAt}ms priority=IMMEDIATE"
            )

            Glide.with(this)
                .load(detailUrl)
                .dontAnimate()
                .placeholder(imageView.drawable ?: android.graphics.drawable.ColorDrawable(android.graphics.Color.DKGRAY))
                .error(android.R.color.darker_gray)
                .priority(Priority.IMMEDIATE)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .fitCenter()
                .listener(object : RequestListener<android.graphics.drawable.Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<android.graphics.drawable.Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d(
                            PerformanceConfig.LOG_TAG,
                            "detail image failed mode=$transitionMode elapsed=${SystemClock.elapsedRealtime() - clickAt}ms error=${e?.rootCauses?.firstOrNull()?.javaClass?.simpleName}"
                        )
                        return false
                    }

                    override fun onResourceReady(
                        resource: android.graphics.drawable.Drawable,
                        model: Any,
                        target: Target<android.graphics.drawable.Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.d(
                            PerformanceConfig.LOG_TAG,
                            "detail image ready mode=$transitionMode elapsed=${SystemClock.elapsedRealtime() - clickAt}ms source=$dataSource first=$isFirstResource"
                        )
                        return false
                    }
                })
                .into(imageView)
        }
    }
}

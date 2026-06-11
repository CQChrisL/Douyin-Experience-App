package com.example.douyinexperiencesection

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import okhttp3.OkHttpClient
import java.io.InputStream
import java.util.concurrent.TimeUnit

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // 核心解答：将 Glide 默认的网络拉取器，强制替换为我们整个 App 共享的 NetworkManager.sharedClient
        // 通过 newBuilder() 浅拷贝核心引擎（共享连接池与线程池）
        // 隔离业务层与媒体层的配置，为图片加载单独设置更长的超时时间
        val glideClient = NetworkManager.sharedClient.newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            // 不添加业务层的 Auth Token 拦截器
            .build()
            
        registry.replace(
            GlideUrl::class.java,
            InputStream::class.java,
            OkHttpUrlLoader.Factory(glideClient)
        )
    }
}

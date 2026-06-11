package com.example.douyinexperiencesection

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * 全局网络请求管理器
 * 保证在整个 App 生命周期内仅存在一个 OkHttpClient 实例，
 * 从而实现底层的线程池（Dispatcher）和连接池（ConnectionPool）的最大化复用。
 */
object NetworkManager {
    
    val sharedClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }
}

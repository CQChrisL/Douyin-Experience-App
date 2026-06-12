package com.example.douyinexperiencesection

import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * 全局网络请求管理器
 * 保证在整个 App 生命周期内仅存在一个 OkHttpClient 实例，
 * 实现底层的线程池（Dispatcher）和连接池（ConnectionPool）的最大化复用。
 */
object NetworkManager {
    
    val sharedClient: OkHttpClient by lazy {
        val dispatcher = Dispatcher().apply {
            maxRequests = 12
            maxRequestsPerHost = 6
        }

        OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }
}

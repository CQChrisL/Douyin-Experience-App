package com.example.douyinexperiencesection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.LruCache
import android.widget.ImageView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.MessageDigest

/**
 * 负责图片加载与三级缓存的单例工具类
 */
object ImageLoader {
    private val client = OkHttpClient()
    private val uiHandler = Handler(Looper.getMainLooper())

    // L1: 内存缓存
    private val memoryCache: LruCache<String, Bitmap>

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8

        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }

    // 将 URL 进行 MD5 哈希作为磁盘文件的名字
    private fun hashKeyForDisk(key: String): String {
        return try {
            val mDigest = MessageDigest.getInstance("MD5")
            mDigest.update(key.toByteArray())
            val bytes = mDigest.digest()
            val sb = StringBuilder()
            for (i in bytes.indices) {
                val hex = Integer.toHexString(0xFF and bytes[i].toInt())
                if (hex.length == 1) {
                    sb.append('0')
                }
                sb.append(hex)
            }
            sb.toString()
        } catch (e: Exception) {
            key.hashCode().toString()
        }
    }

    /**
     * 核心加载逻辑：整合 L1(内存) -> L2(磁盘) -> L3(网络)
     */
    fun loadImage(context: Context, url: String, imageView: ImageView) {
        imageView.tag = url

        // 第一层：查 L1 内存缓存（速度最快，微秒级）
        val memBitmap = memoryCache.get(url)
        if (memBitmap != null) {
            imageView.setImageBitmap(memBitmap)
            return
        }

        // 第二层：查 L2 磁盘缓存（速度中等，毫秒级）
        val diskCacheFile = File(context.cacheDir, hashKeyForDisk(url))
        if (diskCacheFile.exists()) {
            val diskBitmap = BitmapFactory.decodeFile(diskCacheFile.absolutePath)
            if (diskBitmap != null) {
                imageView.setImageBitmap(diskBitmap)
                memoryCache.put(url, diskBitmap)
                return
            }
        }

        // 第三层：查 L3 网络拉取（速度最慢，耗费流量）
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val inputStream = response.body?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    
                    if (bitmap != null) {
                        memoryCache.put(url, bitmap)

                        try {
                            val fos = FileOutputStream(diskCacheFile)
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                            fos.flush()
                            fos.close()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        uiHandler.post {
                            if (imageView.tag == url) {
                                imageView.setImageBitmap(bitmap)
                            }
                        }
                    }
                }
            }
        })
    }
}

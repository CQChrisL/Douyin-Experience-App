package com.example.douyinexperiencesection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mockDataList = mutableListOf<FeedItem>()

        for (i in 1..200) {
            val url = "https://picsum.photos/500/500?random=$i"
            mockDataList.add(FeedItem(i, "这是第 $i 条抖音分享内容，欢迎学习 Android", url))
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = FeedAdapter(mockDataList)
    }
}
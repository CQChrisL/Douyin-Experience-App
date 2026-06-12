package com.example.douyinexperiencesection

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class FeedFragment : Fragment() {

    private val excludedImageIds = setOf("111", "112", "131")   // 测试需要，图片解码异常，过滤
    private lateinit var feedDataList: MutableList<FeedItem>
    private lateinit var adapter: FeedAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var categoryName: String = "经验"
    private var hasLoadedData = false
    private var currentCall: Call? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        categoryName = arguments?.getString("EXTRA_CATEGORY") ?: "经验"
        feedDataList = mutableListOf()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        
        adapter = FeedAdapter(feedDataList)
        recyclerView.adapter = adapter

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            hasLoadedData = true
            fetchRealData(categoryName, swipeRefreshLayout)
        }
    }

    override fun onResume() {
        super.onResume()
        if (!hasLoadedData) {
            hasLoadedData = true
            fetchRealData(categoryName, swipeRefreshLayout)
        }
    }

    override fun onDestroyView() {
        currentCall?.cancel()
        currentCall = null
        super.onDestroyView()
    }

    private fun fetchRealData(categoryName: String, swipeRefreshLayout: SwipeRefreshLayout) {
        swipeRefreshLayout.isRefreshing = true

        val albumId = when(categoryName) {
            "推荐" -> 1
            "经验" -> 2
            "同城" -> 3
            else -> 4
        }
        
        val url = "https://picsum.photos/v2/list?page=$albumId&limit=50"

        val request = Request.Builder().url(url).build()

        currentCall?.cancel()
        currentCall = NetworkManager.sharedClient.newCall(request)
        currentCall?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (call.isCanceled()) return
                Handler(Looper.getMainLooper()).post {
                    if (!isAdded || view == null) return@post
                    swipeRefreshLayout.isRefreshing = false
                    context?.let {
                        Toast.makeText(it, "网络请求失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { safeResponse ->
                    val jsonString = safeResponse.body?.string() ?: return
                    try {
                        val jsonArray = JSONArray(jsonString)
                        val newList = mutableListOf<FeedItem>()
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val idStr = jsonObject.getString("id")
                            if (idStr in excludedImageIds) continue
                            val id = idStr.toIntOrNull() ?: i
                            
                            val author = jsonObject.getString("author")
                            val title = "[$categoryName] 摄影师: $author"
                            val thumbnailUrl = "https://picsum.photos/id/$idStr/500/500.jpg"
                            val detailUrl = "https://picsum.photos/id/$idStr/900/1200.jpg"
                            newList.add(FeedItem(id, title, thumbnailUrl, detailUrl))
                        }
                        
                        Handler(Looper.getMainLooper()).post {
                            if (!isAdded || view == null) return@post
                            swipeRefreshLayout.isRefreshing = false
                            feedDataList.clear()
                            feedDataList.addAll(newList)
                            // 打乱顺序，模拟每次下拉刷新的数据流变动
                            feedDataList.shuffle()
                            adapter.notifyDataSetChanged()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Handler(Looper.getMainLooper()).post {
                            if (!isAdded || view == null) return@post
                            swipeRefreshLayout.isRefreshing = false
                        }
                    }
                }
            }
        })
    }

    companion object {
        fun newInstance(category: String): FeedFragment {
            val fragment = FeedFragment()
            val args = Bundle()
            args.putString("EXTRA_CATEGORY", category)
            fragment.arguments = args
            return fragment
        }
    }
}

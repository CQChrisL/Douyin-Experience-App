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

    private lateinit var feedDataList: MutableList<FeedItem>
    private lateinit var adapter: FeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryName = arguments?.getString("EXTRA_CATEGORY") ?: "经验"
        feedDataList = mutableListOf()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        
        adapter = FeedAdapter(feedDataList)
        recyclerView.adapter = adapter

        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        
        // 首次启动，触发远端真实数据拉取
        fetchRealData(categoryName, swipeRefreshLayout)

        swipeRefreshLayout.setOnRefreshListener {
            fetchRealData(categoryName, swipeRefreshLayout)
        }
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

        NetworkManager.sharedClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(Looper.getMainLooper()).post {
                    swipeRefreshLayout.isRefreshing = false
                    context?.let {
                        Toast.makeText(it, "网络请求失败", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonString ->
                    try {
                        val jsonArray = JSONArray(jsonString)
                        val newList = mutableListOf<FeedItem>()
                        for (i in 0 until jsonArray.length()) {
                            val jsonObject = jsonArray.getJSONObject(i)
                            val idStr = jsonObject.getString("id")
                            val id = idStr.toIntOrNull() ?: i
                            
                            val author = jsonObject.getString("author")
                            val title = "[$categoryName] 摄影师: $author"
                            val imageUrl = "https://picsum.photos/id/$idStr/500/500"
                            newList.add(FeedItem(id, title, imageUrl))
                        }
                        
                        Handler(Looper.getMainLooper()).post {
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

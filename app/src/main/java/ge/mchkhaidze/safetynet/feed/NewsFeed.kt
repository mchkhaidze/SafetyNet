package ge.mchkhaidze.safetynet.feed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import ge.mchkhaidze.safetynet.R

class NewsFeed : AppCompatActivity() {
    private var adapter = NewsFeedAdapter(arrayListOf(NewsFeedItem(1, "23423", "2454365")))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_feed)

        setUpRV()
    }

    private fun setUpRV(){
        val recyclerView = findViewById<RecyclerView>(R.id.news_feed_recycler_view)
        recyclerView.adapter = adapter
    }
}
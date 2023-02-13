package ge.mchkhaidze.safetynet.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import ge.mchkhaidze.safetynet.ErrorHandler
import ge.mchkhaidze.safetynet.R
import ge.mchkhaidze.safetynet.Utils.Companion.showWarning
import ge.mchkhaidze.safetynet.Utils.Companion.startLoader
import ge.mchkhaidze.safetynet.Utils.Companion.stopLoader
import ge.mchkhaidze.safetynet.adapter.NewsFeedAdapter
import ge.mchkhaidze.safetynet.model.NewsFeedItem
import ge.mchkhaidze.safetynet.service.NewsFeedService

class NewsFeedActivity : BaseActivity(), ErrorHandler {

    private val emergencyNumber = "tel:112"
    private val feedManager = NewsFeedService()
    private var adapter = NewsFeedAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_feed)

        setUpRV()
        setUpNavBar()
        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_bar_menu, menu)
        return true
    }

    private fun setUpRV() {
        val recyclerView = findViewById<RecyclerView>(R.id.news_feed_recycler_view)
        recyclerView.adapter = adapter
    }

    private fun setUpNavBar() {
        NavigationBarView.OnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
//                    NavigationService.loadPage(this@NewsFeed, SignUpActivity::class.java)
                    true
                }
                R.id.map -> {
                    true
                }
                else -> false
            }
        }

        findViewById<FloatingActionButton>(R.id.call_button).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse(emergencyNumber))
            ContextCompat.startActivity(this, intent, null)
        }
    }

    private fun loadData() {
        startLoader()
        feedManager.lazyLoadPosts(this::updateUserList, this::handleError)
//        adapter.notifyItemRangeInserted(0, 5)
    }

    private fun updateUserList(
        uid: String?,
        userImage: String?,
        username: String?,
        image: String?,
        description: String?,
        date: String?
    ): Boolean {
        stopLoader()

        if (uid != null) {
            adapter.list.add(NewsFeedItem("userImage!!", username!!, image, description, date!!))
            adapter.notifyItemInserted(0)
        } else if (adapter.list.isEmpty()) {
            showWarning(getString(R.string.no_data), findViewById(R.id.call_button))
        }

        return true
    }

    override fun handleError(err: String): Boolean {
        showWarning(err, findViewById(R.id.call_button))
        return true
    }
}
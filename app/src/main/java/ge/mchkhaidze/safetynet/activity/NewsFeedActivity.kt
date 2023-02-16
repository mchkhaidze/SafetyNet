package ge.mchkhaidze.safetynet.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import android.view.Menu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import ge.mchkhaidze.safetynet.ErrorHandler
import ge.mchkhaidze.safetynet.NewPostFragment
import ge.mchkhaidze.safetynet.R
import ge.mchkhaidze.safetynet.Utils.Companion.showWarning
import ge.mchkhaidze.safetynet.Utils.Companion.startLoader
import ge.mchkhaidze.safetynet.Utils.Companion.stopLoader
import ge.mchkhaidze.safetynet.adapter.NewsFeedAdapter
import ge.mchkhaidze.safetynet.model.NewsFeedItem
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.UID
import ge.mchkhaidze.safetynet.model.UserInfo.Companion.USERNAME
import ge.mchkhaidze.safetynet.service.NavigationService
import ge.mchkhaidze.safetynet.service.NewsFeedService

class NewsFeedActivity : BaseActivity(), ErrorHandler {

    private val emergencyNumber = "tel:112"
    private val feedManager = NewsFeedService()
    private var adapter = NewsFeedAdapter(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_feed)

        window.exitTransition = Slide(Gravity.BOTTOM)

        setUpRV()
        setUpNavBar()
        loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_bar_menu, menu)
        return true
    }

    private fun setUpRV() {

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            loadData()
            swipeRefreshLayout.isRefreshing = false
        }

        val recyclerView = findViewById<RecyclerView>(R.id.news_feed_recycler_view)
        recyclerView.adapter = adapter
    }

    private fun setUpNavBar() {
        val nav: BottomNavigationView = findViewById(R.id.navigation)
        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    true
                }
                R.id.map -> {
                    NavigationService.loadPage(this, MapsActivity::class.java)
                    true
                }
                R.id.add_post -> {
                    val bottomSheetFragment = NewPostFragment()
                    bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
                    true
                }
                R.id.profile -> {
                    val extras = mapOf(
                        Pair(USERNAME, "mariam"),
                        Pair(UID, FirebaseAuth.getInstance().uid.toString())
                    ) //todo set correct username
                    NavigationService.loadPage(this, ProfileActivity::class.java, extras)
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
        feedManager.loadAllPosts(this::updatePostList, this::handleError)
    }

    private fun updatePostList(posts: ArrayList<NewsFeedItem>?): Boolean {
        stopLoader()

        if (posts == null) {
            showWarning(getString(R.string.no_data), findViewById(R.id.call_button))
        } else {
            adapter.list = posts
            adapter.notifyDataSetChanged()
        }

        return true
    }

    override fun handleError(err: String): Boolean {
        showWarning(err, findViewById(R.id.call_button))
        return true
    }
}
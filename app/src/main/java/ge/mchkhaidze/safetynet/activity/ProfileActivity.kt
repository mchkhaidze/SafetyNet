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
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import ge.mchkhaidze.safetynet.ErrorHandler
import ge.mchkhaidze.safetynet.NewPostFragment
import ge.mchkhaidze.safetynet.R
import ge.mchkhaidze.safetynet.Utils
import ge.mchkhaidze.safetynet.Utils.Companion.startLoader
import ge.mchkhaidze.safetynet.Utils.Companion.stopLoader
import ge.mchkhaidze.safetynet.adapter.NewsFeedAdapter
import ge.mchkhaidze.safetynet.model.NewsFeedItem
import ge.mchkhaidze.safetynet.model.NewsFeedItem.Companion.UID
import ge.mchkhaidze.safetynet.model.UserInfo.Companion.USERNAME
import ge.mchkhaidze.safetynet.service.NavigationService
import ge.mchkhaidze.safetynet.service.NewsFeedService

class ProfileActivity : BaseActivity(), ErrorHandler {

    private val emergencyNumber = "tel:112"
    private val feedManager = NewsFeedService()
    private var adapter = NewsFeedAdapter(this)
    private lateinit var uid: String
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        window.exitTransition = Slide(Gravity.BOTTOM)

        setUpToolBar()
        setUpRV()
        setUpNavBar()
        loadData()
    }

    private fun setUpToolBar() {
        toolbar = findViewById(R.id.toolbar_user)
        val extras = intent.extras
        if (extras != null) {
            uid = extras.getString(UID).toString()
            toolbar.title = extras.getString(USERNAME)
        }

        if (uid == FirebaseAuth.getInstance().uid.toString()) {

            toolbar.menu.findItem(R.id.edit).isVisible = true
            toolbar.menu.findItem(R.id.log_out).isVisible = true

            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.edit -> {
                        NavigationService.loadPage(this, SettingsActivity::class.java)
                        true
                    }
                    R.id.log_out -> {
                        FirebaseAuth.getInstance().signOut()
                        NavigationService.loadPage(this, SignInActivity::class.java)
                        true
                    }
                    else -> false
                }
            }
        } else {
            toolbar.menu.getItem(R.id.edit).isVisible = false
            toolbar.menu.getItem(R.id.log_out).isVisible = false
        }
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
                    NavigationService.loadPage(this, NewsFeedActivity::class.java)
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
        feedManager.loadUserSpecificPosts(uid, this::updatePostList, this::handleError)
    }

    private fun updatePostList(posts: ArrayList<NewsFeedItem>?): Boolean {
        stopLoader()

        if (posts == null) {
            Utils.showWarning(getString(R.string.no_data), findViewById(R.id.call_button))
        } else {
            adapter.list = posts
            adapter.notifyDataSetChanged()
        }

        return true
    }

    override fun handleError(err: String): Boolean {
        Utils.showWarning(err, findViewById(R.id.call_button))
        return true
    }
}
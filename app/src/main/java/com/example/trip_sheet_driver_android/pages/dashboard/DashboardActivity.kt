package com.example.trip_sheet_driver_android.pages.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.trip_sheet_driver_android.R
import com.example.trip_sheet_driver_android.pages.trip.AddTripActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        toolbar = findViewById(R.id.toolbar)
        bottomNav = findViewById(R.id.bottomNav)

        ViewCompat.setOnApplyWindowInsetsListener(toolbar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                systemBars.top,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setTitleTextColor(getColor(android.R.color.white))
        loadFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener {

            toolbar.menu.clear()

            when (it.itemId) {

                R.id.menu_home -> {
                    toolbar.inflateMenu(R.menu.top_app_bar_menu)
                    loadFragment(HomeFragment())
                }

                R.id.menu_trips -> {
                    toolbar.inflateMenu(R.menu.top_app_bar_menu)
                    loadFragment(TripsFragment())
                }

                R.id.menu_profile -> {
                    toolbar.inflateMenu(R.menu.profile_menu)
                    loadFragment(ProfileFragment())
                }
            }
            true
        }

        toolbar.setOnMenuItemClickListener {

            when (it.itemId) {

                R.id.action_add -> {
                    startActivity(Intent(this, AddTripActivity::class.java))
                    true
                }

                R.id.action_logout -> {
                    // logout logic here
                    true
                }

                else -> false
            }
        }

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        toggle.drawerArrowDrawable.color = getColor(android.R.color.white)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                startActivity(Intent(this, AddTripActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
package com.example.foodie.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foodie.R
import com.example.foodie.fragment.*
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    lateinit var custom_toolbar: Toolbar
    lateinit var drawerLayout: DrawerLayout
    lateinit var Navigationview: NavigationView
    lateinit var Name: TextView

    val previous: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.foodie.R.layout.activity_main)

        val sharedPref =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)


        custom_toolbar = findViewById(R.id.appbar)
        drawerLayout = findViewById(R.id.drawerLayout)
        Navigationview = findViewById(R.id.NavigationView)

        // Inflate the header view at runtime
        val headerLayout: View = Navigationview.getHeaderView(0)
        // We can now look up items within the header if needed
        Name = headerLayout.findViewById(R.id.txtName)

        Name.text = intent.getStringExtra("name")

        setSupportActionBar(custom_toolbar)
        supportActionBar?.title = "Foodie"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        openHome()

        //hamburger icon and its functionality
        val drawerToggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            com.example.foodie.R.string.open,
            com.example.foodie.R.string.close
        )

        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        //setting click listener to navigation view
        Navigationview.setNavigationItemSelectedListener {
            if (previous != null) {
                previous?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            when (it.itemId) {
                com.example.foodie.R.id.Home -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                com.example.foodie.R.id.MyProfile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(com.example.foodie.R.id.frame, MyProfile())
                        .addToBackStack("Profile").commit()
                    supportActionBar?.title = "My Profile"
                    drawerLayout.closeDrawers()
                }
                com.example.foodie.R.id.Fav_Restaurant -> {
                    supportFragmentManager.beginTransaction()
                        .replace(com.example.foodie.R.id.frame, Fav_Restaurant())
                        .addToBackStack("FavRestaurant").commit()
                    supportActionBar?.title = "Favourite Restaurant "
                    drawerLayout.closeDrawers()
                }
                com.example.foodie.R.id.OrderHistory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(com.example.foodie.R.id.frame, OrderHistory())
                        .addToBackStack("OrderHistory").commit()
                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }
                com.example.foodie.R.id.Faqs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(com.example.foodie.R.id.frame, FAQS()).addToBackStack("Faqs")
                        .commit()
                    supportActionBar?.title = "Frequently Asked Questions "
                    drawerLayout.closeDrawers()
                }
                com.example.foodie.R.id.LOG_OUT -> {
                    val dialog = AlertDialog.Builder(this)
                    val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                    alterDialog.setMessage("Do you wish to log out?")
                    alterDialog.setPositiveButton("Yes") { _, _ ->
                        sharedPref.edit().putBoolean("isLoggedIn", false).apply()
                        val intent = Intent(this, Login::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this, "You have successfully Logged out", Toast.LENGTH_SHORT)
                            .show()
                    }
                    alterDialog.setNegativeButton("No") { _, _ ->

                    }
                    alterDialog.create()
                    alterDialog.show()

                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //to open the drawer from start
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openHome() {
        //to open home fragment
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = Home()
        transaction.replace(com.example.foodie.R.id.frame, fragment)
        supportActionBar?.title = "Restaurants"
        transaction.commit()
        Navigationview.setCheckedItem(com.example.foodie.R.id.Home)
    }

    //    override fun onBackPressed() {
//        if(supportFragmentManager.backStackEntryCount>1){
//            super.onBackPressed()
//        }
//        else{
//            val dialog = AlertDialog.Builder(this)
//            dialog.setTitle("Exit the app")
//            dialog.setMessage("Are you sure to exit the App")
//            dialog.setPositiveButton("yes"){text,listener->
//                null
//            }
//            dialog.setNegativeButton("Exit"){text,listener->
//                ActivityCompat.finishAffinity(this)
//            }
//        }
//    }
    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)

        when (frag) {
            !is Home -> openHome()
            else -> super.onBackPressed()
        }
    }

}
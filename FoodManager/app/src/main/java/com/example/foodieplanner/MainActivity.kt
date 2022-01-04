package com.example.foodieplanner

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration : AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // toolbar
        //val toolbar = findViewById<Toolbar>(R.id.toolbar)
        //setSupportActionBar(toolbar)

        // nav host
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment? ?: return

        // nav controller
        val navController = host.navController

        //val set: Set<Int> = setOf(R.id.homeFragment,R.id.plannerFragment,R.id.savedMealsFragment)
        //appBarConfiguration = AppBarConfiguration(set,null)


        // setup
        //setupActionBar(navController, appBarConfiguration)
        setupBottomNavMenu(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val dest: String = try {
                resources.getResourceName(destination.id)
            } catch (e: Resources.NotFoundException) {
                Integer.toString(destination.id)
            }
            Log.d("NavigationActivity", "Navigated to $dest")
        }


    }

    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNav?.setupWithNavController(navController)
    }

    private fun setupNavigationMenu(navController: NavController) {
        // TODO STEP 9.4 - Use NavigationUI to set up a Navigation View
//        // In split screen mode, you can drag this view out from the left
//        // This does NOT modify the actionbar
//        val sideNavView = findViewById<NavigationView>(R.id.nav_view)
//        sideNavView?.setupWithNavController(navController)
        // TODO END STEP 9.4
    }

    private fun setupActionBar(navController: NavController,
                               appBarConfig : AppBarConfiguration) {
        // This allows NavigationUI to decide what label to show in the action bar
        // By using appBarConfig, it will also determine whether to
        // show the up arrow or drawer menu icon
        setupActionBarWithNavController(navController, appBarConfig)
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val retValue = super.onCreateOptionsMenu(menu)
////        val navigationView = findViewById<NavigationView>(R.id.nav_view)
//        // The NavigationView already has these same navigation items, so we only add
//        // navigation items to the menu here if there isn't a NavigationView
//
////        if (navigationView == null) {
//        menuInflater.inflate(R.menu.main_menu, menu)
//        return true
////        }
////        return retValue
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
////        // Have the NavigationUI look for an action or destination matching the menu
////        // item id and navigate there if found.
////        // Otherwise, bubble up to the parent.
//        return item.onNavDestinationSelected(findNavController(R.id.nav_host_fragment))
//                || super.onOptionsItemSelected(item)
//    }

}



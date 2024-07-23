package com.ashmit.notes

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.ashmit.notes.Firebase.FirebaseAuthHelper
import com.ashmit.notes.Helper.DialogBox
import com.ashmit.notes.NavigationDrawer.DeleteAccFragment
import com.ashmit.notes.NavigationDrawer.HomeFragment
import com.ashmit.notes.NavigationDrawer.UpdateEmailFragment
import com.ashmit.notes.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    lateinit var navigationView : NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //Creating a nav drawer
        val drawerLayout = binding.drawerLayout
        val toolBar = binding.toolbar
        setSupportActionBar(toolBar)
        navigationView = binding.navView
        navigationView.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolBar,
            R.string.openNavigationDrawer,
            R.string.closeNavigationDrawer
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment() , false)
            navigationView.setCheckedItem(R.id.nav_home)
        }

        val headerView = navigationView.getHeaderView(0)
        val navName = headerView.findViewById<TextView>(R.id.navHeaderName)
        val navEmail = headerView.findViewById<TextView>(R.id.navHeaderEmail)
        val navImage = headerView.findViewById<ImageView>(R.id.navHeaderImg)
        FirebaseAuthHelper(this).displayUserInfo(navName, navEmail, navImage)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //checking if the navigation drawer is open and the user has pressed back btn then it will close the nav drawer or else it will check if the stack has frag then it will pop else it will show the exit dialog
                if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                }else{
                    val fragmentManager = supportFragmentManager
                    if (fragmentManager.backStackEntryCount >= 1) {
                        fragmentManager.popBackStack()
                    } else {
                        DialogBox(this@MainActivity).showDialogBox("Exit", "Are you sure you want to exit?") {
                            finish()
                        }
                    }
                }

            }
        })

    }
    //toggle bar
    private fun replaceFragment (fragment: Fragment , boolean: Boolean){
        //as we have to on add the first frag that is the home frag to the back stack so we have taken ab ool var an passed false if for the home screen frag and true for all other frag as we ha ve to add them to the backstack
        val fragManager = supportFragmentManager.beginTransaction().replace(R.id.fragment_Container , fragment)
        if(boolean){
            fragManager.addToBackStack(null)
        }
        fragManager.commit()
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
//            binding.drawerLayout.closeDrawer(GravityCompat.START)
//        }
//    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_home -> {
                replaceFragment(HomeFragment() , true)
                navigationView.setCheckedItem(R.id.nav_home)
            }
            R.id.nav_logOut -> {
                DialogBox(this).showDialogBox("Logout" , "Are you sure u want to Logout ?" ){
                    FirebaseAuthHelper(this).signOut {
                        startActivity(Intent(this , LoginScreen::class.java))
                        finish()
                    }
                }
            }
            R.id.nav_deleteAcc -> {
                replaceFragment(DeleteAccFragment() , true)
                navigationView.setCheckedItem(R.id.nav_deleteAcc)
            }
            R.id.nav_updateEmail -> {
                replaceFragment(UpdateEmailFragment() , true)
                navigationView.setCheckedItem(R.id.nav_updateEmail)
            }
            R.id.exit -> {
                DialogBox(this).showDialogBox("Exit" , "Are you sure u want to Exit ?") {
                    finish()
                }
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}
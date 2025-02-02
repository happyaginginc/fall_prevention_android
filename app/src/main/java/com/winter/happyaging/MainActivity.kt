package com.winter.happyaging

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.winter.happyaging.login.FirstFragment
import com.winter.happyaging.login.LoginFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#B8660E")
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            showFragment(FirstFragment())
        }

        val loginLink: TextView = findViewById(R.id.loginLink)

        loginLink.setOnClickListener {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, LoginFragment())
                addToBackStack(null)
            }
        }
    }

    fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}

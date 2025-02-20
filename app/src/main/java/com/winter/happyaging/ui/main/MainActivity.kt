package com.winter.happyaging.ui.main

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.winter.happyaging.R
import com.winter.happyaging.ui.auth.IntroFragment
import com.winter.happyaging.ui.auth.login.LoginFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, IntroFragment())
                .commit()
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

package com.winter.happyaging

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.winter.happyaging.auth.ui.IntroFragment
import com.winter.happyaging.auth.ui.login.LoginFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#B8660E")
//        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            showFragment(IntroFragment())
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

package com.winter.happyaging.home

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.winter.happyaging.AIFirstFragment
import com.winter.happyaging.AIFourthFragment
import com.winter.happyaging.AISecondFragment
import com.winter.happyaging.AIThirdFragment
import com.winter.happyaging.R
import com.winter.happyaging.databinding.ActivityAiAnalysisBinding
import com.winter.happyaging.databinding.ActivityMainBinding

class MyFragmentPagerAdapter (activity: FragmentActivity): FragmentStateAdapter(activity){
    val fragments:List<Fragment>
    init{
        fragments= listOf(AIFirstFragment(), AISecondFragment(), AIThirdFragment(),
            AIFourthFragment()
        )
    }
    override fun getItemCount(): Int=fragments.size

    override fun createFragment(position: Int): Fragment =fragments[position]
}

class AIAnalysisActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding= ActivityAiAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewpager.adapter = MyFragmentPagerAdapter(this)

    }
}
package com.winter.happyaging

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.winter.happyaging.databinding.FragmentAiFirstBinding
import com.winter.happyaging.home.RoomAdapter
import com.winter.happyaging.home.RoomData

class AIFirstFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        val binding = FragmentAiFirstBinding.inflate(inflater, container, false)
        return binding.root
    }
}
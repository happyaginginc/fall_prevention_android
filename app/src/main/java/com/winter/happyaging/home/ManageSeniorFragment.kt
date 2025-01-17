package com.winter.happyaging.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.winter.happyaging.R

class ManageSeniorFragment : Fragment() {

    private var name: String? = null
    private var info: String? = null
    private var grade: Int? = null

    companion object {
        fun newInstance(name: String, info: String, grade: Int): ManageSeniorFragment {
            val fragment = ManageSeniorFragment()
            val args = Bundle().apply {
                putString("name", name)
                putString("info", info)
                putInt("grade", grade)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString("name")
            info = it.getString("info")
            grade = it.getInt("grade")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_manage_senior, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("HomeActivity", "관리하기 버튼 클릭됨")
        // 데이터 못 받아옴
        view.findViewById<TextView>(R.id.tvSeniorName).text = name ?: "이름 없음"
        view.findViewById<TextView>(R.id.tvSeniorInfo).text = info ?: "정보 없음"
    }
}


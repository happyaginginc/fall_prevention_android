package com.winter.happyaging.home

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.winter.happyaging.R

class ManageSeniorFragment : Fragment() {

    private var name: String? = null
    private var info: String? = null
    private var grade: Int? = null

    companion object {
        private const val ARG_NAME = "arg_name"
        private const val ARG_INFO = "arg_info"
        private const val ARG_GRADE = "arg_grade"

        fun newInstance(name: String, info: String, grade: Int): ManageSeniorFragment {
            val fragment = ManageSeniorFragment()
            val args = Bundle().apply {
                putString(ARG_NAME, name)
                putString(ARG_INFO, info)
                putInt(ARG_GRADE, grade)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(ARG_NAME)
            info = it.getString(ARG_INFO)
            grade = it.getInt(ARG_GRADE, 1)
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

        // 예: 시니어 이름/정보/등급을 표시
        val tvSeniorName = view.findViewById<TextView>(R.id.tvSeniorName)
        val tvSeniorInfo = view.findViewById<TextView>(R.id.tvSeniorInfo)

        tvSeniorName.text = name ?: "이름 없음"
        tvSeniorInfo.text = info ?: "정보 없음"

        // etc... 1등급이면 뷰를 어떻게 표시 etc.
    }
}

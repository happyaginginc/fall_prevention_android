package com.winter.happyaging.ui.home.more

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.winter.happyaging.R
import com.winter.happyaging.network.TokenManager
import com.winter.happyaging.network.UserProfileManager
import com.winter.happyaging.ui.main.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoreFragment : Fragment() {

    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserPhone: TextView
    private lateinit var logoutLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_more, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val headerTitle: TextView = view.findViewById(R.id.tvHeader)
        headerTitle.text = "더보기"

        view.findViewById<View>(R.id.btnBack)?.visibility = View.GONE

        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        tvUserPhone = view.findViewById(R.id.tvUserPhone)

        val context = requireContext()
        val name = UserProfileManager.getUserName(context)
        val email = UserProfileManager.getUserEmail(context)
        val phone = UserProfileManager.getUserPhone(context)

        tvUserName.text = name
        tvUserEmail.text = email
        tvUserPhone.text = phone

        logoutLayout = view.findViewById(R.id.menuLogout)
        logoutLayout.setOnClickListener {
            lifecycleScope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        TokenManager(requireContext()).clearTokens()  // 토큰 삭제
                    }
                    UserProfileManager.clearUserInfo(requireContext())  // 사용자 정보 삭제

                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    requireActivity().finish()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "로그아웃 중 오류 발생: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
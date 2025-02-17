package com.winter.happyaging.ui.auth.findAccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.winter.happyaging.R

/**
 * 이메일/비번 찾기 성공/실패 결과를 표시하는 다이얼로그
 */
class FindAccountDialog : DialogFragment() {

    private var title: String? = null
    private var message: String? = null
    private var success: Boolean = false

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_MESSAGE = "arg_message"
        private const val ARG_SUCCESS = "arg_success"

        fun newInstance(title: String, message: String, success: Boolean): FindAccountDialog {
            return FindAccountDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_MESSAGE, message)
                    putBoolean(ARG_SUCCESS, success)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ARG_TITLE)
            message = it.getString(ARG_MESSAGE)
            success = it.getBoolean(ARG_SUCCESS, false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_find_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnClose: ImageView = view.findViewById(R.id.btnDialogClose)
        val tvDialogTitle: TextView = view.findViewById(R.id.tvDialogTitle)
        val tvDialogMessage: TextView = view.findViewById(R.id.tvDialogMessage)
        val btnConfirm: Button = view.findViewById(R.id.btnDialogConfirm)

        tvDialogTitle.text = title
        tvDialogMessage.text = message

        btnClose.setOnClickListener { dismiss() }
        btnConfirm.setOnClickListener { dismiss() }
    }
}
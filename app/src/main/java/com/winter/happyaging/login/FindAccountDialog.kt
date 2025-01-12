package com.winter.happyaging.login

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.winter.happyaging.R

class FindAccountDialog : DialogFragment() {

    private var title: String? = null
    private var message: String? = null
    private var success: Boolean = false

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_MESSAGE = "arg_message"
        private const val ARG_SUCCESS = "arg_success"

        fun newInstance(title: String, message: String, success: Boolean): FindAccountDialog {
            val fragment = FindAccountDialog()
            val bundle = Bundle()
            bundle.putString(ARG_TITLE, title)
            bundle.putString(ARG_MESSAGE, message)
            bundle.putBoolean(ARG_SUCCESS, success)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 풀스크린/스타일 등을 설정하려면 여기에
        // setStyle(STYLE_NO_TITLE, R.style.CustomDialogTheme) 등
        arguments?.let {
            title = it.getString(ARG_TITLE)
            message = it.getString(ARG_MESSAGE)
            success = it.getBoolean(ARG_SUCCESS, false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Dialog 전용 레이아웃
        val view = inflater.inflate(R.layout.dialog_find_account, container, false)

        // 배경 투명, 라운드 처리 등 디자인은 layout/dialog_find_account.xml에서 꾸며주면 됩니다.
        val btnClose = view.findViewById<ImageView>(R.id.btnDialogClose)
        val tvDialogTitle = view.findViewById<TextView>(R.id.tvDialogTitle)
        val tvDialogMessage = view.findViewById<TextView>(R.id.tvDialogMessage)
        val btnConfirm = view.findViewById<Button>(R.id.btnDialogConfirm)

        tvDialogTitle.text = title
        tvDialogMessage.text = message

        // 아이콘 색상이나 체크/느낌표 등은 success 여부에 따라 다르게 할 수도 있음
        // ...

        btnClose.setOnClickListener {
            dismiss()
        }
        btnConfirm.setOnClickListener {
            dismiss()
        }

        return view
    }

    // DialogFragment가 배경을 둥글게 설정할 수 있게 맞춰주거나,
    // 테마 적용을 위해 onViewCreated에서 LayoutParams 조정해도 됩니다.
}

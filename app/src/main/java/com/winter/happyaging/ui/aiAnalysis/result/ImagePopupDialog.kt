package com.winter.happyaging.ui.aiAnalysis.result

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.winter.happyaging.R

class ImagePopupDialog(context: Context, imageUrl: String) : Dialog(context) {

    init {
        // 타이틀바 제거 및 투명 배경 설정
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_image_popup)

        // 이미지 로드
        val imageView = findViewById<ImageView>(R.id.popupImageView)
        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.logo)
            .error(R.drawable.logo)
            .into(imageView)

        // 배경 클릭 시 다이얼로그 닫기
        findViewById<android.view.View>(R.id.popupContainer)?.setOnClickListener {
            dismiss()
        }
    }
}
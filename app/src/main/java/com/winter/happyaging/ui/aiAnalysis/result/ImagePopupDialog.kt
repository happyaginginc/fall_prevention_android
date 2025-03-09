package com.winter.happyaging.ui.aiAnalysis.result

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.winter.happyaging.R

class ImagePopupDialog(context: Context, imageUrl: String) : Dialog(context) {

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_image_popup)
        val imageView = findViewById<ImageView>(R.id.popupImageView)
        try {
            Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(imageView)
        } catch (e: Exception) {
            imageView.setImageResource(R.drawable.logo)
            Toast.makeText(context, "이미지를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
        findViewById<View>(R.id.popupContainer)?.setOnClickListener { dismiss() }
    }
}
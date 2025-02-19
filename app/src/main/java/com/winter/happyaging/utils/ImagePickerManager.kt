package com.winter.happyaging.utils

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class ImagePickerManager(private val fragment: Fragment) {

    interface ImagePickerCallback {
        fun onImagePicked(uri: Uri)
        fun onError(message: String)
    }

    private var callback: ImagePickerCallback? = null

    fun setCallback(callback: ImagePickerCallback) {
        this.callback = callback
    }

    // Android 14 이상용 Photo Picker
    private val galleryLauncher: ActivityResultLauncher<PickVisualMediaRequest> =
        fragment.registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                callback?.onImagePicked(uri)
            } else {
                callback?.onError("이미지가 선택되지 않았습니다.")
            }
        }

    // Android 13 이하 등 구버전용 이미지 선택 (GetContent)
    private val legacyGalleryLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                callback?.onImagePicked(uri)
            } else {
                callback?.onError("이미지 선택이 취소되었습니다.")
            }
        }

    /**
     * 갤러리 오픈. Android 14 이상이면 Photo Picker, 아니면 기존 방식 사용.
     */
    fun openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            legacyGalleryLauncher.launch("image/*")
        }
    }

    /**
     * Android 14 미만에서 권한 체크가 필요한 경우
     */
    fun checkGalleryPermission(permission: String, permissionLauncher: ActivityResultLauncher<String>) {
        if (ContextCompat.checkSelfPermission(fragment.requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            permissionLauncher.launch(permission)
        }
    }
}
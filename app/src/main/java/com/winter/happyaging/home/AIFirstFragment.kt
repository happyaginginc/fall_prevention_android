package com.winter.happyaging

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException


class AIFirstFragment : Fragment() {

    private lateinit var currentPhotoPath: String
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ai_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.btnCamera1).setOnClickListener {
            dispatchTakePictureIntent(view.findViewById(R.id.preview1))
        }

        view.findViewById<ImageView>(R.id.btnCamera2).setOnClickListener {
            dispatchTakePictureIntent(view.findViewById(R.id.preview2))
        }

        view.findViewById<ImageView>(R.id.btnCamera3).setOnClickListener {
            dispatchTakePictureIntent(view.findViewById(R.id.preview3))
        }
    }

    private fun dispatchTakePictureIntent(previewImageView: ImageView) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                Toast.makeText(requireContext(), "파일 생성 오류", Toast.LENGTH_SHORT).show()
                null
            }

            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir: File? = requireContext().getExternalFilesDir(null)
        return File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val file = File(currentPhotoPath)
            if (file.exists()) {
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, Uri.fromFile(file))
                view?.findViewById<ImageView>(R.id.preview1)?.setImageBitmap(bitmap)
            }
        }
    }


}

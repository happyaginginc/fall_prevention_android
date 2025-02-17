package com.winter.happyaging.ui.aiAnalysis

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.winter.happyaging.ImageManager
import com.winter.happyaging.R
import com.winter.happyaging.data.aiAnalysis.model.RoomData
import com.winter.happyaging.data.image.model.ImageResponse
import com.winter.happyaging.data.image.service.ImageService
import com.winter.happyaging.databinding.FragmentAiRoomBinding
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.ui.aiAnalysis.adapter.RoomAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

abstract class BaseRoomFragment(
    private val step: Int,
    private val roomType: String,
    private val nextAction: Int
) : Fragment(R.layout.fragment_ai_room) {

    private var _binding: FragmentAiRoomBinding? = null
    val binding get() = _binding!!
    lateinit var imageManager: ImageManager
    private lateinit var roomAdapter: RoomAdapter

    val roomList = mutableListOf(RoomData(name = "$roomType 1"))
    private var selectedRoomIndex: Int = 0
    private var selectedCameraNumber: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAiRoomBinding.bind(view)
        imageManager = ImageManager(requireContext())

        binding.tvStep.text = "$step/3"
        binding.tvTitle.text = roomType
        binding.tvSubtitle.text = when (roomType) {
            "화장실" -> "다음 조건에 맞게 화장실을 촬영해주세요"
            "일반 방" -> "다음 조건에 맞게 방을 촬영해주세요"
            "기타" -> "다음 조건에 맞게 촬영해주세요"
            else -> ""
        }

        setupRecyclerView()
        setupBackButton()
        setupBackPressedHandler()

        binding.nextButton.setOnClickListener {
            onNextButtonClick()
        }
    }

    private fun setupRecyclerView() {
        binding.roomRecyclerView.layoutManager = LinearLayoutManager(context)

        roomAdapter = RoomAdapter(
            roomList,
            onCameraClick = { position, cameraNumber ->
                selectedRoomIndex = position
                selectedCameraNumber = cameraNumber
                showImageSelectionDialog()
            },
            onAddRoomClick = {
                roomList.add(RoomData(name = "$roomType ${roomList.size + 1}"))
                roomAdapter.notifyItemInserted(roomList.size - 1)
            },
            onDeleteRoomClick = {
                roomList.removeAt(it)
                roomAdapter.notifyItemRemoved(it)
            }
        )

        binding.roomRecyclerView.adapter = roomAdapter
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setupBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().supportFragmentManager.popBackStack()
            }
        })
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                Log.d("BaseRoomFragment", "✅ 선택된 이미지 URI: $uri")
                uploadGalleryImageToServer(uri)
            } else {
                Log.e("BaseRoomFragment", "🚨 이미지 선택 안됨")
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Log.e("BaseRoomFragment", "🚨 갤러리 접근 권한 거부됨")
            }
        }

    private fun checkGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    private fun openGallery() {
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                imageBitmap?.let { uploadImageToServer(it) }
            }
        }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(cameraIntent)
    }

    private fun showImageSelectionDialog() {
        val options = arrayOf("카메라로 촬영", "갤러리에서 선택")
        AlertDialog.Builder(requireContext())
            .setTitle("이미지 선택")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> checkGalleryPermission()
                }
            }
            .show()
    }

    private fun uploadGalleryImageToServer(uri: Uri) {
        val context = requireContext()
        val storageService = RetrofitClient.getInstance(context).create(ImageService::class.java)

        // 파일을 InputStream 으로 변환 후 ByteArray로 읽어오기
        val inputStream = context.contentResolver.openInputStream(uri)
        val byteArray = inputStream?.readBytes()
        inputStream?.close()

        if (byteArray == null) {
            Log.e("BaseRoomFragment", "이미지 변환 실패")
            return
        }

        // MIME 타입 확인 (image/jpeg, image/png 등)
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        Log.d("BaseRoomFragment", "📌 MIME 타입: $mimeType")

        // RequestBody 변환
        val requestFile = RequestBody.create(mimeType.toMediaTypeOrNull(), byteArray)
        val imagePart = MultipartBody.Part.createFormData("image", "gallery_image.jpg", requestFile)

        Log.d("BaseRoomFragment", "📡 서버에 업로드 요청: $imagePart")

        storageService.uploadImages(imagePart).enqueue(object : Callback<ImageResponse> {
            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.data ?: ""
                    Log.d("BaseRoomFragment", "✅ 업로드 성공! 저장된 URL: $imageUrl")
                    saveImageToDataStore(imageUrl)
                } else {
                    Log.e("BaseRoomFragment", "🚨 서버 응답 실패: ${response.code()}")
                    Log.e("BaseRoomFragment", "🚨 응답 본문: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                Log.e("BaseRoomFragment", "🚨 네트워크 오류 발생: ${t.message}")
            }
        })
    }

    private fun saveImageToDataStore(imageUrl: String) {
        val roomName = roomList[selectedRoomIndex].name
        imageManager.saveImageData(roomName, selectedCameraNumber - 1, imageUrl)
        val imageUri = Uri.parse(imageUrl)

        roomList[selectedRoomIndex] = roomList[selectedRoomIndex].copy().apply {
            when (selectedCameraNumber) {
                1 -> roomList[selectedRoomIndex].imageUri1 = imageUri
                2 -> roomList[selectedRoomIndex].imageUri2 = imageUri
                3 -> roomList[selectedRoomIndex].imageUri3 = imageUri
            }
        }

        binding.roomRecyclerView.adapter?.notifyItemChanged(selectedRoomIndex)
    }

    private fun uploadImageToServer(bitmap: Bitmap) {
        val byteArray = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray.toByteArray())
        val imagePart = MultipartBody.Part.createFormData("image", "uploaded_image.jpg", requestFile)

        val storageService = RetrofitClient.getInstance(requireContext()).create(ImageService::class.java)
        storageService.uploadImages(imagePart).enqueue(object : Callback<ImageResponse> {
            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.data ?: ""
                    Log.d("BaseRoomFragment", "업로드 성공! 저장된 URL: $imageUrl")
                    saveImageToDataStore(imageUrl)
                }
            }

            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                Log.e("BaseRoomFragment", "네트워크 오류 발생: ${t.message}")
            }
        })
    }

    private fun onNextButtonClick() {
        findNavController().navigate(nextAction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
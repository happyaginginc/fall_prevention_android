package com.winter.happyaging.ui.aiAnalysis

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.winter.happyaging.R
import com.winter.happyaging.data.aiAnalysis.model.RoomData
import com.winter.happyaging.data.image.model.ImageResponse
import com.winter.happyaging.data.image.service.ImageService
import com.winter.happyaging.databinding.FragmentAiRoomBinding
import com.winter.happyaging.network.ImageManager
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.ui.aiAnalysis.adapter.RoomAdapter
import com.winter.happyaging.utils.ImagePickerManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseRoomFragment(
    private val step: Int,
    private val roomType: String,
    private val nextAction: Int
) : Fragment(R.layout.fragment_ai_room), ImagePickerManager.ImagePickerCallback {

    companion object {
        private const val TAG = "BaseRoomFragment"
        private const val GALLERY_IMAGE_FILE_NAME = "gallery_image.jpg"
    }

    private var _binding: FragmentAiRoomBinding? = null
    protected val binding get() = _binding!!

    protected lateinit var imageManager: ImageManager
    protected val roomList = mutableListOf(RoomData(name = "$roomType 1"))

    private var selectedRoomIndex = 0
    private var selectedGuideIndex = 0

    private lateinit var roomAdapter: RoomAdapter
    private lateinit var imagePickerManager: ImagePickerManager

    private var cameraImageUri: Uri? = null

    // 갤러리 권한 요청 및 결과 처리
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                imagePickerManager.openGallery()
            } else {
                Log.e(TAG, "갤러리 접근 권한 거부됨")
            }
        }

    // 카메라 촬영 결과 처리 (FileProvider로 저장된 이미지 사용)
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                cameraImageUri?.let { uploadGalleryImageToServer(it) }
            }
        }

    // 카메라 권한 요청
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "카메라 권한이 거부되었습니다.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAiRoomBinding.bind(view)
        imageManager = ImageManager(requireContext())
        imagePickerManager = ImagePickerManager(this).apply { setCallback(this@BaseRoomFragment) }

        view.findViewById<TextView>(R.id.tvHeader).text = "낙상 위험 분석"
        setupUI()
        setupRecyclerView()
        setupBackNavigation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ImagePickerManager 콜백
    override fun onImagePicked(uri: Uri) {
        Log.d(TAG, "선택된 이미지 URI: $uri")
        uploadGalleryImageToServer(uri)
    }

    override fun onError(message: String) {
        Log.e(TAG, message)
    }

    // UI 초기화
    private fun setupUI() = with(binding) {
        tvStep.text = "$step / 6"
        tvTitle.text = roomType
        tvSubtitle.text = when (roomType) {
            "거실" -> "다음 조건에 맞게 거실을 촬영해주세요."
            "주방" -> "다음 조건에 맞게 주방을 촬영해주세요."
            "욕실" -> "다음 조건에 맞게 욕실을 촬영해주세요."
            "일반 방" -> "다음 조건에 맞게 방을 촬영해주세요."
            "외부" -> "다음 조건에 맞게 외부 환경을 촬영해주세요."
            "기타" -> "다음 조건에 맞게 촬영해주세요."
            else -> ""
        }
        nextButton.setOnClickListener { onNextButtonClick() }
    }

    // RecyclerView 및 Adapter 설정
    private fun setupRecyclerView() {
        binding.roomRecyclerView.layoutManager = LinearLayoutManager(context)
        roomAdapter = RoomAdapter(
            roomList,
            onAddImageClick = { roomPos, guideIndex ->
                selectedRoomIndex = roomPos
                selectedGuideIndex = guideIndex
                showImageSelectionDialog()
            },
            onDeleteImageClick = { roomPos, guideIndex, imagePos ->
                val room = roomList[roomPos]
                when (guideIndex) {
                    1 -> room.guide1Images.removeAt(imagePos)
                    2 -> room.guide2Images.removeAt(imagePos)
                    3 -> room.guide3Images.removeAt(imagePos)
                }
                imageManager.saveImageData(room.name, guideIndex, "")
                roomAdapter.notifyItemChanged(roomPos)
            },
            onAddRoomClick = {
                roomList.add(RoomData(name = "$roomType ${roomList.size + 1}"))
                roomAdapter.notifyItemInserted(roomList.lastIndex)
            },
            onDeleteRoomClick = { pos ->
                if (roomList.size > 1) {
                    roomList.removeAt(pos)
                    roomAdapter.notifyItemRemoved(pos)
                }
            }
        )
        binding.roomRecyclerView.adapter = roomAdapter
    }

    // 뒤로가기 처리 (툴바 버튼 + 시스템 백버튼)
    private fun setupBackNavigation() {
        binding.header.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        )
    }

    // 이미지 선택 다이얼로그 표시
    private fun showImageSelectionDialog() {
        val options = arrayOf("카메라로 촬영", "갤러리에서 선택")
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("이미지 선택")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            imagePickerManager.openGallery()
                        } else {
                            imagePickerManager.checkGalleryPermission(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                permissionLauncher
                            )
                        }
                    }
                }
            }
            .show()
    }

    // 카메라 앱 실행
    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
            try {
                createImageFile().also { file ->
                    cameraImageUri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.winter.happyaging",
                        file
                    )
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
                    takePictureLauncher.launch(cameraIntent)
                }
            } catch (ex: IOException) {
                Log.e(TAG, "이미지 파일 생성 실패", ex)
            }
        } else {
            Toast.makeText(requireContext(), "사용 가능한 카메라 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // 임시 이미지 파일 생성
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    // 갤러리 이미지 업로드 (Uri -> RequestBody)
    private fun uploadGalleryImageToServer(uri: Uri) {
        requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
            val byteArray = inputStream.readBytes()
            val mimeType = requireContext().contentResolver.getType(uri) ?: "image/jpeg"

            val requestFile = byteArray.toRequestBody(mimeType.toMediaTypeOrNull())

            val imagePart = MultipartBody.Part.createFormData("image", GALLERY_IMAGE_FILE_NAME, requestFile)
            sendImageToServer(imagePart)
        } ?: Log.e(TAG, "이미지 변환 실패")
    }

    // 서버로 이미지 전송
    private fun sendImageToServer(imagePart: MultipartBody.Part) {
        val imageService = RetrofitClient.getInstance(requireContext())
            .create(ImageService::class.java)
        imageService.uploadImages(imagePart).enqueue(object : Callback<ImageResponse> {
            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.data ?: ""
                    Log.d(TAG, "업로드 성공! 서버 URL: $imageUrl")
                    saveImageToLocalDataStore(imageUrl)
                } else {
                    Log.e(TAG, "서버 응답 실패: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                Log.e(TAG, "네트워크 오류: ${t.message}")
            }
        })
    }

    // 로컬 데이터 저장 및 UI 갱신
    private fun saveImageToLocalDataStore(imageUrl: String) {
        val roomName = roomList[selectedRoomIndex].name
        imageManager.saveImageData(roomName, selectedGuideIndex, imageUrl)
        when (selectedGuideIndex) {
            1 -> roomList[selectedRoomIndex].guide1Images.add(imageUrl)
            2 -> roomList[selectedRoomIndex].guide2Images.add(imageUrl)
            3 -> roomList[selectedRoomIndex].guide3Images.add(imageUrl)
        }
        roomAdapter.notifyItemChanged(selectedRoomIndex)
    }

    protected open fun onNextButtonClick() {
        findNavController().navigate(nextAction)
    }
}
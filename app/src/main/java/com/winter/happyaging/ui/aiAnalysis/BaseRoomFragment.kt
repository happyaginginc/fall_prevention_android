package com.winter.happyaging.ui.aiAnalysis

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
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

    companion object {
        private const val TAG = "BaseRoomFragment"
        private const val IMAGE_FILE_NAME = "uploaded_image.jpg"
        private const val GALLERY_IMAGE_FILE_NAME = "gallery_image.jpg"
    }

    private var _binding: FragmentAiRoomBinding? = null
    protected val binding get() = _binding!!

    protected lateinit var imageManager: ImageManager
    protected val roomList = mutableListOf(RoomData(name = "$roomType 1"))

    /**
     * 현재 선택된 방 인덱스와 카메라 버튼 번호(1,2,3)
     */
    private var selectedRoomIndex: Int = 0
    private var selectedCameraNumber: Int = 0

    /**
     * Room 리스트를 표시할 어댑터
     */
    private lateinit var roomAdapter: RoomAdapter

    /**
     * 갤러리로부터 이미지를 선택하는 런처
     */
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                Log.d(TAG, "✅ 선택된 이미지 URI: $uri")
                uploadGalleryImageToServer(uri)
            } else {
                Log.e(TAG, "🚨 이미지가 선택되지 않았습니다.")
            }
        }

    /**
     * 카메라 접근 권한을 요청하는 런처
     */
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Log.e(TAG, "🚨 갤러리 접근 권한 거부됨")
            }
        }

    /**
     * 카메라 Intent를 실행하는 런처
     */
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                imageBitmap?.let { uploadCameraImageToServer(it) }
            }
        }

    // ---------------------------------------------------------------------------------------------
    // Lifecycle
    // ---------------------------------------------------------------------------------------------
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAiRoomBinding.bind(view)
        imageManager = ImageManager(requireContext())

        setupUI()
        setupRecyclerView()
        setupBackButton()
        setupSystemBackPressedHandler()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ---------------------------------------------------------------------------------------------
    // UI 셋업
    // ---------------------------------------------------------------------------------------------
    private fun setupUI() {
        // 상단 스텝 표시
        binding.tvStep.text = "$step / 6"
        binding.tvTitle.text = roomType
        binding.tvSubtitle.text = getSubtitleText(roomType)

        // 다음 버튼 (마지막 프래그먼트는 별도 override)
        binding.nextButton.setOnClickListener {
            onNextButtonClick()
        }
    }

    /**
     * roomType에 따라 자막을 변경하는 함수
     */
    private fun getSubtitleText(roomType: String): String {
        return when (roomType) {
            "거실" -> "다음 조건에 맞게 거실을 촬영해주세요."
            "주방" -> "다음 조건에 맞게 주방을 촬영해주세요."
            "욕실" -> "다음 조건에 맞게 욕실을 촬영해주세요."
            "일반 방" -> "다음 조건에 맞게 방을 촬영해주세요."
            "외부" -> "다음 조건에 맞게 외부 환경을 촬영해주세요."
            "기타" -> "다음 조건에 맞게 촬영해주세요."
            else -> ""
        }
    }

    /**
     * RecyclerView 및 Adapter를 초기화한다.
     */
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
                // 새 RoomData 추가
                roomList.add(RoomData(name = "$roomType ${roomList.size + 1}"))
                roomAdapter.notifyItemInserted(roomList.size - 1)
            },
            onDeleteRoomClick = {
                // 방이 여러 개일 때만 삭제 허용
                roomList.removeAt(it)
                roomAdapter.notifyItemRemoved(it)
            }
        )

        binding.roomRecyclerView.adapter = roomAdapter
    }

    /**
     * 뒤로가기 버튼 동작 설정
     */
    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    /**
     * 안드로이드 시스템 백버튼(하드웨어) 동작 설정
     */
    private fun setupSystemBackPressedHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        )
    }

    // ---------------------------------------------------------------------------------------------
    // 이미지 선택/업로드
    // ---------------------------------------------------------------------------------------------
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

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(cameraIntent)
    }

    private fun checkGalleryPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission)
            == PackageManager.PERMISSION_GRANTED
        ) {
            openGallery()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    private fun openGallery() {
        galleryLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    /**
     * 카메라 촬영 이미지 Bitmap -> 서버 업로드
     */
    private fun uploadCameraImageToServer(bitmap: Bitmap) {
        val byteArray = ByteArrayOutputStream().apply {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
        }.toByteArray()

        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
        val imagePart = MultipartBody.Part.createFormData("image", IMAGE_FILE_NAME, requestFile)
        sendImageToServer(imagePart)
    }

    /**
     * 갤러리 선택 이미지 Uri -> 서버 업로드
     */
    private fun uploadGalleryImageToServer(uri: Uri) {
        val context = requireContext()
        val inputStream = context.contentResolver.openInputStream(uri)
        val byteArray = inputStream?.readBytes()
        inputStream?.close()

        if (byteArray == null) {
            Log.e(TAG, "이미지 변환 실패")
            return
        }
        // MIME 타입 확인
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        val requestFile = RequestBody.create(mimeType.toMediaTypeOrNull(), byteArray)
        val imagePart = MultipartBody.Part.createFormData(
            "image",
            GALLERY_IMAGE_FILE_NAME,
            requestFile
        )
        sendImageToServer(imagePart)
    }

    /**
     * 실제 서버 업로드 요청 함수
     */
    private fun sendImageToServer(imagePart: MultipartBody.Part) {
        val storageService = RetrofitClient.getInstance(requireContext())
            .create(ImageService::class.java)

        storageService.uploadImages(imagePart).enqueue(object : Callback<ImageResponse> {
            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.data ?: ""
                    Log.d(TAG, "✅ 업로드 성공! 서버에 저장된 URL: $imageUrl")
                    saveImageToLocalDataStore(imageUrl)
                } else {
                    Log.e(TAG, "🚨 서버 응답 실패: ${response.code()}")
                    Log.e(TAG, "🚨 응답 본문: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                Log.e(TAG, "🚨 네트워크 오류 발생: ${t.message}")
            }
        })
    }

    /**
     * 업로드 성공한 이미지 URL을 RoomData와 SharedPreferences에 저장
     */
    private fun saveImageToLocalDataStore(imageUrl: String) {
        val roomName = roomList[selectedRoomIndex].name
        imageManager.saveImageData(roomName, selectedCameraNumber - 1, imageUrl)

        // 실제 화면에서도 미리보기 할 수 있도록 Uri 지정
        val imageUri = Uri.parse(imageUrl)
        roomList[selectedRoomIndex] = roomList[selectedRoomIndex].copy().apply {
            when (selectedCameraNumber) {
                1 -> imageUri1 = imageUri
                2 -> imageUri2 = imageUri
                3 -> imageUri3 = imageUri
            }
        }
        binding.roomRecyclerView.adapter?.notifyItemChanged(selectedRoomIndex)
    }

    // ---------------------------------------------------------------------------------------------
    // 내비게이션
    // ---------------------------------------------------------------------------------------------
    protected open fun onNextButtonClick() {
        // 기본 동작: 단순히 다음 Fragment로 넘어간다.
        findNavController().navigate(nextAction)
    }
}
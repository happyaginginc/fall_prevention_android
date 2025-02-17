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
    // 방 데이터: 이제 각 방은 세 개의 가이드별 이미지 리스트를 가집니다.
    protected val roomList = mutableListOf(RoomData(name = "$roomType 1"))

    /**
     * 현재 선택된 방 인덱스와 선택된 가이드(1~3)
     */
    private var selectedRoomIndex: Int = 0
    private var selectedGuideIndex: Int = 0

    /**
     * Room 리스트를 표시할 어댑터
     */
    private lateinit var roomAdapter: RoomAdapter

    // 이미지 선택/업로드 관련 런처는 그대로 사용
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                Log.d(TAG, "✅ 선택된 이미지 URI: $uri")
                uploadGalleryImageToServer(uri)
            } else {
                Log.e(TAG, "🚨 이미지가 선택되지 않았습니다.")
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) openGallery() else Log.e(TAG, "🚨 갤러리 접근 권한 거부됨")
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                imageBitmap?.let { uploadCameraImageToServer(it) }
            }
        }

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

    private fun setupUI() {
        binding.tvStep.text = "$step / 6"
        binding.tvTitle.text = roomType
        binding.tvSubtitle.text = getSubtitleText(roomType)
        binding.nextButton.setOnClickListener { onNextButtonClick() }
    }

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
                // 삭제 시: 해당 방의 해당 가이드 리스트에서 imagePos 번째 이미지 제거 후 DataStore 업데이트
                val room = roomList[roomPos]
                when (guideIndex) {
                    1 -> room.guide1Images.removeAt(imagePos)
                    2 -> room.guide2Images.removeAt(imagePos)
                    3 -> room.guide3Images.removeAt(imagePos)
                }
                // DataStore에도 반영 (전체 데이터 재저장)
                // 간단하게 해당 방 전체를 다시 저장합니다.
                imageManager.saveImageData(room.name, guideIndex, "") // 빈 문자열은 무시하도록 처리 가능
                roomAdapter.notifyItemChanged(roomPos)
            },
            onAddRoomClick = { pos ->
                roomList.add(RoomData(name = "$roomType ${roomList.size + 1}"))
                roomAdapter.notifyItemInserted(roomList.size - 1)
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

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
    }

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
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES else Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED)
            openGallery() else permissionLauncher.launch(permission)
    }

    private fun openGallery() {
        galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun uploadCameraImageToServer(bitmap: Bitmap) {
        val byteArray = ByteArrayOutputStream().apply {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, this)
        }.toByteArray()
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
        val imagePart = MultipartBody.Part.createFormData("image", IMAGE_FILE_NAME, requestFile)
        sendImageToServer(imagePart)
    }

    private fun uploadGalleryImageToServer(uri: Uri) {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val byteArray = inputStream?.readBytes()
        inputStream?.close()
        if (byteArray == null) {
            Log.e(TAG, "이미지 변환 실패")
            return
        }
        val mimeType = requireContext().contentResolver.getType(uri) ?: "image/jpeg"
        val requestFile = RequestBody.create(mimeType.toMediaTypeOrNull(), byteArray)
        val imagePart = MultipartBody.Part.createFormData("image", GALLERY_IMAGE_FILE_NAME, requestFile)
        sendImageToServer(imagePart)
    }

    private fun sendImageToServer(imagePart: MultipartBody.Part) {
        val storageService = RetrofitClient.getInstance(requireContext()).create(ImageService::class.java)
        storageService.uploadImages(imagePart).enqueue(object : Callback<ImageResponse> {
            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.data ?: ""
                    Log.d(TAG, "✅ 업로드 성공! 서버에 저장된 URL (파일명): $imageUrl")
                    saveImageToLocalDataStore(imageUrl)
                } else {
                    Log.e(TAG, "🚨 서버 응답 실패: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                Log.e(TAG, "🚨 네트워크 오류 발생: ${t.message}")
            }
        })
    }

    private fun saveImageToLocalDataStore(imageUrl: String) {
        val roomName = roomList[selectedRoomIndex].name
        // 저장: 선택된 가이드(selectedGuideIndex)에 이미지 추가
        imageManager.saveImageData(roomName, selectedGuideIndex, imageUrl)
        // 해당 방 데이터 업데이트
        when (selectedGuideIndex) {
            1 -> roomList[selectedRoomIndex].guide1Images.add(imageUrl)
            2 -> roomList[selectedRoomIndex].guide2Images.add(imageUrl)
            3 -> roomList[selectedRoomIndex].guide3Images.add(imageUrl)
        }
        roomAdapter.notifyItemChanged(selectedRoomIndex)
    }

    // 분석 요청 시 각 방의 세 가이드 이미지들을 합쳐 전송합니다.
    protected open fun onNextButtonClick() {
        // 예시: imageManager.getAllImageData()를 통해 각 방의 이미지 리스트를 가져와 RoomRequest 생성
        // (RoomRequest의 roomImages는 세 가이드 이미지들을 합친 리스트로 설정)
        findNavController().navigate(nextAction)
    }
}
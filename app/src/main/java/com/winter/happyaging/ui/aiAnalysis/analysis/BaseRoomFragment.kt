package com.winter.happyaging.ui.aiAnalysis.analysis

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.winter.happyaging.R
import com.winter.happyaging.data.aiAnalysis.model.GuideData
import com.winter.happyaging.data.aiAnalysis.model.RoomData
import com.winter.happyaging.data.image.model.ImageResponse
import com.winter.happyaging.data.image.service.ImageService
import com.winter.happyaging.databinding.FragmentAiRoomBinding
import com.winter.happyaging.network.ImageManager
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.ui.aiAnalysis.analysis.adapter.RoomAdapter
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
    protected val roomType: String,
    private val nextAction: Int
) : Fragment(R.layout.fragment_ai_room), ImagePickerManager.ImagePickerCallback {

    protected open val guideTexts: List<String> = emptyList()

    companion object {
        private const val TAG = "BaseRoomFragment"
        private const val GALLERY_IMAGE_FILE_NAME = "gallery_image.jpg"
    }

    private var _binding: FragmentAiRoomBinding? = null
    protected val binding get() = _binding!!

    protected lateinit var imageManager: ImageManager
    protected val roomList = mutableListOf<RoomData>()

    private var selectedRoomIndex = 0
    private var selectedGuideIndex = 0

    private lateinit var roomAdapter: RoomAdapter
    private lateinit var imagePickerManager: ImagePickerManager
    private var cameraImageUri: Uri? = null

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                imagePickerManager.openGallery()
            }
        }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "카메라 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                cameraImageUri?.let { uploadGalleryImageToServer(it) }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAiRoomBinding.bind(view)
        imageManager = ImageManager(requireContext())
        imagePickerManager = ImagePickerManager(this).apply { setCallback(this@BaseRoomFragment) }

        val guides = guideTexts.map { GuideData(it, mutableListOf()) }.toMutableList()
        roomList.add(RoomData(name = "$roomType 1", guides = guides))

        binding.header.tvHeader.text = "낙상 위험 분석"
        setupUI()
        setupRecyclerView()
        setupBackNavigation()
        binding.roomRecyclerView.isNestedScrollingEnabled = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onImagePicked(uri: Uri) {
        uploadGalleryImageToServer(uri)
    }

    override fun onError(message: String) {
        // 에러 메시지 처리(필요시 사용자에게 노출)
    }

    private fun setupUI() = with(binding) {
        tvStep.text = "$step / 6"
        tvTitle.text = roomType
        tvSubtitle.text = when (roomType) {
            "거실" -> "세련되고 편안한 거실을 촬영해주세요."
            "주방" -> "깔끔하고 현대적인 주방을 촬영해주세요."
            "욕실/화장실" -> "안전하고 청결한 욕실을 촬영해주세요."
            "침실" -> "편안한 일반 방을 촬영해주세요."
            "외부" -> "넓은 외부 환경을 촬영해주세요."
            "기타" -> "추가적인 공간을 촬영해주세요."
            else -> ""
        }
        nextButton.setOnClickListener { onNextButtonClick() }
    }

    private fun setupRecyclerView() {
        binding.roomRecyclerView.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(context)
        roomAdapter = RoomAdapter(
            roomList = roomList,
            onAddImageClick = { roomPos, guidePos ->
                selectedRoomIndex = roomPos
                selectedGuideIndex = guidePos
                showImageSelectionDialog()
            },
            onDeleteImageClick = { roomPos, guidePos, imagePos ->
                roomList[roomPos].guides[guidePos].images.removeAt(imagePos)
                imageManager.removeImageData(roomList[roomPos].name, guidePos, "")
                roomAdapter.notifyItemChanged(roomPos)
            },
            onAddRoomClick = {
                val newGuides = guideTexts.map { GuideData(it, mutableListOf()) }.toMutableList()
                roomList.add(RoomData("$roomType ${roomList.size + 1}", newGuides))
                roomAdapter.notifyItemInserted(roomList.lastIndex)
                binding.roomRecyclerView.smoothScrollToPosition(roomList.lastIndex)
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

    private fun setupBackNavigation() {
        binding.header.btnBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().supportFragmentManager.popBackStack()
                }
            })
    }

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

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            return
        }
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                try {
                    val file = createImageFile()
                    cameraImageUri = FileProvider.getUriForFile(requireContext(), "com.winter.happyaging", file)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
                    takePictureLauncher.launch(intent)
                } catch (ex: IOException) {
                    // 파일 생성 실패 시 처리
                }
            } else {
                Toast.makeText(requireContext(), "사용 가능한 카메라 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun uploadGalleryImageToServer(uri: Uri) {
        requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
            val byteArray = inputStream.readBytes()
            val mimeType = requireContext().contentResolver.getType(uri) ?: "image/jpeg"
            val requestFile = byteArray.toRequestBody(mimeType.toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", GALLERY_IMAGE_FILE_NAME, requestFile)
            sendImageToServer(imagePart)
        }
    }

    private fun sendImageToServer(imagePart: MultipartBody.Part) {
        RetrofitClient.getInstance(requireContext())
            .create(ImageService::class.java)
            .uploadImages(imagePart)
            .enqueue(object : Callback<ImageResponse> {
                override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                    if (response.isSuccessful) {
                        val imageUrl = response.body()?.data.orEmpty()
                        saveImageToLocalDataStore(imageUrl)
                    }
                }
                override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                    // 실패 처리 (필요시 사용자에게 알림)
                }
            })
    }

    private fun saveImageToLocalDataStore(imageUrl: String) {
        roomList[selectedRoomIndex].guides[selectedGuideIndex].images.add(imageUrl)
        imageManager.saveImageData(roomList[selectedRoomIndex].name, selectedGuideIndex, imageUrl)
        val currentScrollY = binding.mainContent.scrollY
        roomAdapter.notifyItemChanged(selectedRoomIndex)
        binding.mainContent.post { binding.mainContent.scrollTo(0, currentScrollY) }
    }

    protected open fun onNextButtonClick() {
        findNavController().navigate(nextAction)
    }
}
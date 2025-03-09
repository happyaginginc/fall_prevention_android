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
import androidx.recyclerview.widget.LinearLayoutManager
import com.winter.happyaging.BuildConfig
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
        private const val GALLERY_IMAGE_FILE_NAME = "gallery_image.jpg"
        private val globalRoomData = mutableMapOf<String, MutableList<RoomData>>()
    }

    private var _binding: FragmentAiRoomBinding? = null
    protected val binding get() = _binding!!

    private val roomList = mutableListOf<RoomData>()

    lateinit var imageManager: ImageManager
    private lateinit var roomAdapter: RoomAdapter
    private lateinit var imagePickerManager: ImagePickerManager

    private var cameraImageUri: Uri? = null
    private var selectedRoomIndex = 0
    private var selectedGuideIndex = 0

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                imagePickerManager.openGallery()
            } else {
                Toast.makeText(requireContext(), "갤러리 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
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
            } else {
                Toast.makeText(requireContext(), "사진 촬영이 취소되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAiRoomBinding.bind(view)
        imageManager = ImageManager(requireContext())
        imagePickerManager = ImagePickerManager(this).apply { setCallback(this@BaseRoomFragment) }

        val existingRooms = globalRoomData[roomType]
        if (!existingRooms.isNullOrEmpty()) {
            roomList.clear()
            roomList.addAll(existingRooms)
            for (room in roomList) {
                if (room.guides.size != guideTexts.size) {
                    val newGuides = guideTexts.map { GuideData(it, mutableListOf()) }.toMutableList()
                    val minSize = minOf(room.guides.size, newGuides.size)
                    for (i in 0 until minSize) {
                        newGuides[i].images.addAll(room.guides[i].images)
                    }
                    room.guides = newGuides
                }
            }
        } else {
            val guides = guideTexts.map { GuideData(it, mutableListOf()) }.toMutableList()
            roomList.add(RoomData(name = "$roomType 1", guides = guides))
        }

        mergeWithLocalImageData()
        setupUI()
        setupRecyclerView()
        setupBackNavigation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        globalRoomData[roomType] = roomList.toMutableList()
        _binding = null
    }

    private fun mergeWithLocalImageData() {
        try {
            val allRooms = imageManager.getAllImageData()
            roomList.forEach { room ->
                val storedInfo = allRooms[room.name] ?: return@forEach
                val minSize = minOf(room.guides.size, storedInfo.guides.size)
                for (i in 0 until minSize) {
                    room.guides[i].images.clear()
                    room.guides[i].images.addAll(storedInfo.guides[i])
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "로컬 이미지 데이터를 불러오는 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
        }
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
        btnPrev.visibility = if (step == 1) View.INVISIBLE else View.VISIBLE
        btnNext.text = if (step == 6) "분석 시작" else "다음"
        btnPrev.setOnClickListener {
            scrollContainer.smoothScrollTo(0, 0)
            findNavController().popBackStack()
        }
        btnNext.setOnClickListener { onNextButtonClick() }
    }

    private fun setupRecyclerView() {
        binding.roomRecyclerView.layoutManager = LinearLayoutManager(context)
        roomAdapter = RoomAdapter(
            roomList = roomList,
            roomType = roomType,
            onAddImageClick = { roomPos, guidePos ->
                selectedRoomIndex = roomPos
                selectedGuideIndex = guidePos
                showImageSelectionDialog()
            },
            onDeleteImageClick = { roomPos, guidePos, imagePos ->
                val removedUrl = roomList[roomPos].guides[guidePos].images[imagePos]
                roomList[roomPos].guides[guidePos].images.removeAt(imagePos)
                imageManager.removeImageData(roomList[roomPos].name, guidePos, removedUrl)
                roomAdapter.notifyItemChanged(roomPos)
            },
            onAddRoomClick = {
                val guides = guideTexts.map { GuideData(it, mutableListOf()) }.toMutableList()
                roomList.add(RoomData("$roomType ${roomList.size + 1}", guides))
                val newPos = roomList.lastIndex
                roomAdapter.notifyItemInserted(newPos)
                binding.roomRecyclerView.post {
                    val vh = binding.roomRecyclerView.findViewHolderForAdapterPosition(newPos)
                    vh?.itemView?.let { itemView ->
                        binding.scrollContainer.smoothScrollTo(0, itemView.top)
                    }
                }
            },
            onDeleteRoomClick = { pos ->
                if (roomList.size > 1) {
                    roomList.removeAt(pos)
                    roomAdapter.notifyItemRemoved(pos)
                } else {
                    Toast.makeText(requireContext(), "최소 한 개의 방은 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        )
        binding.roomRecyclerView.adapter = roomAdapter
    }

    private fun setupBackNavigation() {
        binding.header.btnBack.setOnClickListener { handleBackPress() }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackPress()
                }
            }
        )
    }

    private fun handleBackPress() {
        if (!hasImagesToDelete()) {
            imageManager.clearLocalImageData()
            globalRoomData.clear()
            roomList.clear()
            binding.scrollContainer.smoothScrollTo(0, 0)
            findNavController().popBackStack(R.id.AIIntroFragment, false)
        } else {
            showDeleteAlertDialog()
        }
    }

    private fun hasImagesToDelete(): Boolean {
        val localData = imageManager.getAllImageData()
        if (localData.isNotEmpty()) {
            for (roomImageInfo in localData.values) {
                roomImageInfo.guides.forEach { imageList ->
                    if (imageList.isNotEmpty()) return true
                }
            }
        }
        if (globalRoomData.isNotEmpty()) {
            globalRoomData.values.forEach { roomDataList ->
                roomDataList.forEach { roomData ->
                    roomData.guides.forEach { guide ->
                        if (guide.images.isNotEmpty()) return true
                    }
                }
            }
        }
        return false
    }

    private fun showDeleteAlertDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("뒤로가기")
            .setMessage("지금 뒤로가시면 촬영하신 데이터가 모두 사라집니다.\n정말 뒤로 가시겠습니까?")
            .setPositiveButton("예") { _, _ ->
                imageManager.clearLocalImageData()
                globalRoomData.clear()
                roomList.clear()
                binding.scrollContainer.smoothScrollTo(0, 0)
                findNavController().popBackStack(R.id.AIIntroFragment, false)
            }
            .setNegativeButton("아니오", null)
            .show()
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
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            try {
                val file = createImageFile()
                cameraImageUri = FileProvider.getUriForFile(
                    requireContext(),
                    BuildConfig.APPLICATION_ID + ".fileprovider",
                    file
                )
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                takePictureLauncher.launch(intent)
            } catch (ex: IOException) {
                Toast.makeText(requireContext(), "사진 파일 생성 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "사용 가능한 카메라 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    override fun onImagePicked(uri: Uri) {
        uploadGalleryImageToServer(uri)
    }

    override fun onError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun uploadGalleryImageToServer(uri: Uri) {
        try {
            requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                val byteArray = inputStream.readBytes()
                val mimeType = requireContext().contentResolver.getType(uri) ?: "image/jpeg"
                val requestFile = byteArray.toRequestBody(mimeType.toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", GALLERY_IMAGE_FILE_NAME, requestFile)
                sendImageToServer(imagePart)
            } ?: Toast.makeText(requireContext(), "이미지 파일을 읽을 수 없습니다.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "이미지 파일 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
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
                        if (imageUrl.isNotEmpty()) {
                            saveImageToLocalDataStore(imageUrl)
                        } else {
                            Toast.makeText(requireContext(), "이미지 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "이미지 업로드 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "이미지 업로드 중 오류가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun saveImageToLocalDataStore(imageUrl: String) {
        roomList[selectedRoomIndex].guides[selectedGuideIndex].images.add(imageUrl)
        imageManager.saveImageData(
            roomList[selectedRoomIndex].name,
            selectedGuideIndex,
            imageUrl
        )
        val currentScrollY = binding.scrollContainer.scrollY
        roomAdapter.notifyItemChanged(selectedRoomIndex)
        binding.scrollContainer.post { binding.scrollContainer.scrollTo(0, currentScrollY) }
    }

    protected open fun onNextButtonClick() {
        findNavController().navigate(nextAction)
    }
}
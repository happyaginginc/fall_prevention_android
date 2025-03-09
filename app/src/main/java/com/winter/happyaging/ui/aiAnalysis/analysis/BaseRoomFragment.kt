package com.winter.happyaging.ui.aiAnalysis.analysis

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

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

    // 권한 요청(갤러리)
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                imagePickerManager.openGallery()
            } else {
                Toast.makeText(requireContext(), "갤러리 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }

    // 권한 요청(카메라)
    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "카메라 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    // 사진 촬영 결과
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

        // 기존에 저장돼 있던 방 데이터(글로벌)
        val existingRooms = globalRoomData[roomType]
        if (!existingRooms.isNullOrEmpty()) {
            roomList.clear()
            roomList.addAll(existingRooms)
            // 새로운 guideTexts가 들어왔을 수 있으므로, 기존 room에도 맞춰서 merge
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
            // 새로운 방 리스트 초기화
            val guides = guideTexts.map { GuideData(it, mutableListOf()) }.toMutableList()
            roomList.add(RoomData(name = "$roomType 1", guides = guides))
        }

        // 로컬(ImageManager)에 저장된 정보와 머지
        mergeWithLocalImageData()

        setupUI()
        setupRecyclerView()
        setupBackNavigation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // fragment가 사라질 때 roomList를 globalRoomData에 저장
        globalRoomData[roomType] = roomList.toMutableList()
        _binding = null
    }

    // 로컬 datastore와 UI 데이터를 머지
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

        // 버튼 설정
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
            // 1) 이미지 리사이징/압축
            val processedImageData = processImage(uri)
                ?: run {
                    Toast.makeText(requireContext(), "이미지 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    return
                }

            // 2) 멀티파트 전송
            val requestFile = processedImageData.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", GALLERY_IMAGE_FILE_NAME, requestFile)
            sendImageToServer(imagePart)

        } catch (e: Exception) {
            e.printStackTrace()
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


    private fun processImage(uri: Uri): ByteArray? {
        // 1) inJustDecodeBounds로 원본 크기만 파악 후, inSampleSize 계산
        val originalBitmap = decodeSampledBitmap(uri, 4096) // 4K 이상 넘는 사이즈를 한번에 decode하지 않도록 샘플링
            ?: return null

        // 2) 최대 해상도 1920으로 스케일링
        val scaledBitmap = scaleBitmap(originalBitmap, 1920)

        // 3) JPEG 압축률 100%에서 시작해서 5MB(5*1024*1024) 이하가 될 때까지 줄이는 로직
        return compressBitmapIteratively(scaledBitmap, 5 * 1024 * 1024)
    }

    private fun decodeSampledBitmap(uri: Uri, maxDim: Int): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        requireContext().contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }
        val (width, height) = options.outWidth to options.outHeight

        var inSampleSize = 1
        while ((width / inSampleSize) > maxDim || (height / inSampleSize) > maxDim) {
            inSampleSize *= 2
        }

        // 실제 디코딩
        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = inSampleSize
        }
        return requireContext().contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, decodeOptions)
        }
    }

    private fun scaleBitmap(bitmap: Bitmap, maxDim: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val maxSide = max(width, height)

        if (maxSide <= maxDim) {
            // 이미 충분히 작으면 원본 그대로 리턴
            return bitmap
        }

        val scale = maxDim.toFloat() / maxSide.toFloat()
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun compressBitmapIteratively(bitmap: Bitmap, maxBytes: Int): ByteArray {
        var quality = 100
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)

        // 파일 크기가 maxBytes(5MB) 이하가 될 때까지, 또는 quality가 너무 낮아질 때까지 품질을 줄임
        while (bos.size() > maxBytes && quality > 10) {
            bos.reset()
            quality -= 5
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)
        }

        return bos.toByteArray()
    }

    protected open fun onNextButtonClick() {
        findNavController().navigate(nextAction)
    }
}

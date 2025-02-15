package com.winter.happyaging.ai

import com.winter.happyaging.R

class AIFourthFragment : BaseRoomFragment("기타", R.id.action_AIFourthFragment_to_AnalysisResultFragment)
//import android.content.Context
//import android.content.Intent
//import android.graphics.Bitmap
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import android.util.Log
//import android.view.View
//import android.widget.Toast
//import androidx.activity.result.ActivityResultLauncher
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.bumptech.glide.Glide
//import com.winter.happyaging.ImageManager
//import com.winter.happyaging.R
//import com.winter.happyaging.ResDTO.AIAnalysisRequest
//import com.winter.happyaging.ResDTO.AIAnalysisResponse
//import com.winter.happyaging.ResDTO.ImageResponse
//import com.winter.happyaging.ResDTO.RoomRequest
//import com.winter.happyaging.TokenManager
//import com.winter.happyaging.databinding.FragmentAiFourthBinding
//import com.winter.happyaging.RetrofitClient
//import com.winter.happyaging.service.AIAnalysisRepository
//import com.winter.happyaging.service.ImageService
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.io.ByteArrayOutputStream
//
//class AIFourthFragment : Fragment(R.layout.fragment_ai_fourth) {
//
//    private var _binding: FragmentAiFourthBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
//    private lateinit var imageManager: ImageManager
//    private lateinit var tokenManager: TokenManager
//    private lateinit var roomAdapter: RoomAdapter
//
//    private val roomList = mutableListOf(RoomData(name = "기타 1"))
//    private var selectedRoomIndex: Int = 0
//    private var selectedCameraNumber: Int = 0
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        _binding = FragmentAiFourthBinding.bind(view)
//        imageManager = ImageManager(requireContext())
//        tokenManager = TokenManager(requireContext())
//
//        binding.roomRecyclerView.layoutManager = LinearLayoutManager(context)
//
//        roomAdapter = RoomAdapter(roomList, onCameraClick = { position, cameraNumber ->
//            selectedRoomIndex = position
//            selectedCameraNumber = cameraNumber
//            openCamera()
//        }, onAddRoomClick = {
//            roomList.add(RoomData(name = "기타 ${roomList.size + 1}"))
//            roomAdapter.notifyItemInserted(roomList.size - 1)
//        }, onDeleteRoomClick = {
//            roomList.removeAt(it)
//            roomAdapter.notifyItemRemoved(it)
//        })
//
//        binding.roomRecyclerView.adapter = roomAdapter
//
//        loadStoredImages()
//
//        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == android.app.Activity.RESULT_OK) {
//                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
//                imageBitmap?.let { bitmap ->
//                    uploadImageToServer(bitmap)
//                }
//            }
//        }
//
//        binding.nextButton.setOnClickListener {
//            sendAnalysisRequest()
//        }
//    }
//
//    private fun openCamera() {
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        takePictureLauncher.launch(cameraIntent)
//    }
//
//    private fun uploadImageToServer(bitmap: Bitmap) {
//        val byteArray = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
//        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray.toByteArray())
//        val imagePart = MultipartBody.Part.createFormData("image", "uploaded_image.jpg", requestFile)
//
//        val storageService = RetrofitClient.getInstance(requireContext()).create(ImageService::class.java)
//        storageService.uploadImages(imagePart).enqueue(object : Callback<ImageResponse> {
//            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
//                if (response.isSuccessful) {
//                    val imageUrl = response.body()?.data ?: ""
//                    Log.d("AIFourthFragment", "업로드 성공! 저장된 URL: $imageUrl")
//
//                    saveImageToDataStore(imageUrl)
//                    loadStoredImages()
//                    Toast.makeText(requireContext(), "이미지 업로드 완료!", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
//                Log.e("AIFourthFragment", "네트워크 오류 발생: ${t.message}")
//                Toast.makeText(requireContext(), "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun saveImageToDataStore(imageUrl: String) {
//        val roomName = roomList[selectedRoomIndex].name
//        imageManager.saveImageData(roomName, selectedCameraNumber - 1, imageUrl)
//
//        // 방 데이터 업데이트
//        val imageUri = Uri.parse(imageUrl)
//        when (selectedCameraNumber) {
//            1 -> roomList[selectedRoomIndex].imageUri1 = imageUri
//            2 -> roomList[selectedRoomIndex].imageUri2 = imageUri
//            3 -> roomList[selectedRoomIndex].imageUri3 = imageUri
//        }
//
//        binding.roomRecyclerView.adapter?.notifyItemChanged(selectedRoomIndex)
//    }
//
//
//    private fun loadStoredImages() {
//        for (room in roomList) {
//            val storedImages = imageManager.getImageData(room.name)
//
//            // JSON 파싱 오류 방지를 위해 빈 문자열 검사 후 적용
//            room.imageUri1 = storedImages.getOrNull(0)?.takeIf { it.isNotEmpty() }?.let { Uri.parse(it) }
//            room.imageUri2 = storedImages.getOrNull(1)?.takeIf { it.isNotEmpty() }?.let { Uri.parse(it) }
//            room.imageUri3 = storedImages.getOrNull(2)?.takeIf { it.isNotEmpty() }?.let { Uri.parse(it) }
//        }
//
//        binding.roomRecyclerView.adapter?.notifyDataSetChanged()
//    }
//
//    private fun sendAnalysisRequest() {
//        val token = tokenManager.getAccessToken() ?: ""
//        val seniorId = getStoredSeniorId()
//
//        if (token.isEmpty() || seniorId == -1) {
//            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val rooms = roomList.map { room ->
//            RoomRequest(
//                roomName = room.name,
//                roomCategory = getRoomCategory(room.name),
//                roomImages = imageManager.getImageData(room.name).filter { it.isNotEmpty() } // ✅ 빈 값 제거 후 전송
//            )
//        }
//
//        val request = AIAnalysisRequest(rooms)
//
//        AIAnalysisRepository.uploadRoomImages(
//            context = requireContext(),
//            binding = binding.loadingLayout,
//            seniorId = seniorId,
//            token = token,
//            roomDataList = roomList,
//            onSuccess = { response ->
//                handleAnalysisSuccess(response)
//            },
//            onFailure = { error ->
//                Toast.makeText(requireContext(), "분석 실패: $error", Toast.LENGTH_SHORT).show()
//            }
//        )
//    }
//
//
//    private fun handleAnalysisSuccess(response: AIAnalysisResponse) {
//        Log.d("AIFourthFragment", "분석 성공: ${response.data}")
//        Toast.makeText(requireContext(), "분석이 완료되었습니다!", Toast.LENGTH_SHORT).show()
//        findNavController().navigate(R.id.action_AIFourthFragment_to_AnalysisResultFragment)
//    }
//
//    private fun getStoredSeniorId(): Int {
//        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
//        return sharedPreferences.getInt("seniorId", -1)
//    }
//
//    private fun getRoomCategory(roomName: String): String {
//        return when {
//            roomName.contains("화장실") -> "BATHROOM"
//            roomName.contains("방") -> "ROOM"
//            else -> "OTHER"
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

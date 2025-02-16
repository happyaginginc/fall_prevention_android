package com.winter.happyaging.ai

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.winter.happyaging.ImageManager
import com.winter.happyaging.R
import com.winter.happyaging.ResDTO.ImageResponse
import com.winter.happyaging.databinding.FragmentAiRoomBinding
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.service.ImageService
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
) :
    Fragment(R.layout.fragment_ai_room) {

    private var _binding: FragmentAiRoomBinding? = null
    val binding get() = _binding!!
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    lateinit var imageManager: ImageManager
    private lateinit var roomAdapter: RoomAdapter

    val roomList = mutableListOf(RoomData(name = "$roomType 1"))
    private var selectedRoomIndex: Int = 0
    private var selectedCameraNumber: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAiRoomBinding.bind(view)
        imageManager = ImageManager(requireContext())

        binding.tvStep.text = "$step/3" // 뒤 숫자는 늘어나면 알아서 바꾸기
        binding.tvTitle.text = roomType
        binding.tvSubtitle.text = when (roomType) {
            "화장실" -> "다음 조건에 맞게 화장실을 촬영해주세요"
            "일반 방" -> "다음 조건에 맞게 방을 촬영해주세요"
            "기타" -> "다음 조건에 맞게 촬영해주세요"
            else -> ""
        }

        setupRecyclerView()
        setupCameraLauncher()
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
                openCamera()
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

    private fun setupCameraLauncher() {
        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == android.app.Activity.RESULT_OK) {
                    val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let { uploadImageToServer(it) }
                }
            }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(cameraIntent)
    }

    private fun uploadImageToServer(bitmap: Bitmap) {
        val byteArray = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArray)
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray.toByteArray())
        val imagePart = MultipartBody.Part.createFormData("image", "uploaded_image.jpg", requestFile)

        val token = "Bearer" // + getToken() // ✅ 토큰 가져오기
        Log.d("BaseRoomFragment", "Upload 시작 - Token: $token") // ✅ 토큰 출력

        val storageService = RetrofitClient.getInstance(requireContext()).create(ImageService::class.java)
        storageService.uploadImages(imagePart).enqueue(object : Callback<ImageResponse> {
            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
                if (response.isSuccessful) {
                    val imageUrl = response.body()?.data ?: ""
                    Log.d("BaseRoomFragment", "업로드 성공! 저장된 URL: $imageUrl")

                    // 서버 응답이 성공하면 저장 + UI 갱신
                    saveImageToDataStore(imageUrl)
                    Toast.makeText(requireContext(), "이미지 업로드 완료!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                Log.e("BaseRoomFragment", "네트워크 오류 발생: ${t.message}")
            }
        })
    }


    private fun saveImageToDataStore(imageUrl: String) {
        val roomName = roomList[selectedRoomIndex].name

        // ✅ 저장 직전 디버깅
        Log.d("BaseRoomFragment", "saveImageData 호출: roomName=$roomName, cameraIndex=${selectedCameraNumber - 1}, imageUrl=$imageUrl")

        imageManager.saveImageData(roomName, selectedCameraNumber - 1, imageUrl)

        val imageUri = Uri.parse(imageUrl)
        val updatedRoom = roomList[selectedRoomIndex].copy().apply {
            when (selectedCameraNumber) {
                1 -> roomList[selectedRoomIndex].imageUri1 = imageUri
                2 -> roomList[selectedRoomIndex].imageUri2 = imageUri
                3 -> roomList[selectedRoomIndex].imageUri3 = imageUri
            }
        }

        roomList[selectedRoomIndex] = updatedRoom

        // ✅ 저장 후 DataStore에서 데이터 불러와서 확인
        Log.d("BaseRoomFragment", "DataStore 저장 완료 후 불러온 데이터: ${imageManager.getImageData(roomName)}")

        binding.roomRecyclerView.adapter?.notifyItemChanged(selectedRoomIndex)
    }


    private fun onNextButtonClick() {
        findNavController().navigate(nextAction)
    }

    fun getToken(): String {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("accessToken", "") ?: ""
        Log.d("BaseRoomFragment", "저장된 토큰: $token") // ✅ 저장된 토큰 확인
        return token
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

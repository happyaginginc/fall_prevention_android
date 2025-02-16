package com.winter.happyaging.ai

import com.winter.happyaging.R

class AISecondFragment : BaseRoomFragment(1,"화장실", R.id.action_AISecondFragment_to_AIThirdFragment)

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
//import com.winter.happyaging.ImageManager
//import com.winter.happyaging.R
//import com.winter.happyaging.databinding.FragmentAiSecondBinding
//import com.winter.happyaging.network.RetrofitClient
//import com.winter.happyaging.ResDTO.ImageResponse
//import com.winter.happyaging.service.ImageService
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.io.ByteArrayOutputStream
//
//class AISecondFragment : Fragment(R.layout.fragment_ai_second) {
//
//    private var _binding: FragmentAiSecondBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
//    private lateinit var imageManager: ImageManager // DataStore 관리 클래스
//    private val roomList = mutableListOf(RoomData(name = "화장실 1"))
//    private lateinit var roomAdapter: RoomAdapter
//    private var selectedRoomIndex: Int = 0
//    private var selectedCameraNumber: Int = 0
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        _binding = FragmentAiSecondBinding.bind(view)
//        imageManager = ImageManager(requireContext()) // DataStore 초기화
//
//        binding.roomRecyclerView.layoutManager = LinearLayoutManager(context)
//        roomAdapter = RoomAdapter(
//            roomList,
//            onCameraClick = { position, cameraNumber ->
//                selectedRoomIndex = position
//                selectedCameraNumber = cameraNumber
//                openCamera()
//            },
//            onAddRoomClick = {
//                roomList.add(RoomData(name = "화장실 ${roomList.size + 1}"))
//                roomAdapter.notifyItemInserted(roomList.size - 1)
//            },
//            onDeleteRoomClick = {
//                roomList.removeAt(it)
//                roomAdapter.notifyItemRemoved(it)
//            }
//        )
//
//        binding.roomRecyclerView.adapter = roomAdapter
//
//        takePictureLauncher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//                if (result.resultCode == android.app.Activity.RESULT_OK) {
//                    val imageBitmap = result.data?.extras?.get("data") as? Bitmap
//                    imageBitmap?.let { uploadImageToServer(it) }
//                }
//            }
//
//        binding.nextButton.setOnClickListener {
//            findNavController().navigate(R.id.action_AISecondFragment_to_AIThirdFragment)
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
//        val requestFile =
//            RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray.toByteArray())
//        val imagePart =
//            MultipartBody.Part.createFormData("image", "uploaded_image.jpg", requestFile)
//
//        val storageService =
//            RetrofitClient.getInstance(requireContext()).create(ImageService::class.java)
//        storageService.uploadImages(imagePart).enqueue(object : Callback<ImageResponse> {
//            override fun onResponse(call: Call<ImageResponse>, response: Response<ImageResponse>) {
//                if (response.isSuccessful) {
//                    val imageUrl = response.body()?.data ?: ""
//                    Log.d("AISecondFragment", "업로드 성공! 저장된 URL: $imageUrl")
//
//                    // DataStore에 저장 (방 이름, 칸 번호)
//                    val roomName = roomList[selectedRoomIndex].name
//                    imageManager.saveImageData(
//                        "$roomName-${selectedCameraNumber}",
//                        selectedRoomIndex,
//                        imageUrl
//                    )
//
//                    // RoomAdapter UI 갱신
//                    val imageUri = Uri.parse(imageUrl)
//                    when (selectedCameraNumber) {
//                        1 -> roomList[selectedRoomIndex].imageUri1 = imageUri
//                        2 -> roomList[selectedRoomIndex].imageUri2 = imageUri
//                        3 -> roomList[selectedRoomIndex].imageUri3 = imageUri
//                    }
//                    roomAdapter.notifyItemChanged(selectedRoomIndex)
//
//                    Toast.makeText(requireContext(), "이미지 업로드 완료!", Toast.LENGTH_SHORT).show()
//                } else {
//                    Log.e("AISecondFragment", "업로드 실패: ${response.errorBody()?.string()}")
//                }
//            }
//
//            override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
//                Log.e("AISecondFragment", "네트워크 오류 발생: ${t.message}")
//            }
//        })
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

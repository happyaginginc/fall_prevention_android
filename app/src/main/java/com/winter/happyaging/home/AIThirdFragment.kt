package com.winter.happyaging

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.winter.happyaging.databinding.FragmentAiSecondBinding
import com.winter.happyaging.databinding.FragmentAiThirdBinding
import com.winter.happyaging.home.RoomAdapter
import com.winter.happyaging.home.RoomData
import java.io.File
import java.io.FileOutputStream


class AIThirdFragment : Fragment(R.layout.fragment_ai_third) {

    private lateinit var roomAdapter: RoomAdapter
    private lateinit var roomList: MutableList<RoomData>
    private var _binding: FragmentAiThirdBinding? = null
    private val binding get() = _binding!!
    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAiThirdBinding.bind(view)
        binding.roomRecyclerView.layoutManager = LinearLayoutManager(context)

        roomList = mutableListOf(
            RoomData(name = "방 1")
        )

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                val uri = saveImageToUri(imageBitmap)
                Toast.makeText(context, "$uri", Toast.LENGTH_SHORT).show()
                val room = roomList[clickedPosition]

                when (clickedCameraNumber) {
                    1 -> room.imageUri1 = uri
                    2 -> room.imageUri2 = uri
                    3 -> room.imageUri3 = uri
                }

                roomAdapter.notifyItemChanged(clickedPosition)

                Toast.makeText(requireContext(), "사진 촬영이 완료", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(requireContext(), "사진 촬영이 취소되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        roomAdapter = RoomAdapter(
            roomList,
            onCameraClick = { position, cameraNumber ->
                clickedPosition = position
                clickedCameraNumber = cameraNumber
                if (ContextCompat.checkSelfPermission(requireContext(), "android.permission.CAMERA")
                    != PackageManager.PERMISSION_GRANTED) {
                    permissionLauncher.launch("android.permission.CAMERA")
                } else {
                    openCamera()
                }
            },
            onAddRoomClick = { position ->
                // 방 추가
                val newRoom = RoomData(name = "방 ${roomList.size + 1}")
                roomList.add(newRoom)
                roomAdapter.notifyItemInserted(roomList.size - 1)
            },
            onDeleteRoomClick = { position ->
                // 방 삭제
                roomList.removeAt(position)
                roomAdapter.notifyItemRemoved(position)
            }
        )

        // RecyclerView에 어댑터 설정
        binding.roomRecyclerView.adapter = roomAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            takePictureLauncher.launch(cameraIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "카메라 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveImageToUri(bitmap: Bitmap): Uri {
        val file = File(requireContext().cacheDir, "image_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        return Uri.fromFile(file) // Uri로 반환
    }
    companion object {
        private var clickedPosition: Int = 0
        private var clickedCameraNumber: Int = 0
    }

}

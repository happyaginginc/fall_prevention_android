package com.winter.happyaging.home

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.databinding.ItemRoomBinding

class RoomAdapter(
    private val roomList: MutableList<RoomData>,
    private val onCameraClick: (position: Int, cameraNumber: Int) -> Unit,
    private val onAddRoomClick: (position: Int) -> Unit,
    private val onDeleteRoomClick: (position: Int) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    inner class RoomViewHolder(private val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(room: RoomData, position: Int) {

            // 방 번호 및 이름 설정
            binding.roomNumber.text = (position + 1).toString()
            binding.roomName.setText(room.name)

            // 이미지 미리보기
            room.imageUri1?.let { binding.preview1.setImageURI(it) }
            room.imageUri2?.let { binding.preview2.setImageURI(it) }
            room.imageUri3?.let { binding.preview3.setImageURI(it) }


            if (position == 0) {
                binding.DeleteRoomButton.visibility = View.GONE
            } else {
                binding.DeleteRoomButton.visibility = View.VISIBLE
            }



            // 카메라 버튼
            binding.btnCamera1.setOnClickListener { onCameraClick(position, 1) }
            binding.btnCamera2.setOnClickListener { onCameraClick(position, 2) }
            binding.btnCamera3.setOnClickListener { onCameraClick(position, 3) }

            // 방 추가 및 삭제 버튼
            binding.AddRoomButton.setOnClickListener { onAddRoomClick(position) }
            binding.DeleteRoomButton.setOnClickListener { onDeleteRoomClick(position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(roomList[position], position)
    }


    override fun getItemCount(): Int = roomList.size
}

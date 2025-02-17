package com.winter.happyaging.ui.aiAnalysis.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.winter.happyaging.R
import com.winter.happyaging.data.aiAnalysis.model.RoomData
import com.winter.happyaging.databinding.ItemRoomBinding

class RoomAdapter(
    private val roomList: MutableList<RoomData>,
    private val onCameraClick: (position: Int, cameraNumber: Int) -> Unit,
    private val onAddRoomClick: (position: Int) -> Unit,
    private val onDeleteRoomClick: (position: Int) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    companion object {
        private const val TAG = "RoomAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(roomList[position], position)
    }

    override fun getItemCount(): Int = roomList.size

    inner class RoomViewHolder(private val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(room: RoomData, position: Int) {
            // 방 이름
            binding.roomName.setText(room.name)

            // 이미지 3개에 Glide로 미리보기
            Glide.with(binding.root)
                .load(room.imageUri1)
                .placeholder(R.drawable.preview)
                .into(binding.preview1)

            Glide.with(binding.root)
                .load(room.imageUri2)
                .placeholder(R.drawable.preview)
                .into(binding.preview2)

            Glide.with(binding.root)
                .load(room.imageUri3)
                .placeholder(R.drawable.preview)
                .into(binding.preview3)

            // 방이 하나뿐이면 삭제 버튼 숨기기
            binding.DeleteRoomButton.visibility = if (roomList.size > 1) View.VISIBLE else View.GONE

            // 카메라 버튼 리스너
            binding.btnCamera1.setOnClickListener {
                Log.d(TAG, "btnCamera1 클릭됨 - 방 번호: ${position + 1}")
                onCameraClick(position, 1)
            }
            binding.btnCamera2.setOnClickListener {
                Log.d(TAG, "btnCamera2 클릭됨 - 방 번호: ${position + 1}")
                onCameraClick(position, 2)
            }
            binding.btnCamera3.setOnClickListener {
                Log.d(TAG, "btnCamera3 클릭됨 - 방 번호: ${position + 1}")
                onCameraClick(position, 3)
            }

            // 방 추가 버튼
            binding.AddRoomButton.setOnClickListener {
                Log.d(TAG, "방 추가 버튼 클릭됨 - 현재 방 개수: ${roomList.size}")
                onAddRoomClick(position)
            }

            // 방 삭제 버튼
            binding.DeleteRoomButton.setOnClickListener {
                Log.d(TAG, "방 삭제 버튼 클릭됨 - 방 번호: ${position + 1}")
                if (roomList.size > 1) {
                    onDeleteRoomClick(position)
                }
            }
        }
    }
}
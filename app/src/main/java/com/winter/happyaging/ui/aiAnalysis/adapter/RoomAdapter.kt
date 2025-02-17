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

    inner class RoomViewHolder(private val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(room: RoomData, position: Int) {

            binding.roomName.setText(room.name)

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

            binding.DeleteRoomButton.visibility = if (roomList.size > 1) View.VISIBLE else View.GONE

            binding.btnCamera1.setOnClickListener {
                Log.d("RoomAdapter", "btnCamera1 클릭됨 - 방 번호: ${position + 1}")
                onCameraClick(position, 1)
            }
            binding.btnCamera2.setOnClickListener {
                Log.d("RoomAdapter", "btnCamera2 클릭됨 - 방 번호: ${position + 1}")
                onCameraClick(position, 2)
            }
            binding.btnCamera3.setOnClickListener {
                Log.d("RoomAdapter", "btnCamera3 클릭됨 - 방 번호: ${position + 1}")
                onCameraClick(position, 3)
            }

            binding.AddRoomButton.setOnClickListener {
                Log.d("RoomAdapter", "방 추가 버튼 클릭됨 - 현재 방 개수: ${roomList.size}")
                onAddRoomClick(position)
                notifyDataSetChanged()
            }

            binding.DeleteRoomButton.setOnClickListener {
                Log.d("RoomAdapter", "방 삭제 버튼 클릭됨 - 방 번호: ${position + 1}")
                if (roomList.size > 1) {
                    onDeleteRoomClick(position)
                    notifyDataSetChanged()
                }
            }
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

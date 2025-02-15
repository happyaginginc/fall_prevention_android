package com.winter.happyaging.ai

import android.net.Uri
import android.util.Log
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

            binding.roomNumber.text = (position + 1).toString()
            binding.roomName.setText(room.name)

            binding.preview1.setImageURI(room.imageUri1 ?: Uri.EMPTY)
            binding.preview2.setImageURI(room.imageUri2 ?: Uri.EMPTY)
            binding.preview3.setImageURI(room.imageUri3 ?: Uri.EMPTY)

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

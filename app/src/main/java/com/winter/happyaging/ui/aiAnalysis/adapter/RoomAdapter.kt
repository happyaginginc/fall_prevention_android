package com.winter.happyaging.ui.aiAnalysis.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R
import com.winter.happyaging.data.aiAnalysis.model.RoomData
import com.winter.happyaging.databinding.ItemRoomBinding

class RoomAdapter(
    private val roomList: MutableList<RoomData>,
    private val onAddImageClick: (roomPosition: Int, guideIndex: Int) -> Unit,
    private val onDeleteImageClick: (roomPosition: Int, guideIndex: Int, imagePosition: Int) -> Unit,
    private val onAddRoomClick: (position: Int) -> Unit,
    private val onDeleteRoomClick: (position: Int) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(roomList[position])
    }

    override fun getItemCount(): Int = roomList.size

    inner class RoomViewHolder(private val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(room: RoomData) {
            val currentPosition = bindingAdapterPosition.takeIf { it != RecyclerView.NO_POSITION } ?: return

            binding.roomName.setText(room.name)

            binding.rvGuide1.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = RoomImageAdapter(
                    imageList = room.guide1Images,
                    baseImageUrl = context.getString(R.string.base_image_url),
                    onAddClick = { onAddImageClick(currentPosition, 1) },
                    onDeleteClick = { imagePos -> onDeleteImageClick(currentPosition, 1, imagePos) }
                )
            }

            binding.rvGuide2.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = RoomImageAdapter(
                    imageList = room.guide2Images,
                    baseImageUrl = context.getString(R.string.base_image_url),
                    onAddClick = { onAddImageClick(currentPosition, 2) },
                    onDeleteClick = { imagePos -> onDeleteImageClick(currentPosition, 2, imagePos) }
                )
            }

            binding.rvGuide3.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = RoomImageAdapter(
                    imageList = room.guide3Images,
                    baseImageUrl = context.getString(R.string.base_image_url),
                    onAddClick = { onAddImageClick(currentPosition, 3) },
                    onDeleteClick = { imagePos -> onDeleteImageClick(currentPosition, 3, imagePos) }
                )
            }

            binding.AddRoomButton.setOnClickListener {
                Log.d("RoomAdapter", "방 추가 버튼 클릭됨 - 현재 방 개수: ${roomList.size}")
                onAddRoomClick(currentPosition)
            }
            binding.DeleteRoomButton.setOnClickListener {
                Log.d("RoomAdapter", "방 삭제 버튼 클릭됨 - 방 번호: ${currentPosition + 1}")
                if (roomList.size > 1) onDeleteRoomClick(currentPosition)
            }
        }
    }
}
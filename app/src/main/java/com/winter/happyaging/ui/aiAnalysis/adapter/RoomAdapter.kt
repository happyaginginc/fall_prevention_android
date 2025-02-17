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
    // 콜백: 각 가이드별 이미지 추가 시 (방 인덱스, 가이드 인덱스)
    private val onAddImageClick: (roomPosition: Int, guideIndex: Int) -> Unit,
    // 콜백: 각 가이드별 이미지 삭제 시 (방 인덱스, 가이드 인덱스, imagePosition)
    private val onDeleteImageClick: (roomPosition: Int, guideIndex: Int, imagePosition: Int) -> Unit,
    // 방 추가 및 삭제
    private val onAddRoomClick: (position: Int) -> Unit,
    private val onDeleteRoomClick: (position: Int) -> Unit
) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    companion object {
        private const val TAG = "RoomAdapter"
        // BASE_IMAGE_URL는 RoomImageAdapter 내부에서 사용하므로 RoomAdapter는 단순 전달
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
            binding.roomName.setText(room.name)

            // 설정: 각 가이드별 RecyclerView 초기화
            // (가이드1: 방문이 있는 벽면에서…, 가이드2: 방문과 마주보는 벽면에서…, 가이드3: 방 밖에서 방문을 향해서)
            binding.rvGuide1.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = RoomImageAdapter(
                    imageList = room.guide1Images,
                    baseImageUrl = context.getString(R.string.base_image_url),
                    onAddClick = { onAddImageClick(adapterPosition, 1) },
                    onDeleteClick = { imagePos -> onDeleteImageClick(adapterPosition, 1, imagePos) }
                )
            }
            binding.rvGuide2.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = RoomImageAdapter(
                    imageList = room.guide2Images,
                    baseImageUrl = context.getString(R.string.base_image_url),
                    onAddClick = { onAddImageClick(adapterPosition, 2) },
                    onDeleteClick = { imagePos -> onDeleteImageClick(adapterPosition, 2, imagePos) }
                )
            }
            binding.rvGuide3.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = RoomImageAdapter(
                    imageList = room.guide3Images,
                    baseImageUrl = context.getString(R.string.base_image_url),
                    onAddClick = { onAddImageClick(adapterPosition, 3) },
                    onDeleteClick = { imagePos -> onDeleteImageClick(adapterPosition, 3, imagePos) }
                )
            }

            // 방 추가/삭제 버튼
            binding.AddRoomButton.setOnClickListener {
                Log.d(TAG, "방 추가 버튼 클릭됨 - 현재 방 개수: ${roomList.size}")
                onAddRoomClick(position)
            }
            binding.DeleteRoomButton.setOnClickListener {
                Log.d(TAG, "방 삭제 버튼 클릭됨 - 방 번호: ${position + 1}")
                if (roomList.size > 1) onDeleteRoomClick(position)
            }
        }
    }
}
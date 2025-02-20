package com.winter.happyaging.ui.aiAnalysis.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.R
import com.winter.happyaging.data.aiAnalysis.model.RoomData
import com.winter.happyaging.databinding.ItemRoomBinding

class RoomAdapter(
    private val roomList: MutableList<RoomData>,
    private val guideText1: String,
    private val guideText2: String,
    private val guideText3: String,
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
        holder.bind(roomList[position], position)
    }

    override fun getItemCount(): Int = roomList.size

    inner class RoomViewHolder(private val binding: ItemRoomBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(room: RoomData, position: Int) {
            binding.apply {
                roomNumberText.text = "${position + 1}"
                roomHint.text = "미작성 시 '${room.name}'로 표기됩니다."
                roomName.setText("")
                roomNameLayout.hint = room.name

                // doAfterTextChanged를 이용해 중복 TextWatcher 등록을 방지
                roomName.doAfterTextChanged { text ->
                    val newName = text?.toString()?.trim()
                    room.name = if (newName.isNullOrEmpty()) room.name else newName
                }

                tvGuide1.text = guideText1
                tvGuide2.text = guideText2
                tvGuide3.text = guideText3

                // 각 가이드 이미지 RecyclerView 설정
                rvGuide1.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = RoomImageAdapter(
                        imageList = room.guide1Images,
                        baseImageUrl = context.getString(R.string.base_image_url),
                        onAddClick = { onAddImageClick(position, 1) },
                        onDeleteClick = { imagePos -> onDeleteImageClick(position, 1, imagePos) }
                    )
                }
                rvGuide2.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = RoomImageAdapter(
                        imageList = room.guide2Images,
                        baseImageUrl = context.getString(R.string.base_image_url),
                        onAddClick = { onAddImageClick(position, 2) },
                        onDeleteClick = { imagePos -> onDeleteImageClick(position, 2, imagePos) }
                    )
                }
                rvGuide3.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = RoomImageAdapter(
                        imageList = room.guide3Images,
                        baseImageUrl = context.getString(R.string.base_image_url),
                        onAddClick = { onAddImageClick(position, 3) },
                        onDeleteClick = { imagePos -> onDeleteImageClick(position, 3, imagePos) }
                    )
                }

                // 방 추가 및 삭제 버튼
                AddRoomButton.setOnClickListener { onAddRoomClick(position) }
                DeleteRoomButton.visibility = if (roomList.size > 1) View.VISIBLE else View.GONE
                DeleteRoomButton.setOnClickListener { onDeleteRoomClick(position) }
            }
        }
    }
}
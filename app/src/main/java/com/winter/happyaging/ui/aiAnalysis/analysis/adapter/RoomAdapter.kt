package com.winter.happyaging.ui.aiAnalysis.analysis.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.winter.happyaging.data.aiAnalysis.model.RoomData
import com.winter.happyaging.databinding.ItemRoomBinding

class RoomAdapter(
    private val roomList: MutableList<RoomData>,
    private val onAddImageClick: (roomPosition: Int, guidePosition: Int) -> Unit,
    private val onDeleteImageClick: (roomPosition: Int, guidePosition: Int, imagePosition: Int) -> Unit,
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
            binding.roomNumberText.text = "${position + 1}"
            binding.roomHint.text = "미작성 시 '${room.name}'로 표기됩니다."
            binding.roomName.setText("")
            binding.roomNameLayout.hint = room.name

            binding.roomName.doAfterTextChanged { text ->
                text?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.let { room.name = it }
            }

            binding.rvGuides.layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
            binding.rvGuides.adapter = GuideAdapter(
                roomIndex = position,
                guides = room.guides,
                onAddImageClick = onAddImageClick,
                onDeleteImageClick = onDeleteImageClick
            )
            binding.AddRoomButton.setOnClickListener { onAddRoomClick(position) }
            binding.DeleteRoomButton.visibility = if (roomList.size > 1) View.VISIBLE else View.GONE
            binding.DeleteRoomButton.setOnClickListener { onDeleteRoomClick(position) }
        }
    }
}
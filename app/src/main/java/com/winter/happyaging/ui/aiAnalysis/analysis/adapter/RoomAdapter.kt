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
            binding.apply {
                roomNumberText.text = "${position + 1}"
                roomHint.text = "미작성 시 '${room.name}'로 표기됩니다."
                roomName.setText("")
                roomNameLayout.hint = room.name

                roomName.doAfterTextChanged { text ->
                    val newName = text?.toString()?.trim()
                    if (!newName.isNullOrEmpty()) {
                        room.name = newName
                    }
                }

                rvGuides.layoutManager = LinearLayoutManager(root.context, LinearLayoutManager.VERTICAL, false)
                val guideAdapter = GuideAdapter(
                    roomIndex = position,
                    guides = room.guides,
                    onAddImageClick = onAddImageClick,
                    onDeleteImageClick = onDeleteImageClick
                )
                rvGuides.adapter = guideAdapter

                AddRoomButton.setOnClickListener { onAddRoomClick(position) }
                DeleteRoomButton.visibility = if (roomList.size > 1) View.VISIBLE else View.GONE
                DeleteRoomButton.setOnClickListener { onDeleteRoomClick(position) }
            }
        }
    }
}
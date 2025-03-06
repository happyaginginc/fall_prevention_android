package com.winter.happyaging.ui.aiAnalysis.result.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.winter.happyaging.R
import com.winter.happyaging.ui.aiAnalysis.result.ImagePopupDialog

class ImageListAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<ImageListAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val url = imageUrls[position]
        Glide.with(holder.imageView.context)
            .load(url)
            .placeholder(R.drawable.logo)
            .error(R.drawable.logo)
            .into(holder.imageView)
        holder.imageView.setOnClickListener { ImagePopupDialog(it.context, url).show() }
    }

    override fun getItemCount(): Int = imageUrls.size
}
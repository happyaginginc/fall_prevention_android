package com.winter.happyaging.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.winter.happyaging.data.product.model.ProductResponse
import com.winter.happyaging.databinding.ItemProductBinding

class ProductAdapter(private var productList: List<ProductResponse>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductResponse) {
            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = "₩${product.price}"
            binding.tvProductDescription.text = product.description

            // Glide를 사용하여 이미지 로딩
            Glide.with(binding.ivProduct.context)
                .load(product.imageUrl)
                .into(binding.ivProduct)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size

    fun updateData(newList: List<ProductResponse>) {
        productList = newList
        notifyDataSetChanged()
    }
}
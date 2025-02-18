package com.winter.happyaging.ui.home.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.winter.happyaging.R
import com.winter.happyaging.data.product.model.ProductResponse
import com.winter.happyaging.data.product.service.ProductService
import com.winter.happyaging.databinding.FragmentPreventionGoodsBinding
import com.winter.happyaging.network.RetrofitClient
import com.winter.happyaging.network.model.ApiResponse
import com.winter.happyaging.ui.home.adapter.ProductAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PreventionGoodsFragment : Fragment() {

    private var _binding: FragmentPreventionGoodsBinding? = null
    private val binding get() = _binding!!

    // 전체 상품 리스트 저장
    private var fullProductList: List<ProductResponse> = emptyList()
    private val productAdapter = ProductAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPreventionGoodsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.btnBack)?.visibility = View.GONE
        val headerTitle: TextView = view.findViewById(R.id.tvHeader)
        headerTitle.text = "낙상 예방 물품"

        // RecyclerView 설정
        binding.recyclerProduct.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productAdapter
        }

        // 검색어 입력 시 텍스트 변화 감지
        binding.edtSearchProduct.addTextChangedListener { text ->
            val query = text.toString().trim()
            filterProductList(query)
        }

        fetchProductData()
    }

    // 서버에서 상품 데이터 받아오기
    private fun fetchProductData() {
        val productService =
            RetrofitClient.getInstance(requireContext()).create(ProductService::class.java)
        productService.getProductList().enqueue(object : Callback<ApiResponse<List<ProductResponse>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<ProductResponse>>>,
                response: Response<ApiResponse<List<ProductResponse>>>,
            ) {
                if (response.isSuccessful) {
                    fullProductList = response.body()?.data.orEmpty()
                    Log.d("PreventionGoodsFragment", "받아온 상품 리스트: $fullProductList")
                    // 현재 검색어에 따라 필터링 적용
                    val currentQuery = binding.edtSearchProduct.text.toString().trim()
                    filterProductList(currentQuery)
                } else {
                    Log.e("PreventionGoodsFragment", "API 호출 실패 - 상태 코드: ${response.code()}")
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<ProductResponse>>>, t: Throwable) {
                Log.e("PreventionGoodsFragment", "네트워크 오류: ${t.message}")
            }
        })
    }

    // 검색어에 따른 필터링
    private fun filterProductList(query: String) {
        val filteredList = if (query.isEmpty()) {
            fullProductList
        } else {
            fullProductList.filter { it.name.contains(query, ignoreCase = true) }
        }
        productAdapter.updateData(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.winter.happyaging.R
import com.winter.happyaging.data.product.model.ProductResponse
import com.winter.happyaging.databinding.FragmentPreventionGoodsBinding
import com.winter.happyaging.ui.home.adapter.ProductAdapter

class PreventionGoodsFragment : Fragment() {

    private var _binding: FragmentPreventionGoodsBinding? = null
    private val binding get() = _binding!!

    // ViewModel 사용
    private val productViewModel: ProductViewModel by viewModels()
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

        // SwipeRefreshLayout 설정
        binding.swipeRefreshLayout.setOnRefreshListener {
            productViewModel.refreshProductList()
        }

        // 검색어 입력 시 캐시된 데이터 필터링
        binding.edtSearchProduct.addTextChangedListener { text ->
            filterProductList(text.toString().trim(), productViewModel.productList.value)
        }

        // ViewModel LiveData 구독
        productViewModel.productList.observe(viewLifecycleOwner) { products ->
            filterProductList(binding.edtSearchProduct.text.toString().trim(), products)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        // 처음 데이터 로드
        productViewModel.fetchProductList()
    }

    private fun filterProductList(query: String, fullList: List<ProductResponse>?) {
        val filteredList = if (fullList == null) {
            emptyList()
        } else {
            if (query.isEmpty()) fullList
            else fullList.filter { it.name.contains(query, ignoreCase = true) }
        }
        productAdapter.updateData(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.winter.happyaging.ui.home.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.winter.happyaging.R
import com.winter.happyaging.data.product.model.ProductResponse
import com.winter.happyaging.databinding.FragmentPreventionGoodsBinding
import com.winter.happyaging.ui.home.product.adapter.ProductAdapter

class PreventionGoodsFragment : Fragment() {

    private var _binding: FragmentPreventionGoodsBinding? = null
    private val binding get() = _binding!!

    // 정렬 옵션을 정의한 enum
    private enum class SortType {
        DEFAULT,    // 기본 순 (API 에서 받은 순서)
        NAME_ASC,   // 이름 순 (올림)
        NAME_DESC,  // 이름 순 (내림)
        PRICE_ASC,  // 가격 순 (올림)
        PRICE_DESC  // 가격 순 (내림)
    }
    private var sortType: SortType = SortType.DEFAULT

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

        // Spinner 정렬 옵션 설정
        val sortOptions = listOf("기본 순", "이름 순 (올림)", "이름 순 (내림)", "가격 순 (올림)", "가격 순 (내림)")
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, sortOptions)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerSort.adapter = spinnerAdapter

        binding.spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                sortType = when (position) {
                    0 -> SortType.DEFAULT
                    1 -> SortType.NAME_ASC
                    2 -> SortType.NAME_DESC
                    3 -> SortType.PRICE_ASC
                    4 -> SortType.PRICE_DESC
                    else -> SortType.DEFAULT
                }
                // 현재 검색어와 정렬 옵션을 적용해 리스트 갱신
                filterProductList(binding.edtSearchProduct.text.toString().trim(), productViewModel.productList.value)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // 검색어 입력 시 필터링 및 정렬 적용
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
        val sortedList = when (sortType) {
            SortType.NAME_ASC -> filteredList.sortedBy { it.name }
            SortType.NAME_DESC -> filteredList.sortedByDescending { it.name }
            SortType.PRICE_ASC -> filteredList.sortedBy { it.price }
            SortType.PRICE_DESC -> filteredList.sortedByDescending { it.price }
            else -> filteredList
        }
        productAdapter.updateData(sortedList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
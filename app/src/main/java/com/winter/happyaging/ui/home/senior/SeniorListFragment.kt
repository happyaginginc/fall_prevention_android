package com.winter.happyaging.ui.home.senior

import android.os.Bundle
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
import com.winter.happyaging.data.senior.model.SeniorReadResponse
import com.winter.happyaging.databinding.FragmentSeniorListBinding
import com.winter.happyaging.ui.home.senior.adapter.SeniorAdapter

class SeniorListFragment : Fragment() {

    private var _binding: FragmentSeniorListBinding? = null
    private val binding get() = _binding!!

    private val seniorViewModel: SeniorViewModel by viewModels()

    private val seniorAdapter = SeniorAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSeniorListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.btnBack)?.visibility = View.GONE
        val headerTitle: TextView = view.findViewById(R.id.tvHeader)
        headerTitle.text = "낙상 위험 분석 시작하기"

        binding.recyclerSenior.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = seniorAdapter
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            seniorViewModel.refreshSeniorList()
        }

        binding.edtSearchSenior.addTextChangedListener { text ->
            filterSeniorList(text.toString().trim(), seniorViewModel.seniorList.value)
        }

        seniorViewModel.seniorList.observe(viewLifecycleOwner) { seniors ->
            filterSeniorList(binding.edtSearchSenior.text.toString().trim(), seniors)
            binding.swipeRefreshLayout.isRefreshing = false
        }
        
        parentFragmentManager.setFragmentResultListener("refreshSeniorList", this) { _, _ ->
            seniorViewModel.refreshSeniorList()
        }
        
        seniorViewModel.fetchSeniorList()

        binding.btnRegisterSenior.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RegisterSeniorFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    // 검색어와 전체 리스트를 이용하여 필터링
    private fun filterSeniorList(query: String, fullList: List<SeniorReadResponse>?) {
        val filteredList = if (fullList == null) {
            emptyList()
        } else {
            if (query.isEmpty()) fullList
            else fullList.filter { it.name.contains(query, ignoreCase = true) }
        }
        seniorAdapter.updateData(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.winter.happyaging.ui.home.knowledge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.winter.happyaging.R
import com.winter.happyaging.data.youtube.model.YoutubeVideo
import com.winter.happyaging.databinding.FragmentKnowledgeBinding

class KnowledgeFragment : Fragment() {

    private var _binding: FragmentKnowledgeBinding? = null
    private val binding get() = _binding!!
    private val videoAdapter = VideoAdapter(emptyList())
    private var fullVideoList: List<YoutubeVideo> = emptyList()  // 전체 데이터 저장

    private val knowledgeViewModel: KnowledgeViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentKnowledgeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.btnBack)?.visibility = View.GONE
        binding.header.tvHeader.text = "관련 지식"
        binding.recyclerVideos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerVideos.adapter = videoAdapter

        binding.edtSearchVideo.addTextChangedListener { text ->
            filterVideos(text.toString())
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            knowledgeViewModel.fetchVideos(isRefresh = true)
        }

        // 전체 데이터 업데이트 후 검색어 적용
        knowledgeViewModel.videoList.observe(viewLifecycleOwner) { videos ->
            fullVideoList = videos
            filterVideos(binding.edtSearchVideo.text.toString())
            binding.swipeRefreshLayout.isRefreshing = false
        }

        knowledgeViewModel.fetchVideos()
    }

    private fun filterVideos(query: String) {
        val filteredList = if (query.isEmpty()) {
            fullVideoList
        } else {
            fullVideoList.filter { it.title.contains(query, ignoreCase = true) }
        }
        videoAdapter.updateData(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
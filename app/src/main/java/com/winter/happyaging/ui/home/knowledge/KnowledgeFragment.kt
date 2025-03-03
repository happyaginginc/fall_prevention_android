package com.winter.happyaging.ui.home.knowledge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.winter.happyaging.data.video.VideoItem
import com.winter.happyaging.databinding.FragmentKnowledgeBinding

class KnowledgeFragment : Fragment() {

    private var _binding: FragmentKnowledgeBinding? = null
    private val binding get() = _binding!!
    private val videoAdapter = VideoAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKnowledgeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 헤더 제목 변경 (fragment_more와 유사한 header 활용)
        binding.header.tvHeader.text = "관련 지식"

        // RecyclerView 설정
        binding.recyclerVideos.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerVideos.adapter = videoAdapter

        // 검색 기능 구현
        binding.edtSearchVideo.addTextChangedListener { text ->
            filterVideos(text.toString())
        }

        // 유튜브 영상 목록 불러오기 (여기서는 더미 데이터 사용)
        fetchVideos()
    }

    private fun fetchVideos() {
        // 실제 구현에서는 YouTube Data API 등을 통해 영상 목록을 받아오면 됩니다.
        val dummyVideos = listOf(
            VideoItem("건강한 노후를 위한 운동법", "https://img.youtube.com/vi/VIDEO_ID_1/0.jpg", "https://www.youtube.com/watch?v=VIDEO_ID_1"),
            VideoItem("노인을 위한 영양식단 소개", "https://img.youtube.com/vi/VIDEO_ID_2/0.jpg", "https://www.youtube.com/watch?v=VIDEO_ID_2"),
            VideoItem("행복한 노후 생활 팁", "https://img.youtube.com/vi/VIDEO_ID_3/0.jpg", "https://www.youtube.com/watch?v=VIDEO_ID_3")
        )
        videoAdapter.updateData(dummyVideos)
    }

    private fun filterVideos(query: String) {
        val filteredList = videoAdapter.currentList.filter {
            it.title.contains(query, ignoreCase = true)
        }
        videoAdapter.updateData(filteredList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
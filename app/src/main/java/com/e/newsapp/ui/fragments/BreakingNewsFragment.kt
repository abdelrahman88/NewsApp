package com.e.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.newsapp.R
import com.e.newsapp.databinding.FragmentBreakingNewsBinding
import com.e.newsapp.ui.NewsViewModel
import com.e.newsapp.ui.adapters.NewsAdapter
import com.e.newsapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.e.newsapp.utils.NewsEvent
import com.e.newsapp.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BreakingNewsFragment : Fragment() {

    private val TAG = "BreakingNewsFragment"

    @Inject
    lateinit var newsAdapter: NewsAdapter
    private val newsViewModel: NewsViewModel by viewModels()
    private var _binding: FragmentBreakingNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putParcelable("article", it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment, bundle
            )
        }
        lifecycleScope.launchWhenCreated {
            newsViewModel.getBreakingNews("us")
            newsViewModel.news.observe(viewLifecycleOwner, { newsEvent ->
                when (newsEvent) {
                    is NewsEvent.Success -> {
                        isProgressBarVisible(false)
                        //"DiffUtil, ListAdapter, and AsyncListDiffer require the list to not mutate while in use"
                        // so we add .toList() here
                        newsAdapter.differ.submitList(newsEvent.response.articles.toList())
                        // we need to add 2 here because
                        // 1- we have an integer division here that is always rounded off
                        // 2- the last page of our response will always be empty and we don't
                        // really to consider that so
                        val totalPages = newsEvent.response.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = newsViewModel.breakingNewsPage == totalPages
                        if(isLastPage){
                            binding.rvBreakingNews.setPadding(0,0,0,0)
                        }
                    }
                    is NewsEvent.Failure -> {
                        isProgressBarVisible(false)
                        Log.e(TAG, newsEvent.errorMessage)
                    }
                    NewsEvent.Loading -> {
                        isProgressBarVisible(true)
                    }
                }
            })
        }
    }

    private fun isProgressBarVisible(isVisible :Boolean){
        binding.paginationProgressBar.visible(isVisible)
        isLoading = isVisible
    }

    var isScrolling = false
    var isLastPage = false
    var isLoading = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling =true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

            val shouldPaginate = isAtLastItem && isNotLoadingAndNotLastPage
                    && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){
                newsViewModel.getBreakingNews("us")
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
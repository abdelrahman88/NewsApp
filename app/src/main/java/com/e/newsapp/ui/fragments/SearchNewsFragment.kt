package com.e.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.newsapp.R
import com.e.newsapp.databinding.FragmentSearchNewsBinding
import com.e.newsapp.ui.NewsViewModel
import com.e.newsapp.ui.adapters.NewsAdapter
import com.e.newsapp.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.e.newsapp.utils.Constants.Companion.SEARCH_NEWS_DELAY_TIME
import com.e.newsapp.utils.NewsEvent
import com.e.newsapp.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchNewsFragment : Fragment() {

    private val TAG = "SearchNewsFragment"
    private val newsViewModel : NewsViewModel by viewModels()
    @Inject
    lateinit var newsAdapter: NewsAdapter
    private var _binding: FragmentSearchNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchNewsBinding.inflate(inflater , container ,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putParcelable("article" , it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment , bundle
            )
        }
        var job : Job ? = null
        binding.etSearch.addTextChangedListener{ editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_DELAY_TIME)
                editable?.let {
                    if(it.toString().isNotEmpty()){
                        newsViewModel.searchForNews(it.toString() )
                    }
                }
            }
        }

        newsViewModel.searchNews.observe(viewLifecycleOwner, { newsEvent ->
            when (newsEvent) {
                is NewsEvent.Success -> {
                    isProgressBarVisible(false)
                    newsAdapter.differ.submitList(newsEvent.response.articles.toList())
                    val totalPages = newsEvent.response.totalResults / QUERY_PAGE_SIZE + 2
                    isLastPage = newsViewModel.searchNewsPage == totalPages
                    if(isLastPage){
                        binding.rvSearchNews.setPadding(0,0,0,0)
                    }
                }
                is NewsEvent.Failure -> {
                    isProgressBarVisible(false)
                    Log.e(TAG , "Error")
                }
                NewsEvent.Loading -> {
                    isProgressBarVisible(true)
                }
            }
        })
    }

    private fun isProgressBarVisible(isVisible :Boolean){
        binding.paginationProgressBar.visible(isVisible)
        isLoading = isVisible
    }

    var isLoading = false
    var isScrolling = false
    var isLastPage  = false

    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val isNotLoadingAndNotLastPage = !isLoading && ! isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= QUERY_PAGE_SIZE

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling
            if(shouldPaginate){
                newsViewModel.searchForNews(binding.etSearch.text.toString())
                isScrolling = false
            }
        }
    }
    private fun setupRecyclerView() {
        binding.rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.scrollListener)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
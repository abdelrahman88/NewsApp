package com.e.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.e.newsapp.R
import com.e.newsapp.data.models.Article
import com.e.newsapp.databinding.FragmentSavedNewsBinding
import com.e.newsapp.ui.NewsViewModel
import com.e.newsapp.ui.adapters.NewsAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SavedNewsFragment :Fragment() {
    private var _binding :FragmentSavedNewsBinding? = null
    private val binding get() = _binding!!
  @Inject
    lateinit var newsAdapter: NewsAdapter
    private val newsViewModel : NewsViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedNewsBinding.inflate(inflater ,container ,false)
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
                R.id.action_savedNewsFragment_to_articleFragment , bundle
            )
        }
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN
        ,ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        )
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                newsViewModel.deleteArticle( article)
                Snackbar.make(view , "Article deleted successfully " , Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        newsViewModel.saveArticle(article)
                    }
                    show()
                }
            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }
        newsViewModel.getSavedNews().observe(viewLifecycleOwner , { articles ->
            newsAdapter.differ.submitList(articles)
        })
    }

    private fun setupRecyclerView(){
        binding.rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
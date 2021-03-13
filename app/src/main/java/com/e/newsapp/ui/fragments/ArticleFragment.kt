package com.e.newsapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.e.newsapp.data.models.Article
import com.e.newsapp.databinding.FragmentArticleBinding
import com.e.newsapp.ui.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleFragment : Fragment() {

    val newsViewModel : NewsViewModel by viewModels()
    private var _binding  : FragmentArticleBinding? = null
    private val binding get() = _binding!!
    val args : ArticleFragmentArgs by navArgs()
    lateinit var article: Article
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArticleBinding.inflate(inflater , container ,false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // without generated argument class
//        article = requireArguments().getParcelable("article")!!

        article = args.article
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

        binding.fab.setOnClickListener {
            newsViewModel.saveArticle(article)
            Snackbar.make(it , "Article Saved Successfully " ,Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
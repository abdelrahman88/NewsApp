package com.e.newsapp.data.repository

import androidx.lifecycle.LiveData
import com.e.newsapp.data.models.Article
import com.e.newsapp.data.models.NewsResponse
import com.e.newsapp.utils.Resource

interface NewsRepository {
    suspend fun getBreakingNews(country : String , page : Int ): Resource<NewsResponse>
    suspend fun searchForNews(searchWord : String , page : Int ): Resource<NewsResponse>
    suspend fun upsert(article: Article) : Long
    fun getSavedArticles(): LiveData<List<Article>>
    suspend fun deleteArticle(article: Article)
}
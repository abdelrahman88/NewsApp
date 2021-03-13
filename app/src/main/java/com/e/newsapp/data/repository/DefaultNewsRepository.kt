package com.e.newsapp.data.repository

import androidx.lifecycle.LiveData
import com.e.newsapp.data.db.database.ArticleDatabase
import com.e.newsapp.data.models.Article
import com.e.newsapp.data.models.NewsResponse
import com.e.newsapp.data.remote.api.NewsApi
import com.e.newsapp.utils.Resource
import java.lang.Exception
import javax.inject.Inject

class DefaultNewsRepository @Inject constructor(private val api :NewsApi, val db : ArticleDatabase) : NewsRepository {
    init {
        System.loadLibrary("Keys")
    }

    //To declare a function that is implemented in native (C or C++) code, you need to mark it with the external modifier:
    private external fun getApiKey():String

    override suspend fun getBreakingNews(
        country: String,
        page: Int
    ): Resource<NewsResponse> {
        try {
            val response = api.getBreakingNews(country , page , getApiKey())
            val data = response.body()
            if(response.isSuccessful){
                data?.let {
                   return Resource.Success(data)
                }
            }
            return    Resource.Error("Error Occurred" , null)
        }
        catch (error : Exception){
          return  Resource.Error("Error Occurred + ${error.message}" , null)
        }
    }

    override suspend fun searchForNews(searchWord: String, page: Int): Resource<NewsResponse> {
        try {
            val response = api.searchForNews(searchWord,page , getApiKey())
            val data = response.body()
            if(response.isSuccessful){
               data?.let {
                    return Resource.Success(data)
                }
            }
            return Resource.Error("Error Occurred" , data)
        }
        catch (e: Exception){
            return Resource.Error("Error Occurred : ${e.message}" , null)
        }
    }

    override suspend fun upsert(article: Article): Long {
        return db.getArticleDao().upsert(article)
    }

    override fun getSavedArticles(): LiveData<List<Article>> {
        return db.getArticleDao().getAllArticles()
    }

    override suspend fun deleteArticle(article: Article) {
         db.getArticleDao().deleteArticle(article)
    }
}
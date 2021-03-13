package com.e.newsapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.e.newsapp.data.models.Article
import com.e.newsapp.data.models.NewsResponse
import com.e.newsapp.data.repository.NewsRepository
import com.e.newsapp.utils.DispatcherProvider
import com.e.newsapp.utils.NewsEvent
import com.e.newsapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository,
    private val dispatchers : DispatcherProvider) : ViewModel() {

    private val _news = MutableLiveData<NewsEvent>()
    val news = _news
    var breakingNewsResponse: NewsResponse ? = null
    var breakingNewsPage = 1

    private val _searchNews = MutableLiveData<NewsEvent>()
    val searchNews = _searchNews
    var searchNewsResponse: NewsResponse ? = null
    var searchNewsPage = 1


    fun getBreakingNews(country : String ){
        viewModelScope.launch(dispatchers.io) {
            _news.postValue(NewsEvent.Loading)
            when(val newsResponse = repository.getBreakingNews(country ,breakingNewsPage )){
                is Resource.Success -> {
                    newsResponse.data?.let { resultResponse ->
                        breakingNewsPage++
                        if(breakingNewsResponse == null){
                            breakingNewsResponse = resultResponse
                        }
                        else {
                            val oldArticles = breakingNewsResponse?.articles
                            val newArticles = resultResponse.articles
                            oldArticles?.addAll(newArticles)
                        }
                        _news.postValue(NewsEvent.Success(breakingNewsResponse ?: resultResponse))
                    } ?: _news.postValue(NewsEvent.Failure("Unexpected Error"))
                }
                is Resource.Error -> {
                    _news.postValue(NewsEvent.Failure(newsResponse.message!!))
                }
            }
        }
    }

    fun searchForNews(searchWord : String ){
        viewModelScope.launch(dispatchers.io){
            _searchNews.postValue(NewsEvent.Loading)
            when(val response =  repository.searchForNews(searchWord , searchNewsPage)){
                is Resource.Success -> {
                    response.data?.let { resultResponse ->
                        searchNewsPage++
                        if(searchNewsResponse == null){
                            searchNewsResponse = resultResponse
                        }
                        else {
                            val oldArticles = searchNewsResponse?.articles
                            val newArticles = resultResponse.articles
                            oldArticles?.addAll(newArticles)
                        }
                        _searchNews.postValue(NewsEvent.Success(searchNewsResponse ?: resultResponse))
                    } ?: _searchNews.postValue(NewsEvent.Failure("Unexpected Error"))
                }
                is Resource.Error -> {
                    _searchNews.postValue(NewsEvent.Failure(response.message!!))
                }
            }
        }
    }

    fun getSavedNews()= repository.getSavedArticles()

    fun deleteArticle(article: Article) {
        viewModelScope.launch(dispatchers.io){
            repository.deleteArticle(article)
        }
    }

    fun saveArticle(article : Article){
        viewModelScope.launch(dispatchers.io){
            repository.upsert(article)
        }
    }

}
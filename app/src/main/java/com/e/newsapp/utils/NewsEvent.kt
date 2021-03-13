package com.e.newsapp.utils

import com.e.newsapp.data.models.NewsResponse

sealed class NewsEvent{
    class Success(val response: NewsResponse) : NewsEvent()
    class Failure(val errorMessage : String) : NewsEvent()
    object Loading : NewsEvent()
}

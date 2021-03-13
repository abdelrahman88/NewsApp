package com.e.newsapp.di

import com.e.newsapp.data.remote.api.NewsApi
import com.e.newsapp.ui.adapters.NewsAdapter
import com.e.newsapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    fun provideNewsAdapter() = NewsAdapter()
}
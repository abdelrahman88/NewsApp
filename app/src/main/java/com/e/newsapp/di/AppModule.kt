package com.e.newsapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.e.newsapp.data.db.database.ArticleDatabase
import com.e.newsapp.data.remote.api.NewsApi
import com.e.newsapp.data.repository.DefaultNewsRepository
import com.e.newsapp.data.repository.NewsRepository
import com.e.newsapp.utils.Constants
import com.e.newsapp.utils.Constants.Companion.ARTICLE_DATABASE_NAME
import com.e.newsapp.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance(): NewsApi {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofitInstance = Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return  retrofitInstance.create(NewsApi::class.java)
    }

    @Singleton
    @Provides
    fun provideArticleDataBase(@ApplicationContext appContext: Context) = Room.databaseBuilder(
            appContext , ArticleDatabase::class.java , ARTICLE_DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideArticleDao(db : ArticleDatabase) = db.getArticleDao()

    @Singleton
    @Provides
    fun provideDispatchers() : DispatcherProvider{
        return object :DispatcherProvider{
            override val main: CoroutineDispatcher
                get() = Dispatchers.Main
            override val io: CoroutineDispatcher
                get() = Dispatchers.IO
            override val default: CoroutineDispatcher
                get() = Dispatchers.Default
        }
    }

    @Singleton
    @Provides
    fun provideNewsRepository(newsApi : NewsApi , db: ArticleDatabase ) : NewsRepository{
        return  DefaultNewsRepository(newsApi , db)
    }
}
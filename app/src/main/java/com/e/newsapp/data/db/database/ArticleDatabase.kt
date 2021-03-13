package com.e.newsapp.data.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.e.newsapp.data.db.converters.Converters
import com.e.newsapp.data.db.dao.ArticleDao
import com.e.newsapp.data.models.Article

@Database(entities = [Article::class] , version = 1)
@TypeConverters(Converters::class)
abstract class ArticleDatabase :RoomDatabase() {
    abstract fun getArticleDao () : ArticleDao

/*
    // Without Dagger we need to do the following
    companion object{
        // Volatile means that other threads can immediately see when a thread change
        //this instance
        // or in other words volatile`, meaning that writes to this field
        //  are immediately made visible to other threads.
        @Volatile
        private var instance : ArticleDatabase? = null
        private val lock = Any()

        //operator marks a function as overloading an operator or implementing a convention
        // for more info about operator visit the following link
        //https://kotlinlang.org/docs/operator-overloading.html
        // invoke fun is called whenever we create an instance of our database
        operator fun invoke(context: Context) = instance ?: synchronized(lock){
            // every thing that happens in this synchronized block of code
            // can not be accessed by other threads at the same time
            instance ?: createDataBase(context).also { articleDatabase ->
                instance = articleDatabase
            }
        }

        private fun createDataBase(context: Context): ArticleDatabase {
            return Room.databaseBuilder(
                    context , ArticleDatabase::class.java , ARTICLE_DATABASE_NAME).build()
        }
    }
*/
}
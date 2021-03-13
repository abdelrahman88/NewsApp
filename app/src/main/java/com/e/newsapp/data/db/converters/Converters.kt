package com.e.newsapp.data.db.converters

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.e.newsapp.data.models.Source

class Converters {

    @TypeConverter
    fun fromSourceToString(source: Source):String{
        return source.name
    }

    @TypeConverter
    fun fromStringToSource(name :String) :Source{
        return Source(name , name)
    }
}
package com.maxim.newsapp.data;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomDatabase.Callback;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.maxim.newsapp.data.dao.HeadlinesDao;
import com.maxim.newsapp.model.Article;

@Database(entities = {Article.class},
        version = 1,
        exportSchema = false)
@TypeConverters(DatabaseConverters.class)
public abstract class NewsDatabase extends RoomDatabase {
    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "news";
    private static NewsDatabase sInstance;

    public static NewsDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = Room.databaseBuilder(
                        context.getApplicationContext(),
                        NewsDatabase.class,
                        DATABASE_NAME).build();
            }
        }
        return sInstance;
    }


    public abstract HeadlinesDao headlinesDao();
}

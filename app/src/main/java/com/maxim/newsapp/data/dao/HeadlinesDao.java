package com.maxim.newsapp.data.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;


import com.maxim.newsapp.model.Article;

import java.util.List;

@Dao
public interface HeadlinesDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void bulkInsert(List<Article> articles);

//    @Query("SELECT * FROM articles  LIMIT :page , :pagesize")
//    LiveData<List<Article>> getNextArticles(int page, int pagesize);
//
//    @Query("SELECT * FROM articles")
//    LiveData<List<Article>> getAllArticles();

    @Query("SELECT * FROM articles ORDER BY published_at DESC")
    LiveData<List<Article>> sortByPublishedAt();
}

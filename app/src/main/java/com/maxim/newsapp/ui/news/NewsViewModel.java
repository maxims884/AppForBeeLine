package com.maxim.newsapp.ui.news;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;


import com.maxim.newsapp.data.NewsRepository;
import com.maxim.newsapp.model.Article;

import java.util.List;

public class NewsViewModel extends AndroidViewModel {
    private final NewsRepository newsRepository;

    public NewsViewModel(@NonNull Application application) {
        super(application);
        newsRepository = NewsRepository.getInstance(application.getApplicationContext());
    }

    public LiveData<List<Article>> getNewsHeadlines(String language, int page, int pageSize) {
        return newsRepository.getHeadlines(language,page, pageSize);
    }
}

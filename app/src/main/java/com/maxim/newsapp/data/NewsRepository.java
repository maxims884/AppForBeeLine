package com.maxim.newsapp.data;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;


import com.maxim.newsapp.data.dao.HeadlinesDao;
import com.maxim.newsapp.model.Article;
import com.maxim.newsapp.network.NewsApiClient;
import com.maxim.newsapp.utils.AppExecutors;

import java.util.List;


public class NewsRepository {
    private static final Object LOCK = new Object();
    private static NewsRepository sInstance;

    private final NewsApiClient newsApiService;
    private final HeadlinesDao headlinesDao;
    private final AppExecutors mExecutor;
    private final MutableLiveData<List<Article>> networkArticleLiveData;

    private NewsRepository(Context context) {
        newsApiService = NewsApiClient.getInstance(context);
        headlinesDao = NewsDatabase.getInstance(context).headlinesDao();
        mExecutor = AppExecutors.getInstance();
        networkArticleLiveData = new MutableLiveData<>();
        networkArticleLiveData.observeForever(new Observer<List<Article>>() {
            @Override
            public void onChanged(@Nullable final List<Article> articles) {
                if (articles != null) {
                    mExecutor.getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            headlinesDao.bulkInsert(articles);
                        }
                    });
                }
            }
        });
    }

    public synchronized static NewsRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new NewsRepository(context);
            }
        }
        return sInstance;
    }

    public LiveData<List<Article>> getHeadlines(String language,int page,int pageSize) {
        final LiveData<List<Article>> networkData = newsApiService.getHeadlines(language,page,pageSize);
        networkData.observeForever(new Observer<List<Article>>() {
            @Override
            public void onChanged(@Nullable List<Article> articles) {
                if (articles != null) {
                    networkArticleLiveData.setValue(articles);
                    networkData.removeObserver(this);
                }
            }
        });
        //return headlinesDao.getAllArticles();
        return headlinesDao.sortByPublishedAt();
        //return headlinesDao.getNextArticles(((page*pageSize)-9),page*pageSize);
    }
}
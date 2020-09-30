package com.maxim.newsapp.network;

import com.maxim.newsapp.BuildConfig;
import com.maxim.newsapp.model.ArticleResponseWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * An Api interface to send network requests
 * Includes Category enum that provides category names for requests
 */
public interface NewsApi {
    String API_KEY = BuildConfig.NewsApiKey;

    @Headers("X-Api-Key:" + API_KEY)
    @GET("/v2/top-headlines")
    Call<ArticleResponseWrapper> getHeadlines(
            @Query("language") String language,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );
}
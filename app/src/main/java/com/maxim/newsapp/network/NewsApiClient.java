package com.maxim.newsapp.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.maxim.newsapp.model.Article;
import com.maxim.newsapp.model.ArticleResponseWrapper;
import com.maxim.newsapp.utils.AppStatus;
import com.maxim.newsapp.utils.DateDeserializer;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A Singleton client class that provides {@link NewsApi} instance to load network requests
 */
public class NewsApiClient {
    private static final String NEWS_API_URL = "https://newsapi.org/";
    private static final Object LOCK = new Object();
    private static NewsApi sNewsApi;
    private static NewsApiClient sInstance;
    private NewsApiClient() {
    }

    /**
     * Provides instance of {@link NewsApi}
     *
     * @param context Context of current Activity or Application
     * @return {@link NewsApi}
     */
    public static NewsApiClient getInstance(final Context context) {
        if (sInstance == null || sNewsApi == null) {
            synchronized (LOCK) {
                Cache cache = new Cache(context.getApplicationContext().getCacheDir(), 5 * 1024 * 1024);
                Interceptor networkInterceptor = new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        if (AppStatus.getInstance(context).isOnline()) {
                            int maxAge = 60;
                            return originalResponse.newBuilder()
                                    .header("Cache-Control", "public, max-age=" + maxAge)
                                    .build();
                        } else {
                            int maxStale =  60 * 24 ;
                            return originalResponse.newBuilder()
                                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                                    .build();
                        }
                    }
                };
                HttpLoggingInterceptor loggingInterceptor =
                        new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
                OkHttpClient client = new OkHttpClient.Builder()
                        .cache( cache)
                        .addNetworkInterceptor(networkInterceptor)
                        .addInterceptor(loggingInterceptor)
                        .build();
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Date.class, new DateDeserializer())
                        .create();
                Retrofit.Builder builder =
                        new Retrofit
                                .Builder()
                                .baseUrl(NEWS_API_URL)
                                .client(client)
                                .addConverterFactory(GsonConverterFactory.create(gson));
                sNewsApi = builder.build().create(NewsApi.class);
                sInstance = new NewsApiClient();
            }
        }
        return sInstance;
    }

    public LiveData<List<Article>> getHeadlines(String language, int page, int pagesize) {
        final MutableLiveData<List<Article>> networkArticleLiveData = new MutableLiveData<>();

        Call<ArticleResponseWrapper> networkCall = sNewsApi.getHeadlines(language,page,pagesize);

        networkCall.enqueue(new Callback<ArticleResponseWrapper>() {
            @Override
            public void onResponse(@NonNull Call<ArticleResponseWrapper> call, @NonNull retrofit2.Response<ArticleResponseWrapper> response) {

                if (response.body() != null) {
                    List<Article> articles = response.body().getArticles();
                    networkArticleLiveData.setValue(articles);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArticleResponseWrapper> call, @NonNull Throwable t) {

            }
        });
        return networkArticleLiveData;
    }
}

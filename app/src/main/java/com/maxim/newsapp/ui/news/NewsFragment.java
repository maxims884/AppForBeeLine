package com.maxim.newsapp.ui.news;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.maxim.newsapp.MainActivity;
import com.maxim.newsapp.R;
import com.maxim.newsapp.adapter.NewsAdapter;
import com.maxim.newsapp.databinding.NewsFragmentBinding;
import com.maxim.newsapp.model.Article;
import com.maxim.newsapp.utils.PaginationScrollListener;

import java.util.List;

public class NewsFragment extends Fragment implements NewsAdapter.NewsAdapterListener {
    public static final String PARAM_LIST_STATE = "param-state";

    private static final int PAGE_START = 1;
    private static final int PAGE_SIZE = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    private final NewsAdapter newsAdapter = new NewsAdapter(null, this);
    private NewsFragmentBinding binding;
    private NewsViewModel viewModel;
    private Parcelable listState;

    public static NewsFragment newInstance() {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil
                .inflate(inflater, R.layout.news_fragment, container, false);
        recyclerView = binding.rvNewsPosts;
        recyclerView.setAdapter(newsAdapter);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            listState = savedInstanceState.getParcelable(PARAM_LIST_STATE);
        }
       viewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
        binding.swipeRfrshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeRfrshLayout.setRefreshing( true );
                viewModel.getNewsHeadlines("ru",PAGE_START,PAGE_SIZE).observe(NewsFragment.this, new Observer<List<Article>>() {
                    @Override
                    public void onChanged(@Nullable List<Article> articles) {
                        if (articles != null) {
                            newsAdapter.setArticles(articles,true);
                            restoreRecyclerViewState();
                        }
                    }
                });
                binding.swipeRfrshLayout.setRefreshing( false );
            }

        });

        linearLayoutManager = new LinearLayoutManager(this.getActivity(),RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        binding.rvNewsPosts.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                currentPage += 1;
                if(!isLoading)loadNextPage();

            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        loadFirstPage();
        }

    @Override
    public void onNewsItemClicked(Article article) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra(DetailActivity.PARAM_ARTICLE, article);
        binding.rvNewsPosts.scheduleLayoutAnimation();
        startActivity(intent);
    }


    private void loadFirstPage() {

        viewModel.getNewsHeadlines("ru",currentPage,PAGE_SIZE).observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(@Nullable List<Article> articles) {
                if (articles != null) {
                    newsAdapter.setArticles(articles,false);
                    restoreRecyclerViewState();
                }
            }

        });

        if (currentPage > TOTAL_PAGES) isLastPage = true;
    }

    private void loadNextPage() {
        isLoading = true;
        viewModel.getNewsHeadlines("ru",currentPage,PAGE_SIZE).observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(@Nullable List<Article> articles) {
                if (articles != null) {
                    newsAdapter.setArticles(articles,true);
                    restoreRecyclerViewState();
                    isLoading = false;
                }
            }

        });

        if (currentPage == TOTAL_PAGES) isLastPage = true;
    }

    @Override
    public void onItemOptionsClicked(Article article) {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (binding.rvNewsPosts.getLayoutManager() != null) {
            listState = binding.rvNewsPosts.getLayoutManager().onSaveInstanceState();
            outState.putParcelable(PARAM_LIST_STATE, listState);
        }
    }

    private void restoreRecyclerViewState() {
        if (binding.rvNewsPosts.getLayoutManager() != null) {
            binding.rvNewsPosts.getLayoutManager().onRestoreInstanceState(listState);
        }
    }
}

package com.maxim.newsapp;

import android.databinding.DataBindingUtil;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.maxim.newsapp.databinding.ActivityMainBinding;
import com.maxim.newsapp.ui.news.NewsFragment;

public class MainActivity extends AppCompatActivity{
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private ActivityMainBinding binding;
    private NewsFragment newsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (savedInstanceState == null) {
            newsFragment = NewsFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_container, newsFragment)
                    .commit();
        }

        setupToolbar();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.app_name));
            binding.toolbar.setContentInsetsAbsolute(10, 10);
        }
    }
}

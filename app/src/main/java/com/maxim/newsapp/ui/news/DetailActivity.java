package com.maxim.newsapp.ui.news;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.maxim.newsapp.R;
import com.maxim.newsapp.data.NewsRepository;
import com.maxim.newsapp.databinding.ActivityDetailBinding;
import com.maxim.newsapp.model.Article;

public class DetailActivity extends AppCompatActivity {
    public static final String PARAM_ARTICLE = "param-article";
    private ActivityDetailBinding binding;
    private NewsRepository newsRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        makeUiFullscreen();
        setupToolbar();
        setupArticleAndListener();
        newsRepository = NewsRepository.getInstance(this);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void makeUiFullscreen() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            binding.getRoot().setFitsSystemWindows(true);
        }
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    /**
     * Extracts Article from Arguments and Adds button listeners
     */
    private void setupArticleAndListener() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(PARAM_ARTICLE)) {
            final Article article = bundle.getParcelable(PARAM_ARTICLE);
            if (article != null) {
                binding.setArticle(article);
                setupShareButton(article);
            }
        }
    }

    private void setupShareButton(final Article article) {
        binding.ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String shareText = article.getTitle() + "\n" + article.getUrl();
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                intent.setType("text/plain");
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
    }
}
package com.maxim.newsapp.ui.headlines;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maxim.newsapp.R;
import com.maxim.newsapp.databinding.FragmentHeadlinesBinding;

public class HeadlinesFragment extends Fragment {
    private FragmentHeadlinesBinding binding;

    public HeadlinesFragment() {
    }

    public static HeadlinesFragment newInstance() {
        return new HeadlinesFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_headlines, container, false);

        ViewCompat.setElevation(binding.tablayoutHeadlines, 2);

        if (getActivity() != null) {
            binding.tablayoutHeadlines.setupWithViewPager(binding.viewpagerHeadlines);
        }
        return this.binding.getRoot();
    }
}

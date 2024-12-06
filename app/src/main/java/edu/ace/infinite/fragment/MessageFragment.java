package edu.ace.infinite.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager2.widget.ViewPager2;

import edu.ace.infinite.R;
import edu.ace.infinite.adapter.PersonalPagerAdapter;

public class MessageFragment extends BaseFragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);
        initViews();
        return view;
    }

    private void initViews() {

    }
}

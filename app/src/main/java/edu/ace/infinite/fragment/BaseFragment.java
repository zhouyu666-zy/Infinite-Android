package edu.ace.infinite.fragment;

import android.view.View;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    protected View view;

    protected <T extends View> T findViewById(int id){           
        return view.findViewById(id);
    }
}

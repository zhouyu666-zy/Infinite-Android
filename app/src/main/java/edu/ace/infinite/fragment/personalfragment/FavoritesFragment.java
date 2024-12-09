package edu.ace.infinite.fragment.personalfragment;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import edu.ace.infinite.R;
import edu.ace.infinite.adapter.PersonalVideoAdapter;
import edu.ace.infinite.pojo.Video;
import edu.ace.infinite.utils.http.VideoHttpUtils;

public class FavoritesFragment extends Fragment {
    private RecyclerView recyclerView;
    private PersonalVideoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_works, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        int spanCount = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        adapter = new PersonalVideoAdapter(getContext());
        recyclerView.setAdapter(adapter);

        initView();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshList = true;
    }

    public static boolean refreshList = false;
    private void initView() {
        refreshList = true;
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                if(refreshList){
                    refreshList = false;
                    loadList();
                }
                recyclerView.postDelayed(this, 500);
            }
        });
    }

    private boolean isRefreshing = false;
    private void loadList() {
        if(isRefreshing){
            return;
        }
        isRefreshing = true;
        //获取点赞数据
        new Thread(() -> {
            Video likeList = VideoHttpUtils.getLikeList();
            List<Video.Data> videoList = likeList.getData();
            getActivity().runOnUiThread(() -> {
                adapter.setVideos(videoList);
                isRefreshing = false;
            });
        }).start();
    }
}
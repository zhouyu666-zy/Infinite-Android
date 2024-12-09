package edu.ace.infinite.fragment.personalfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.ace.infinite.R;
import edu.ace.infinite.adapter.PersonalVideoAdapter;
import edu.ace.infinite.pojo.Video;
import edu.ace.infinite.utils.http.VideoHttpUtils;

import java.util.List;

public class LikesFragment extends Fragment {

    private RecyclerView recyclerView;
    private PersonalVideoAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_likes, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
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
            if(likeList != null){
                List<Video.Data> videoList = likeList.getData();
                getActivity().runOnUiThread(() -> {
                    adapter.setVideos(videoList);
                    isRefreshing = false;
                });
            }
        }).start();
    }
}

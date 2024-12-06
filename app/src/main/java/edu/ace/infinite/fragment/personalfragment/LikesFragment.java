package edu.ace.infinite.fragment.personalfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import edu.ace.infinite.R;
import edu.ace.infinite.adapter.VideoGridAdapter;
import edu.ace.infinite.model.VideoItem;

import java.util.ArrayList;
import java.util.List;

public class LikesFragment extends Fragment {

    private RecyclerView recyclerView;
    private VideoGridAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_likes, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new VideoGridAdapter(getContext());
        recyclerView.setAdapter(adapter);

        loadLikedVideos();

        return view;
    }

    private void loadLikedVideos() {
        // TODO: 从服务器加载用户喜欢的视频数据
        // 这里使用模拟数据
        adapter.setVideos(generateDummyLikedData());
    }

    private List<VideoItem> generateDummyLikedData() {
        List<VideoItem> dummyData = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            dummyData.add(new VideoItem("Liked Video " + i, R.drawable.video_cover));
        }
        return dummyData;
    }
}

package edu.ace.infinite.fragment.personalfragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.ace.infinite.R;
import edu.ace.infinite.adapter.VideoGridAdapter;
import edu.ace.infinite.model.VideoItem;

public class FavoritesFragment extends Fragment {
    private RecyclerView recyclerView;
    private VideoGridAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_works, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        adapter = new VideoGridAdapter(getContext());
        recyclerView.setAdapter(adapter);

        loadWorks();

        return view;
    }

    private void loadWorks() {
        // TODO: 从服务器加载作品数据
        // 这里使用模拟数据
        adapter.setVideos(generateDummyData());
    }

    private List<VideoItem> generateDummyData() {
        List<VideoItem> dummyData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            dummyData.add(new VideoItem("Video " + i, R.drawable.video_cover));
        }
        return dummyData;
    }
}
package edu.ace.infinite.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import edu.ace.infinite.fragment.personalfragment.FavoritesFragment;
import edu.ace.infinite.fragment.personalfragment.LikesFragment;
import edu.ace.infinite.fragment.personalfragment.WorksFragment;

public class PersonalPagerAdapter extends FragmentStateAdapter {
    public PersonalPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new WorksFragment();
            case 1:
                return new LikesFragment();
            case 2:
                return new FavoritesFragment();
            default:
                return new WorksFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}


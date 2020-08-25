package com.narwal.whatsappclone;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ScreenSliderAdapter extends FragmentStateAdapter {
    public ScreenSliderAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new ChatsFragment();
        } else {
            return new PeopleFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

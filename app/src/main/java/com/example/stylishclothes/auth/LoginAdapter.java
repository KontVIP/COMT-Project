package com.example.stylishclothes.auth;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.stylishclothes.R;

public class LoginAdapter extends FragmentPagerAdapter {

    private Context context;
    int totalTabs;

    public LoginAdapter(FragmentManager fm, Context context, int totalTabs) {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                LoginTabFragment loginTabFragment = new LoginTabFragment();
                return loginTabFragment;
            case 1:
                SignupTabFragment signupTabFragment = new SignupTabFragment();
                return  signupTabFragment;
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Вхід";
        } else {
            return "Реєстрація";
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}

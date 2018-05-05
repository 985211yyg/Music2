package com.example.yungui.music.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yungui.music.R;
import com.example.yungui.music.adapter.SectionsPagerAdapter;
import com.google.android.gms.plus.PlusOneButton;

/**
 * A fragment with a Google +1 button.
 * Use the {@link PlusOneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlusOneFragment extends Fragment {
    private ViewPager viewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    public PlusOneFragment() {

    }

    public static PlusOneFragment newInstance(String param1, String param2) {
        PlusOneFragment fragment = new PlusOneFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_plus_one, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        viewPager = view.findViewById(R.id.viewpager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getActivity().getSupportFragmentManager());
        mSectionsPagerAdapter.addFragments(SongDetailAboutFragment.newInstance(0));
        mSectionsPagerAdapter.addFragments(SongDetailAlbumFragment.newInstance(0));
        mSectionsPagerAdapter.addFragments(SongDetailLrcFragment.newInstance(0));
        viewPager.setAdapter(mSectionsPagerAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

    }


}

package com.example.ef.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.ef.R;
import com.example.ef.SlidePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * פריגמנט היוצר רשימה של שני פרגמנטים שונים , ומאפשר מעבר
 * בינהם באמצעות החלקת המסך ימינה או שמאלה עבור הדף המבוקש
 */
public class DiarySlideScanHistoryFragment extends Fragment {

    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    View root;
    List<Fragment> listSlider;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_d_iary_slide_scanhistory, container, false);

        initViews();

        initVarbs();

        listSlider.add(new DiaryFragment());
        listSlider.add(new ScanHistoryFragment());
        pagerAdapter = new SlidePagerAdapter(getActivity().getSupportFragmentManager(), listSlider);
        pager.setAdapter(pagerAdapter);

        return root;
    }

    private void initViews() {
        pager = root.findViewById(R.id.pager);
    }

    private void initVarbs() {
        listSlider = new ArrayList<>();

    }
}


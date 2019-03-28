package com.example.ledmatrix;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // supply ViewPager with PagerAdapter implementation
        FragmentManager fragmentManager = getSupportFragmentManager();
        InputPagerAdapter adapter = new InputPagerAdapter(fragmentManager);
        ViewPager viewPager = findViewById(R.id.input_view_pager);
        viewPager.setAdapter(adapter);

        // link TabLayout with ViewPager
        TabLayout tabLayout = findViewById(R.id.input_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }
}
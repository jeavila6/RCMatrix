package com.example.ledmatrix;

import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set toolbar as app bar
        Toolbar mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

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
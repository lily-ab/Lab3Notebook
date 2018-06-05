package com.example.lab3notebook;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    /** идентификатор первого фрагмента. */
    public static final int FRAGMENT_ONE = 0;
    /** идентификатор третего. */
    /** идентификатор второго. */
    public static final int FRAGMENT_TWO = 1;
    /** количество фрагментов. */
    public static final int FRAGMENTS = 2;
    /** адаптер фрагментов. */
    private FragmentPagerAdapter _fragmentPagerAdapter;
    /** список фрагментов для отображения. */
    private final List<Fragment> _fragments = new ArrayList<Fragment>();
    /** сам ViewPager который будет все это отображать. */
    private ViewPager _viewPager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // создаем фрагменты.
        _fragments.add(FRAGMENT_ONE, new FirstFragment());
        _fragments.add(FRAGMENT_TWO, new SecondFragment());
        // Настройка фрагментов, определяющих количество фрагментов, экраны и название.
        _fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {

                return FRAGMENTS;
            }

            @Override
            public Fragment getItem(final int position) {

                return _fragments.get(position);
            }

            @Override
            public CharSequence getPageTitle(final int position) {

                switch (position) {
                    case FRAGMENT_ONE:
                        return "Title One";
                    case FRAGMENT_TWO:
                        return "Title Two";
                    default:
                        return null;
                }
            }
        };
        _viewPager =  findViewById(R.id.pager);
        _viewPager.setAdapter(_fragmentPagerAdapter);
        _viewPager.setCurrentItem(0);
    }

    public void addNote(View view){
        Intent intent = new Intent(this, NoteActivity.class);
        startActivity(intent);
    }

    public void addTag(View view){
        Intent intent = new Intent(this, TagActivity.class);
        startActivity(intent);
    }
}
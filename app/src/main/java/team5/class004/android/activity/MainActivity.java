package team5.class004.android.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import team5.class004.android.R;
import team5.class004.android.databinding.ActivityMainBinding;
import team5.class004.android.fragment.ExploreFragment;
import team5.class004.android.fragment.HabitListFragment;
import team5.class004.android.fragment.MyProfileFragment;
import team5.class004.android.utils.Utils;

public class MainActivity extends BaseActivity {
    MainActivity mActivity = this;
    ActivityMainBinding activityBinding;
    MenuItem prevMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(mActivity, R.layout.activity_main);

        setSupportActionBar(activityBinding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        activityBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, HabitCreateActivity.class));
            }
        });

        activityBinding.viewpager.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        activityBinding.viewpager.setCurrentItem(0);


        activityBinding.bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.habit_list:
                        activityBinding.viewpager.setCurrentItem(0);
                        break;
                    case R.id.test1:
                        activityBinding.viewpager.setCurrentItem(1);
                        break;
                    case R.id.test2:
                        activityBinding.viewpager.setCurrentItem(2);
                        break;
                }
                return true;
            }
        });

        activityBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null)
                    prevMenuItem.setChecked(false);
                else
                    activityBinding.bottomNavigationView.getMenu().getItem(0).setChecked(false);

                activityBinding.bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = activityBinding.bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return new HabitListFragment();
                case 1:
                    return new ExploreFragment();
                case 2:
                    return new MyProfileFragment();
                default:
                    return null;
            }
        }
        @Override
        public int getCount()
        {
            return 3;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
package team5.class004.android.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.AWSStartupHandler;
import com.amazonaws.mobile.client.AWSStartupResult;

import team5.class004.android.R;
import team5.class004.android.databinding.ActivityMainBinding;
import team5.class004.android.fragment.ExploreFragment;
import team5.class004.android.fragment.HabitListFragment;
import team5.class004.android.fragment.MyProfileFragment;
import team5.class004.android.utils.Utils;

public class MainActivity extends BaseActivity {
    MainActivity mActivity = this;
    public ActivityMainBinding activityBinding;
    MenuItem prevMenuItem;
    int currentMenuItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(mActivity, R.layout.activity_main);

        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d("YourMainActivity", "AWSMobileClient is instantiated and you are connected to AWS!");
            }
        }).execute();

        setSupportActionBar(activityBinding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        activityBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentMenuItem == 1)
                    startActivity(new Intent(mActivity, BoardDocumentCreateActivity.class));
                else
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
                        activityBinding.fab.setVisibility(View.VISIBLE);
//                        activityBinding.fab.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_add_white));
                        currentMenuItem = 0;
                        break;
                    case R.id.test1:
                        activityBinding.fab.setVisibility(View.VISIBLE);
//                        activityBinding.fab.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_videocam_black_24dp));
                        currentMenuItem = 1;
                        break;
                    case R.id.test2:
                        activityBinding.fab.setVisibility(View.GONE);
                        currentMenuItem = 2;
                        break;
                }
                activityBinding.viewpager.setCurrentItem(currentMenuItem);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
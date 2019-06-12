package team5.class004.android.activity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
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
import team5.class004.android.fragment.JournalListFragment;
import team5.class004.android.fragment.MyProfileFragment;
import team5.class004.android.utils.AlarmReceiver;

public class MainActivity extends BaseActivity {
    MainActivity mActivity = this;
    public ActivityMainBinding activityBinding;
    MenuItem prevMenuItem;
    int currentMenuItem = 0;
    AlarmManager alarmManager;
    Handler mHandler = new Handler();
    Intent alarmIntent;
    PendingIntent pendingIntent;
    boolean notified = false;

    private void handleNotification() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("habit", "habit", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("habit");
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent);
//        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, System.currentTimeMillis(), 5000, pendingIntent);
        mAlarmChecker.run();
    }

    Runnable mAlarmChecker = new Runnable() {
        @Override
        public void run() {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent);
            mHandler.postDelayed(mAlarmChecker, 6000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(mActivity, R.layout.activity_main);
//        handleNotification();
        AWSMobileClient.getInstance().initialize(this, new AWSStartupHandler() {
            @Override
            public void onComplete(AWSStartupResult awsStartupResult) {
                Log.d("YourMainActivity", "AWSMobileClient is instantiated and you are connected to AWS!");
            }
        }).execute();

        setSupportActionBar(activityBinding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final Intent myIntent = new Intent(mActivity, BoardDocumentCreateActivity.class);
        activityBinding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentMenuItem == 2) {
                    myIntent.putExtra("board", ExploreFragment.board);
                    startActivity(myIntent);
                } else if(currentMenuItem == 1)
                    startActivity(new Intent(mActivity, JournalCreateActivity.class));
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
                    case R.id.diary:
                        activityBinding.fab.setVisibility(View.VISIBLE);
//                        activityBinding.fab.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_videocam_black_24dp));
                        currentMenuItem = 1;
                        break;
                    case R.id.explore:
                        activityBinding.fab.setVisibility(View.VISIBLE);
//                        activityBinding.fab.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.ic_videocam_black_24dp));
                        currentMenuItem = 2;
                        break;
                    case R.id.my_profile:
                        activityBinding.fab.setVisibility(View.GONE);
                        currentMenuItem = 3;
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
        HabitListFragment habitListFragment = new HabitListFragment();
        JournalListFragment journalListFragment = new JournalListFragment();
        ExploreFragment exploreFragment = new ExploreFragment();
        MyProfileFragment myProfileFragment = new MyProfileFragment();

        public pagerAdapter(FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return habitListFragment;
                case 1:
                    return journalListFragment;
                case 2:
                    return exploreFragment;
                case 3:
                    return myProfileFragment;
                default:
                    return null;
            }
        }
        @Override
        public int getCount()
        {
            return 4;
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
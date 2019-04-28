package team5.class004.android.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import team5.class004.android.R;
import team5.class004.android.databinding.ActivityMainBinding;
import team5.class004.android.databinding.ActivitySplashBinding;
import team5.class004.android.fragment.ExploreFragment;
import team5.class004.android.fragment.HabitListFragment;
import team5.class004.android.fragment.MyProfileFragment;

public class SplashActivity extends BaseActivity {
    SplashActivity mActivity = this;
    ActivitySplashBinding activityBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(mActivity, R.layout.activity_splash);

        startActivity(new Intent(mActivity, MainActivity.class));
        finish();
    }
}

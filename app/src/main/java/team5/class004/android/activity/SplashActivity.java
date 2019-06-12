package team5.class004.android.activity;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;

import team5.class004.android.R;
import team5.class004.android.databinding.ActivitySplashBinding;

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

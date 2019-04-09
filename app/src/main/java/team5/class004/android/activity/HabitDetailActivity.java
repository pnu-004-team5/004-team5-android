package team5.class004.android.activity;

import android.app.Activity;
import android.os.Bundle;
import team5.class004.android.R;
import team5.class004.android.utils.Utils;

public class HabitDetailActivity extends BaseActivity {
    Activity mActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit_detail);

        Utils.getInstance().setActionbar(mActivity, "이건 무슨 습관?", true);
    }

}

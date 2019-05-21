package team5.class004.android.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import team5.class004.android.R;
import team5.class004.android.databinding.ActivityHabitDetailBinding;
import team5.class004.android.databinding.ActivityHabitDetailNotificationBinding;
import team5.class004.android.model.HabitItem;
import team5.class004.android.utils.Utils;

public class HabitDetailNotificationActivity extends BaseActivity {
    Activity mActivity = this;
    ActivityHabitDetailNotificationBinding activityBinding;
    HabitItem habitItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(mActivity, R.layout.activity_habit_detail_notification);

        Utils.getInstance().setActionbar(mActivity, "알림 설정", true);

        activityBinding.switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(mActivity, "알림 설정되었습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity, "알림 해제되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

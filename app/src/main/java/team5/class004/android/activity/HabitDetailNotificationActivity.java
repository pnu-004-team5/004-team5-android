package team5.class004.android.activity;

import android.app.Activity;
import androidx.databinding.DataBindingUtil;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;

import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.Date;

import team5.class004.android.GlobalApp;
import team5.class004.android.R;
import team5.class004.android.databinding.ActivityHabitDetailNotificationBinding;
import team5.class004.android.model.HabitItem;
import team5.class004.android.utils.AlarmReceiver;
import team5.class004.android.utils.Utils;

public class HabitDetailNotificationActivity extends BaseActivity {
    Activity mActivity = this;
    ActivityHabitDetailNotificationBinding activityBinding;
    HabitItem habitItem;
    AlarmManager alarmManager;
    Handler mHandler = new Handler();
    Intent alarmIntent;
    PendingIntent pendingIntent;
    boolean notified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(mActivity, R.layout.activity_habit_detail_notification);

        Utils.getInstance().setActionbar(mActivity, "알림 설정", true);

//        GlobalApp.getInstance().prefs.edit().putString()

        habitItem = (HabitItem) getIntent().getSerializableExtra("habitItem");

        if(GlobalApp.getInstance().prefs.getInt("notification_habit_" + habitItem.id + "_hour", -1) != -1) {
            activityBinding.timePicker.setHour(GlobalApp.getInstance().prefs.getInt("notification_habit_" + habitItem.id + "_hour", -1));
            activityBinding.timePicker.setMinute(GlobalApp.getInstance().prefs.getInt("notification_habit_" + habitItem.id + "_minute", -1));
            activityBinding.timePicker.setVisibility(View.VISIBLE);
            activityBinding.switchNotification.setChecked(true);
        } else {
            activityBinding.timePicker.setVisibility(View.INVISIBLE);
            activityBinding.switchNotification.setChecked(false);
        }

        activityBinding.switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    GlobalApp.getInstance().prefs.edit().putInt("notification_habit_" + habitItem.id + "_hour", activityBinding.timePicker.getHour()).apply();
                    GlobalApp.getInstance().prefs.edit().putInt("notification_habit_" + habitItem.id + "_minute", activityBinding.timePicker.getMinute()).apply();
                    GlobalApp.getInstance().prefs.edit().putString("notification_habit_name", habitItem.name).apply();
                    activityBinding.timePicker.setVisibility(View.VISIBLE);
                    Snackbar.make(activityBinding.switchNotification, "매일 " + activityBinding.timePicker.getHour() + "시 " + activityBinding.timePicker.getMinute() + "분에 알림이 설정되었습니다.", Snackbar.LENGTH_SHORT).show();
                } else {
                    GlobalApp.getInstance().prefs.edit().remove("notification_habit_" + habitItem.id + "_hour").apply();
                    GlobalApp.getInstance().prefs.edit().remove("notification_habit_" + habitItem.id + "_minute").apply();
                    activityBinding.timePicker.setVisibility(View.INVISIBLE);
                    Snackbar.make(activityBinding.switchNotification, "알림 해제되었습니다.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        activityBinding.timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                GlobalApp.getInstance().prefs.edit().putInt("notification_habit_" + habitItem.id + "_hour", hourOfDay).apply();
                GlobalApp.getInstance().prefs.edit().putInt("notification_habit_" + habitItem.id + "_minute", minute).apply();
                GlobalApp.getInstance().prefs.edit().putString("notification_habit_name", habitItem.name).apply();
                Snackbar.make(activityBinding.switchNotification, "매일 " + hourOfDay + "시 " + minute + "분에 알림이 설정되었습니다.", Snackbar.LENGTH_SHORT).show();
            }
        });
//        GlobalApp.getInstance().prefs.edit().putString("notification_time")
//        activityBinding.timePicker.getHour()
//        activityBinding.timePicker.getMinute()
        handleNotification();
    }

    private void handleNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("habit", "habit", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("habit");
            channel.enableVibration(true);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
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
            Date date = new Date();
            date.setTime(System.currentTimeMillis());
            date.setHours(GlobalApp.getInstance().prefs.getInt("notification_habit_" + habitItem.id + "_hour", -1));
            date.setMinutes(GlobalApp.getInstance().prefs.getInt("notification_habit_" + habitItem.id + "_minute", -1));
            if(!notified && System.currentTimeMillis() >= date.getTime()) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pendingIntent);
                notified = true;
            }
            mHandler.postDelayed(mAlarmChecker, 6000);
        }
    };
}

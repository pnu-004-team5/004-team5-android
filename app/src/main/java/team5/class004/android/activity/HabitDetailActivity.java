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
import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import team5.class004.android.R;
import team5.class004.android.databinding.ActivityHabitDetailBinding;
import team5.class004.android.model.HabitItem;
import team5.class004.android.utils.Utils;

public class HabitDetailActivity extends BaseActivity {
    Activity mActivity = this;
    ActivityHabitDetailBinding activityBinding;
    HabitItem habitItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(mActivity, R.layout.activity_habit_detail);

        Utils.getInstance().setActionbar(mActivity, "이건 무슨 습관?", true);


        habitItem = (HabitItem) getIntent().getSerializableExtra("habitItem");

        List<Calendar> calendars = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(2019, 3, 28);
        calendars.add(calendar);
        calendar.set(2019, 3, 27);
        calendars.add(calendar);

        activityBinding.calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
              @Override
              public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

              }
          });
                activityBinding.calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
        activityBinding.calendarView.setSelectionColor(getResources().getColor(R.color.colorAccent));
        activityBinding.calendarView.addDecorators(new OneDayDecorator());

        activityBinding.tvHabitName.setText(habitItem.name);
        activityBinding.tvHabitDate.setText(habitItem.fromDate + " ~ " + habitItem.toDate);
        activityBinding.tvHabitMemo.setText(habitItem.memo);
        activityBinding.headerContainer.setBackgroundColor(Color.parseColor(habitItem.color));

    }

    public class OneDayDecorator implements DayViewDecorator {

        private CalendarDay date;

        public OneDayDecorator() {
            date = CalendarDay.today();
        }

        @Override
        public boolean shouldDecorate(CalendarDay calendarDay) {
            boolean found = false;

            try {
                JSONArray completeDateJsonArr = new JSONArray(habitItem.completeDate);
                String month = String.format("%1$" + 2 + "s", calendarDay.getMonth()+1).replace(' ', '0');
                String day = String.format("%1$" + 2 + "s", calendarDay.getDay()).replace(' ', '0');
                for(int i = 0; i < completeDateJsonArr.length(); i++) {
                    if(completeDateJsonArr.get(i).equals(calendarDay.getYear() + "-" + (month) + "-" + day)) {
                        found = true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return found; //date != null && day.equals(date);
        }

        @Override
        public void decorate(DayViewFacade view) {

            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.calendar_checked));
            view.addSpan(new StyleSpan(Typeface.BOLD));
            view.addSpan(new RelativeSizeSpan(1.4f));
            view.addSpan(new ForegroundColorSpan(Color.WHITE));
        }
    }
}

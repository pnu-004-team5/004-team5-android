package team5.class004.android.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team5.class004.android.GlobalApp;
import team5.class004.android.R;
import team5.class004.android.databinding.ActivityHabitCreateBinding;
import team5.class004.android.model.HabitItem;
import team5.class004.android.utils.Utils;
import team5.class004.android.widget.LoadingDialog;

public class HabitCreateActivity extends BaseActivity {
    Activity mActivity = this;
    HabitItem habitItem = new HabitItem();
    ActivityHabitCreateBinding activityBinding;
    LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(mActivity, R.layout.activity_habit_create);
        dialog = new LoadingDialog(mActivity);
        Utils.getInstance().setActionbar(mActivity, "습관 만들기", true);
    }


    void showDialog() {
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }

    void dismisslDialog() {
        dialog.dismiss();
    }


    public void create() {
        showDialog();

        HashMap<String, String> params = new HashMap<>();
        params.put("name", activityBinding.etHabitName.getText().toString());
        params.put("memo", activityBinding.etHabitMemo.getText().toString());
        params.put("fromDate", activityBinding.fromDatePicker.getYear() + "-" + (activityBinding.fromDatePicker.getMonth()+1) + "-" + activityBinding.fromDatePicker.getDayOfMonth());
        params.put("toDate", activityBinding.toDatePicker.getYear() + "-" + (activityBinding.toDatePicker.getMonth()+1) + "-" + activityBinding.toDatePicker.getDayOfMonth());
        GlobalApp.getInstance().restClient.api().createMyHabit(params).enqueue(new Callback<HabitItem>()
        {
            @Override
            public void onResponse(@NonNull Call<HabitItem> call, @NonNull Response<HabitItem> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    habitItem = response.body();
                    finish();
                }
                dismisslDialog();
            }

            @Override
            public void onFailure(@NonNull Call<HabitItem> call, @NonNull Throwable t)
            {
                dismisslDialog();
                finish();
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_habit_create_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.complete) {
            create();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

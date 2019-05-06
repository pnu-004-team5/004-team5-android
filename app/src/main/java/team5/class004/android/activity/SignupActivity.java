package team5.class004.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team5.class004.android.GlobalApp;
import team5.class004.android.R;
import team5.class004.android.databinding.ActivitySignupBinding;
import team5.class004.android.model.HabitItem;
import team5.class004.android.model.UserItem;
import team5.class004.android.utils.Utils;
import team5.class004.android.widget.LoadingDialog;

public class SignupActivity extends BaseActivity {
    Activity mActivity = SignupActivity.this;
    ActivitySignupBinding activityBinding;
    LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(mActivity, R.layout.activity_signup);
        Utils.getInstance().setActionbar(mActivity, "로그인", true);
        dialog = new LoadingDialog(mActivity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        activityBinding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                HashMap<String, String> params = new HashMap<>();
                params.put("name", activityBinding.etName.getText().toString());
                params.put("email", activityBinding.etEmail.getText().toString());
                params.put("password", activityBinding.etPassword.getText().toString());
                GlobalApp.getInstance().restClient.api().createUser(params).enqueue(new Callback<UserItem>()
                {
                    @Override
                    public void onResponse(@NonNull Call<UserItem> call, @NonNull Response<UserItem> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            if(response.body().email == null) {
                                Toast.makeText(mActivity, "이미 존재하는 계정입니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                GlobalApp.getInstance().userItem = response.body();
                                if(GlobalApp.getInstance().userItem != null)
                                    GlobalApp.getInstance().prefs.edit().putString("user", new Gson().toJson(GlobalApp.getInstance().userItem)).apply();
                                startActivity(new Intent(mActivity, LoginActivity.class));
                                finish();
                            }
                        }
                        dismissDialog();
                    }

                    @Override
                    public void onFailure(@NonNull Call<UserItem> call, @NonNull Throwable t)
                    {
                        dismissDialog();
                        finish();
                        t.printStackTrace();
                    }
                });
            }
        });
    }

    void showDialog() {
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }

    void dismissDialog() {
        dialog.dismiss();
    }
}

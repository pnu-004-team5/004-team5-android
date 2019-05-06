package team5.class004.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
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
import team5.class004.android.databinding.ActivityLoginBinding;
import team5.class004.android.model.UserItem;
import team5.class004.android.utils.Utils;
import team5.class004.android.widget.LoadingDialog;

public class LoginActivity extends BaseActivity {
    Activity mActivity = LoginActivity.this;
    ActivityLoginBinding activityBinding;
    LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(mActivity, R.layout.activity_login);

        Utils.getInstance().setActionbar(mActivity, "로그인", true);
        dialog = new LoadingDialog(mActivity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        activityBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                HashMap<String, String> params = new HashMap<>();
                params.put("email", activityBinding.etEmail.getText().toString());
                params.put("password", activityBinding.etPassword.getText().toString());
                GlobalApp.getInstance().restClient.api().getUser(params).enqueue(new Callback<UserItem>()
                {
                    @Override
                    public void onResponse(@NonNull Call<UserItem> call, @NonNull Response<UserItem> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            if(response.body().email == null) {
                                Toast.makeText(mActivity, "일치하는 계정이 없습니다.", Toast.LENGTH_SHORT).show();
                            } else {
                                GlobalApp.getInstance().userItem = response.body();
                                if(GlobalApp.getInstance().userItem != null)
                                    GlobalApp.getInstance().prefs.edit().putString("user", new Gson().toJson(GlobalApp.getInstance().userItem)).apply();
                                startActivity(new Intent(mActivity, MainActivity.class));
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

        activityBinding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, SignupActivity.class));
                finish();
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

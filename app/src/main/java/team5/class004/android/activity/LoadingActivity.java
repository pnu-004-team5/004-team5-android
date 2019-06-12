package team5.class004.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team5.class004.android.GlobalApp;
import team5.class004.android.model.UserItem;

public class LoadingActivity extends Activity {
    LoadingActivity mActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        UserItem userItem = new Gson().fromJson(GlobalApp.getInstance().prefs.getString("user", "{email: \"\", password: \"\"}"), UserItem.class);
        HashMap<String, String> params = new HashMap<>();
        params.put("email", userItem.email);
        params.put("password", userItem.password);
        GlobalApp.getInstance().restClient.api().getUser(params).enqueue(new Callback<UserItem>()
        {
            @Override
            public void onResponse(@NonNull Call<UserItem> call, @NonNull Response<UserItem> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    if(response.body().email == null) {
                        startActivity(new Intent(mActivity, LoginActivity.class));
                        finish();
                    } else if(!response.body().auth) {

                        startActivity(new Intent(mActivity, LoginActivity.class));
                        finish();
                    } else {
                        GlobalApp.getInstance().userItem = response.body();
                        GlobalApp.getInstance().prefs.edit().putString("user", new Gson().toJson(GlobalApp.getInstance().userItem)).apply();
                        startActivity(new Intent(mActivity, MainActivity.class));
                        finish();
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserItem> call, @NonNull Throwable t)
            {
                try {
                    Thread.sleep(1000);
                    finish();
                    t.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

//        if(GlobalApp.getInstance().userItem == null) {
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//        } else {
//            startActivity(new Intent(this, MainActivity.class));
//            finish();
//        }
    }
}
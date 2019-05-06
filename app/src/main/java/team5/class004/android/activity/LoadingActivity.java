package team5.class004.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import team5.class004.android.GlobalApp;

public class LoadingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try{
            Thread.sleep(3000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        if(GlobalApp.getInstance().userItem == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
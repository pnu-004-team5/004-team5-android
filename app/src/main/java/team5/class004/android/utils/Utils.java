package team5.class004.android.utils;


import android.content.Context;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import team5.class004.android.R;

public class Utils {
    private Utils() { }

    private static class SingletonHolder {
        public static final Utils INSTANCE = new Utils();
    }

    public static Utils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void setActionbar(Context context, String title, boolean displayHomeAsUp) {
        final ActionBar actionBar = ((AppCompatActivity)context).getSupportActionBar();
        View viewActionBar = ((AppCompatActivity)context).getLayoutInflater().inflate(R.layout.actionbar_custom, null);
        ActionBar.LayoutParams params1 = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER);
        TextView tvTitle = viewActionBar.findViewById(R.id.actionbar_title);
//        ImageView ivImage = viewActionBar.findViewById(R.id.actionbar_image);
        tvTitle.setText(title);
//        if(image != 0) {
//            ivImage.setVisibility(View.VISIBLE);
//            GlideApp.with(context).load(image).dontTransform().override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).fitCenter().into(ivImage);
//        }

//        if(isTransparent)
//            tvTitle.setTextColor(Color.WHITE);
        actionBar.setCustomView(viewActionBar, params1);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(displayHomeAsUp);
        actionBar.setIcon(android.R.color.transparent);
        actionBar.setHomeButtonEnabled(true);
    }
}
package team5.class004.android.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team5.class004.android.GlobalApp;
import team5.class004.android.R;
import team5.class004.android.databinding.ActivityBoardDocumentCreateBinding;
import team5.class004.android.databinding.ActivityHabitCreateBinding;
import team5.class004.android.model.BoardDocumentItem;
import team5.class004.android.utils.Utils;
import team5.class004.android.widget.LoadingDialog;

public class BoardDocumentCreateActivity extends BaseActivity {
    Activity mActivity = this;
    BoardDocumentItem boardDocumentItem = new BoardDocumentItem();
    ActivityBoardDocumentCreateBinding activityBinding;
    LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(mActivity, R.layout.activity_board_document_create);
        dialog = new LoadingDialog(mActivity);
        Utils.getInstance().setActionbar(mActivity, "글 작성", true);
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
        params.put("userId", GlobalApp.getInstance().userItem.id);
        params.put("content", "asdasd");
        params.put("videoUrl", "zxczxc");
//        params.put("regdate", activityBinding.fromDatePicker.getYear() + "-" + (activityBinding.fromDatePicker.getMonth()+1) + "-" + activityBinding.fromDatePicker.getDayOfMonth());
        GlobalApp.getInstance().restClient.api().createBoardDocument(params).enqueue(new Callback<BoardDocumentItem>()
        {
            @Override
            public void onResponse(@NonNull Call<BoardDocumentItem> call, @NonNull Response<BoardDocumentItem> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    boardDocumentItem = response.body();
                    finish();
                }
                dismisslDialog();
            }

            @Override
            public void onFailure(@NonNull Call<BoardDocumentItem> call, @NonNull Throwable t)
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

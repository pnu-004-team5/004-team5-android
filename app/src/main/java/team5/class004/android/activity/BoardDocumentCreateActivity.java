package team5.class004.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team5.class004.android.GlobalApp;
import team5.class004.android.R;
import team5.class004.android.databinding.ActivityBoardDocumentCreateBinding;
import team5.class004.android.databinding.ActivityHabitCreateBinding;
import team5.class004.android.fragment.MyProfileFragment;
import team5.class004.android.interfaces.AppConstants;
import team5.class004.android.model.BoardDocumentItem;
import team5.class004.android.model.UserItem;
import team5.class004.android.utils.Utils;
import team5.class004.android.widget.LoadingDialog;

public class BoardDocumentCreateActivity extends BaseActivity {
    Activity mActivity = this;
    BoardDocumentItem boardDocumentItem = new BoardDocumentItem();
    ActivityBoardDocumentCreateBinding activityBinding;
    LoadingDialog dialog;
    Random rand = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(mActivity, R.layout.activity_board_document_create);
        dialog = new LoadingDialog(mActivity);
        Utils.getInstance().setActionbar(mActivity, "글 작성", true);

        activityBinding.btnChooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.create(mActivity).includeVideo(true).start();
            }
        });
    }


    void showDialog() {
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }

    void dismisslDialog() {
        dialog.dismiss();
    }


    public void create(String videoUrl, String content) {
        showDialog();

        HashMap<String, String> params = new HashMap<>();
        params.put("userId", GlobalApp.getInstance().userItem.id);
        params.put("content", content);
        params.put("videoUrl", videoUrl);
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
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        Log.e("asd", "2222222222");
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image image = ImagePicker.getFirstImageOrNull(data);
//            printImages(image);
            uploadWithTransferUtility(image.getPath());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void uploadWithTransferUtility(String path) {
        showDialog();
        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(GlobalApp.getInstance())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .build();

        final String uploadKey = GlobalApp.getInstance().userItem.id + "_" + rand.nextInt(999999999) + "_video.mp4";
        TransferObserver uploadObserver =
                transferUtility.upload(
                        "public/" + uploadKey,
                        new File(path), CannedAccessControlList.PublicRead);

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    activityBinding.etVideoUrl.setText(AppConstants.S3_URL + "/" + uploadKey);
                    dismisslDialog();
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int)percentDonef;

                Log.e("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent
                        + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
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
            create(activityBinding.etVideoUrl.getText().toString(), activityBinding.etContent.getText().toString());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

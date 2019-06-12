package team5.class004.android.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

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
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team5.class004.android.GlobalApp;
import team5.class004.android.R;
import team5.class004.android.databinding.ActivityHabitCreateBinding;
import team5.class004.android.databinding.ActivityJournalCreateBinding;
import team5.class004.android.fragment.MyProfileFragment;
import team5.class004.android.interfaces.AppConstants;
import team5.class004.android.model.BoardDocumentItem;
import team5.class004.android.model.HabitItem;
import team5.class004.android.model.JournalItem;
import team5.class004.android.model.UserItem;
import team5.class004.android.utils.Utils;
import team5.class004.android.widget.LoadingDialog;

public class JournalCreateActivity extends BaseActivity {
    Activity mActivity = this;
    ActivityJournalCreateBinding activityBinding;
    LoadingDialog dialog;
    JournalItem journalItem = new JournalItem();
    Image selectedImage;
    boolean isUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityBinding = DataBindingUtil.setContentView(mActivity, R.layout.activity_journal_create);
        dialog = new LoadingDialog(mActivity);
        Utils.getInstance().setActionbar(mActivity, "습관일기 쓰기", true);

        activityBinding.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.create(mActivity).start();
            }
        });

        activityBinding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mActivity)
                        .setTitle("삭제")
                        .setMessage("정말 삭제하시겠습니까?")
                        .setIcon(android.R.drawable.ic_menu_delete)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                HashMap<String, String> params = new HashMap<>();
                                params.put("id", journalItem.id);
                                GlobalApp.getInstance().restClient.api().deleteJournal(params).enqueue(new Callback<JournalItem>() {
                                    @Override
                                    public void onResponse(@NonNull Call<JournalItem> call, @NonNull Response<JournalItem> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<JournalItem> call, @NonNull Throwable t) {
                                        t.printStackTrace();
                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                        .show();
            }
        });

        try {
            journalItem = (JournalItem) getIntent().getSerializableExtra("journalItem");
//            activityBinding.ivImage.setImageURI(Uri.parse(journalItem.imageUrl));
            RequestOptions myOptions = new RequestOptions().error(R.mipmap.ic_launcher);
            Glide.with(mActivity).load(journalItem.imageUrl).apply(myOptions).into(activityBinding.ivImage);
            activityBinding.etContent.setText(journalItem.content);
            Utils.getInstance().setActionbar(mActivity, "습관일기 보기 및 업데이트", true);
            isUpdate = true;
            activityBinding.btnDelete.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            journalItem = new JournalItem();
            e.printStackTrace();
        }
    }


    void showDialog() {
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }

    void dismisslDialog() {
        dialog.dismiss();
    }



    public void uploadWithTransferUtility(String path) {
        showDialog();
        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(GlobalApp.getInstance())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .build();

        final String uploadKey = GlobalApp.getInstance().userItem.id + "_" + new Random().nextInt(999999999) + "_profile_image.jpg";
        TransferObserver uploadObserver =
                transferUtility.upload(
                        "public/" + uploadKey,
                        new File(path), CannedAccessControlList.PublicRead);

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    journalItem.imageUrl = AppConstants.S3_URL + "/" + uploadKey;
                    activityBinding.ivImage.setImageURI(Uri.parse(selectedImage.getPath()));
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

    void complete() {
        showDialog();
        HashMap<String, String> params = new HashMap<>();
        params.put("userId", GlobalApp.getInstance().userItem.id);
        params.put("content", activityBinding.etContent.getText().toString());
        try {
            params.put("imageUrl", journalItem.imageUrl != null ? journalItem.imageUrl : "");
        } catch (Exception e) {
            params.put("imageUrl", "");
        }
        params.put("date", activityBinding.datePicker.getYear() + "-" + (activityBinding.datePicker.getMonth()+1) + "-" + activityBinding.datePicker.getDayOfMonth());

        if(isUpdate) {
            params.put("id", journalItem.id);
            GlobalApp.getInstance().restClient.api().updateJournal(params).enqueue(new Callback<JournalItem>()
            {
                @Override
                public void onResponse(@NonNull Call<JournalItem> call, @NonNull Response<JournalItem> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        Snackbar.make(activityBinding.ivImage, "수정되었습니다.", Snackbar.LENGTH_SHORT).show();
                    }
                    dismisslDialog();
                }

                @Override
                public void onFailure(@NonNull Call<JournalItem> call, @NonNull Throwable t)
                {
                    dismisslDialog();
                    t.printStackTrace();
                }
            });
        } else {
            GlobalApp.getInstance().restClient.api().createJournal(params).enqueue(new Callback<JournalItem>()
            {
                @Override
                public void onResponse(@NonNull Call<JournalItem> call, @NonNull Response<JournalItem> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        finish();
                    }
                    dismisslDialog();
                }

                @Override
                public void onFailure(@NonNull Call<JournalItem> call, @NonNull Throwable t)
                {
                    dismisslDialog();
                    t.printStackTrace();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        Log.e("asd", "2222222222");
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            selectedImage = ImagePicker.getFirstImageOrNull(data);
//            printImages(image);
            uploadWithTransferUtility(selectedImage.getPath());
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            complete();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

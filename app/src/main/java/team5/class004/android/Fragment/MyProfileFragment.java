package team5.class004.android.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team5.class004.android.GlobalApp;
import team5.class004.android.R;
import team5.class004.android.activity.HabitDetailActivity;
import team5.class004.android.activity.LoginActivity;
import team5.class004.android.activity.MainActivity;
import team5.class004.android.databinding.FragmentHabitListBinding;
import team5.class004.android.databinding.FragmentMyProfileBinding;
import team5.class004.android.databinding.ItemHabitListBinding;
import team5.class004.android.interfaces.AppConstants;
import team5.class004.android.model.HabitItem;
import team5.class004.android.model.UserItem;
import team5.class004.android.widget.LoadingDialog;

public class MyProfileFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View rootView;
    FragmentMyProfileBinding fragmentBinding;
    Activity mActivity;
    LoadingDialog dialog;
    Random rand = new Random();

    public MyProfileFragment()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        dialog = new LoadingDialog(mActivity);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile, container, false);
        rootView = fragmentBinding.getRoot();

        refresh();

        fragmentBinding.swipeRefreshLayout.setOnRefreshListener(this);

        fragmentBinding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("로그아웃");
                alert.setMessage("로그아웃 할까요?");
                alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });

                alert.setNegativeButton("아니오", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });
        fragmentBinding.btnRemoveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
                alert.setTitle("회원탈퇴");
                alert.setMessage("탈퇴할까요?");
                alert.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAccount();
                    }
                });

                alert.setNegativeButton("아니오", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();
            }
        });
        fragmentBinding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.create(MyProfileFragment.this).start();
            }
        });
        return rootView;
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

    private void printImages(Image images) {
        if (images == null) {
            Log.e("asd", "asddddddddd");
            return;
        }

        StringBuilder stringBuffer = new StringBuilder();
        stringBuffer.append(images.getPath()).append("\n");
//        fragmentBinding.printImage.setText(stringBuffer.toString());
//        fragmentBinding.printImage.setOnClickListener(v -> ImageViewerActivity.start(MainActivity.this, images));
    }

    public void uploadWithTransferUtility(String path) {
        showDialog();
        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(GlobalApp.getInstance())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(new AmazonS3Client(AWSMobileClient.getInstance().getCredentialsProvider()))
                        .build();

        final String uploadKey = GlobalApp.getInstance().userItem.id + "_" + rand.nextInt(999999999) + "_profile_image.jpg";
        TransferObserver uploadObserver =
                transferUtility.upload(
                        "public/" + uploadKey,
                        new File(path), CannedAccessControlList.PublicRead);

        // Attach a listener to the observer to get state update and progress notifications
        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    Log.e("asd", "aaaaaaaaaaaaaaa");
                    // Handle a completed upload.
                    HashMap<String, String> params = new HashMap<>();
                    params.put("id", GlobalApp.getInstance().userItem.id);
                    params.put("email", fragmentBinding.etEmail.getText().toString());
                    params.put("password", fragmentBinding.etPassword.getText().toString());
                    params.put("name", fragmentBinding.etName.getText().toString());
                    params.put("phone", fragmentBinding.etPhoneNumber.getText().toString());
                    params.put("introduce", fragmentBinding.etIntroduce.getText().toString());
                    params.put("profileImagePath", AppConstants.S3_URL + "/" + uploadKey);
                    GlobalApp.getInstance().restClient.api().updateUser(params).enqueue(new Callback<UserItem>()
                    {
                        @Override
                        public void onResponse(@NonNull Call<UserItem> call, @NonNull Response<UserItem> response)
                        {
                            if (response.isSuccessful() && response.body() != null)
                            {
                                GlobalApp.getInstance().userItem = response.body();

                                fragmentBinding.etEmail.setText(GlobalApp.getInstance().userItem.email);
                                fragmentBinding.etPassword.setText(GlobalApp.getInstance().userItem.password);
                                fragmentBinding.etName.setText(GlobalApp.getInstance().userItem.name);
                                fragmentBinding.etPhoneNumber.setText(GlobalApp.getInstance().userItem.phone);
                                fragmentBinding.etIntroduce.setText(GlobalApp.getInstance().userItem.introduce);
                                RequestOptions myOptions = new RequestOptions().error(R.mipmap.ic_launcher);
                                Glide.with(mActivity).load(GlobalApp.getInstance().userItem.profileImagePath).apply(myOptions).into(fragmentBinding.ivProfile);
                                GlobalApp.getInstance().prefs.edit().putString("user", new Gson().toJson(GlobalApp.getInstance().userItem)).apply();
                            }
                            dismisslDialog();
                            fragmentBinding.swipeRefreshLayout.setRefreshing(false);
                            Snackbar.make(fragmentBinding.getRoot(), "업데이트 되었습니다.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(@NonNull Call<UserItem> call, @NonNull Throwable t)
                        {
                            dismisslDialog();
                            fragmentBinding.swipeRefreshLayout.setRefreshing(false);
                            Snackbar.make(fragmentBinding.getRoot(), "업데이트 중 오류 발생", Toast.LENGTH_SHORT).show();
                            t.printStackTrace();
                        }
                    });
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

    void logout() {
        GlobalApp.getInstance().userItem = null;
        GlobalApp.getInstance().prefs.edit().remove("user").apply();
        Toast.makeText(mActivity, "로그아웃 되었습니다.", Toast.LENGTH_LONG).show();
        mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
        mActivity.finish();
    }

    void removeAccount() {
        showDialog();
        HashMap<String, String> params = new HashMap<>();
        params.put("email", GlobalApp.getInstance().userItem.email);
        GlobalApp.getInstance().restClient.api().removeUser(params).enqueue(new Callback<UserItem>()
        {
            @Override
            public void onResponse(@NonNull Call<UserItem> call, @NonNull Response<UserItem> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    GlobalApp.getInstance().userItem = null;
                    GlobalApp.getInstance().prefs.edit().remove("user").apply();
                    Toast.makeText(mActivity, "탈퇴 완료 되었습니다.", Toast.LENGTH_LONG).show();
                    mActivity.startActivity(new Intent(mActivity, LoginActivity.class));
                    mActivity.finish();
                }
                dismisslDialog();
            }

            @Override
            public void onFailure(@NonNull Call<UserItem> call, @NonNull Throwable t)
            {
                dismisslDialog();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    void refresh() {
        showDialog();
        HashMap<String, String> params = new HashMap<>();
        params.put("email", GlobalApp.getInstance().userItem.email);
        params.put("password", GlobalApp.getInstance().userItem.password);
        GlobalApp.getInstance().restClient.api().getUser(params).enqueue(new Callback<UserItem>()
        {
            @Override
            public void onResponse(@NonNull Call<UserItem> call, @NonNull Response<UserItem> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    if(response.body().email == null) {
                        startActivity(new Intent(mActivity, LoginActivity.class));
                        mActivity.finish();
                    } else if(!response.body().auth) {
                        startActivity(new Intent(mActivity, LoginActivity.class));
                        mActivity.finish();
                    } else {
                        GlobalApp.getInstance().userItem = response.body();
                        GlobalApp.getInstance().prefs.edit().putString("user", new Gson().toJson(GlobalApp.getInstance().userItem)).apply();
                        fragmentBinding.etEmail.setText(GlobalApp.getInstance().userItem.email);
                        fragmentBinding.etPassword.setText(GlobalApp.getInstance().userItem.password);
                        fragmentBinding.etName.setText(GlobalApp.getInstance().userItem.name);
                        fragmentBinding.etPhoneNumber.setText(GlobalApp.getInstance().userItem.phone);
                        fragmentBinding.etIntroduce.setText(GlobalApp.getInstance().userItem.introduce);
                        RequestOptions myOptions = new RequestOptions().error(R.mipmap.ic_launcher);
                        Glide.with(mActivity).load(GlobalApp.getInstance().userItem.profileImagePath).apply(myOptions).into(fragmentBinding.ivProfile);
                    }
                }
                dismisslDialog();
            }

            @Override
            public void onFailure(@NonNull Call<UserItem> call, @NonNull Throwable t)
            {
                try {
                    dismisslDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void update() {
        showDialog();
        HashMap<String, String> params = new HashMap<>();
        params.put("id", GlobalApp.getInstance().userItem.id);
        params.put("email", fragmentBinding.etEmail.getText().toString());
        params.put("password", fragmentBinding.etPassword.getText().toString());
        params.put("name", fragmentBinding.etName.getText().toString());
        params.put("phone", fragmentBinding.etPhoneNumber.getText().toString());
        params.put("introduce", fragmentBinding.etIntroduce.getText().toString());
        GlobalApp.getInstance().restClient.api().updateUser(params).enqueue(new Callback<UserItem>()
        {
            @Override
            public void onResponse(@NonNull Call<UserItem> call, @NonNull Response<UserItem> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    GlobalApp.getInstance().userItem = response.body();

                    fragmentBinding.etEmail.setText(GlobalApp.getInstance().userItem.email);
                    fragmentBinding.etPassword.setText(GlobalApp.getInstance().userItem.password);
                    fragmentBinding.etName.setText(GlobalApp.getInstance().userItem.name);
                    fragmentBinding.etPhoneNumber.setText(GlobalApp.getInstance().userItem.phone);
                    fragmentBinding.etIntroduce.setText(GlobalApp.getInstance().userItem.introduce);
                    RequestOptions myOptions = new RequestOptions().error(R.mipmap.ic_launcher);
                    Glide.with(mActivity).load(GlobalApp.getInstance().userItem.profileImagePath).apply(myOptions).into(fragmentBinding.ivProfile);

                    GlobalApp.getInstance().prefs.edit().putString("user", new Gson().toJson(GlobalApp.getInstance().userItem)).apply();
                }
                dismisslDialog();
                fragmentBinding.swipeRefreshLayout.setRefreshing(false);
                Snackbar.make(fragmentBinding.getRoot(), "업데이트 되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<UserItem> call, @NonNull Throwable t)
            {
                dismisslDialog();
                fragmentBinding.swipeRefreshLayout.setRefreshing(false);
                Snackbar.make(fragmentBinding.getRoot(), "업데이트 중 오류 발생", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_my_profile_options, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.update) {
            update();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void showDialog() {
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
        fragmentBinding.swipeRefreshLayout.setRefreshing(false);
    }

    void dismisslDialog() {
        dialog.dismiss();
        fragmentBinding.swipeRefreshLayout.setRefreshing(false);
    }

}

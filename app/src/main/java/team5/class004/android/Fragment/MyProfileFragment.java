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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team5.class004.android.GlobalApp;
import team5.class004.android.R;
import team5.class004.android.activity.HabitDetailActivity;
import team5.class004.android.activity.LoginActivity;
import team5.class004.android.databinding.FragmentHabitListBinding;
import team5.class004.android.databinding.FragmentMyProfileBinding;
import team5.class004.android.databinding.ItemHabitListBinding;
import team5.class004.android.model.HabitItem;
import team5.class004.android.model.UserItem;
import team5.class004.android.widget.LoadingDialog;

public class MyProfileFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View rootView;
    FragmentMyProfileBinding fragmentBinding;
    Activity mActivity;
    LoadingDialog dialog;


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
        refresh();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile, container, false);
        rootView = fragmentBinding.getRoot();

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
        return rootView;
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
        update();
    }

    void refresh() {
        fragmentBinding.swipeRefreshLayout.setRefreshing(true);
        HashMap<String, String> params = new HashMap<>();
        params.put("email", GlobalApp.getInstance().userItem.email);
        GlobalApp.getInstance().restClient.api().getMyHabits(params).enqueue(new Callback<ArrayList<HabitItem>>()
        {
            @Override
            public void onResponse(@NonNull Call<ArrayList<HabitItem>> call, @NonNull Response<ArrayList<HabitItem>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {

                }
                fragmentBinding.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<HabitItem>> call, @NonNull Throwable t)
            {
                fragmentBinding.swipeRefreshLayout.setRefreshing(false);
                t.printStackTrace();
            }
        });

    }

    void update() {
        showDialog();
        HashMap<String, String> params = new HashMap<>();
//        params.put("level", String.valueOf(boxLevel));
        GlobalApp.getInstance().restClient.api().getMyHabits(params).enqueue(new Callback<ArrayList<HabitItem>>()
        {
            @Override
            public void onResponse(@NonNull Call<ArrayList<HabitItem>> call, @NonNull Response<ArrayList<HabitItem>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {

                }
                dismisslDialog();
                Snackbar.make(fragmentBinding.getRoot(), "업데이트 되었습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<HabitItem>> call, @NonNull Throwable t)
            {
                dismisslDialog();
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
    }

    void dismisslDialog() {
        dialog.dismiss();
    }

}

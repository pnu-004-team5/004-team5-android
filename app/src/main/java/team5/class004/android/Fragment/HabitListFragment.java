package team5.class004.android.fragment;

import android.app.Activity;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team5.class004.android.GlobalApp;
import team5.class004.android.R;
import team5.class004.android.activity.HabitDetailActivity;
import team5.class004.android.databinding.FragmentHabitListBinding;
import team5.class004.android.databinding.ItemHabitListBinding;
import team5.class004.android.model.AbstractResponse;
import team5.class004.android.model.HabitItem;
import team5.class004.android.widget.LoadingDialog;
import team5.class004.android.widget.SmoothCheckBox;

public class HabitListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View rootView;
    FragmentHabitListBinding fragmentBinding;
    Activity mActivity;
    MyRecyclerAdapter adapter = new MyRecyclerAdapter();
    ArrayList<HabitItem> habitItems = new ArrayList<>();
    LoadingDialog dialog;


    public HabitListFragment()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        dialog = new LoadingDialog(mActivity);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_habit_list, container, false);
        fragmentBinding.setFragment(this);
        rootView = fragmentBinding.getRoot();

        fragmentBinding.swipeRefreshLayout.setOnRefreshListener(HabitListFragment.this);

        fragmentBinding.recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        fragmentBinding.recyclerView.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(fragmentBinding.recyclerView);
//        for(int i = 0; i < 50; i++) {
//            habitItems.add(new HabitItem());
//        }

        return rootView;
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    void refresh() {
        fragmentBinding.swipeRefreshLayout.setRefreshing(true);
        HashMap<String, String> params = new HashMap<>();
//        params.put("level", String.valueOf(boxLevel));
        GlobalApp.getInstance().restClient.api().getMyHabits(params).enqueue(new Callback<ArrayList<HabitItem>>()
        {
            @Override
            public void onResponse(@NonNull Call<ArrayList<HabitItem>> call, @NonNull Response<ArrayList<HabitItem>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    habitItems = response.body();
                    fragmentBinding.recyclerView.getAdapter().notifyDataSetChanged();
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

    void showDialog() {
        dialog.setCancelable(false);
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.show();
    }

    void dismisslDialog() {
        dialog.dismiss();
    }

    public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

        public MyRecyclerAdapter() {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_habit_list, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            try {
                int color = Color.parseColor(habitItems.get(position).color);
                holder.itemBinding.container.setBackgroundColor(color);
                holder.itemBinding.tvHabitName.setText(habitItems.get(position).name);
                holder.itemBinding.tvHabitDate.setText(habitItems.get(position).fromDate + " ~ " + habitItems.get(position).toDate);
                holder.itemBinding.tvHabitMemo.setText(habitItems.get(position).memo);

                JSONArray completeDateJsonArr = new JSONArray(habitItems.get(position).completeDate);
                Calendar calendar = Calendar.getInstance();
                java.util.Date date = calendar.getTime();
                String today = (new SimpleDateFormat("yyyy-MM-dd").format(date));

                boolean found = false;
                for(int i = 0; i < completeDateJsonArr.length(); i++) {
                    if(completeDateJsonArr.get(i).equals(today)) {
                        found = true;
                    }
                }

                holder.itemBinding.scb.setOnCheckedChangeListener(null);
                if(found) {
                    holder.itemBinding.scb.setChecked(true);
                } else {
                    holder.itemBinding.scb.setChecked(false);
                }
                holder.itemBinding.scb.setOnCheckedChangeListener(holder);
            } catch (Exception e) {
//                holder.itemBinding.container.setBackgroundColor(colors.get(position % colors.size()));
            }
        }

        @Override
        public int getItemCount() {
            return habitItems.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, SmoothCheckBox.OnCheckedChangeListener {
            public ItemHabitListBinding itemBinding;

            public ViewHolder(View itemView){
                super(itemView);

                itemBinding = DataBindingUtil.bind(itemView);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
                itemBinding.scb.setOnCheckedChangeListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Intent intent = new Intent(getContext(), HabitDetailActivity.class);
                intent.putExtra("habitItem", habitItems.get(position));
                startActivity(intent);
            }

            @Override
            public boolean onLongClick(View view) {
//                startActivity(new Intent(getContext(), HabitDetailActivity.class));
                return true;
            }

            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, final boolean isChecked) {
                final int position = getAdapterPosition();
                showDialog();

                Calendar calendar = Calendar.getInstance();
                java.util.Date date = calendar.getTime();
                String today = (new SimpleDateFormat("yyyy-MM-dd").format(date));

                HashMap<String, String> params = new HashMap<>();
                params.put("id", habitItems.get(position).id);
                params.put("targetDate", today);
                GlobalApp.getInstance().restClient.api().doneMyHabit(params).enqueue(new Callback<HabitItem>()
                {
                    @Override
                    public void onResponse(@NonNull Call<HabitItem> call, @NonNull Response<HabitItem> response)
                    {
                        if (response.isSuccessful() && response.body() != null)
                        {
                            habitItems.set(position, response.body());
                            adapter.notifyDataSetChanged();
                        }
                        dismisslDialog();
                        Snackbar.make(fragmentBinding.getRoot(), isChecked ? "완료되었습니다." : "미완료되었습니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(@NonNull Call<HabitItem> call, @NonNull Throwable t)
                    {
                        Snackbar.make(fragmentBinding.getRoot(), "오류가 발생하여 작업에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        dismisslDialog();
                        t.printStackTrace();
                    }
                });
            }
        }
    }

    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Toast.makeText(mActivity, "on Move", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            final int position = viewHolder.getAdapterPosition();
            showDialog();
            HashMap<String, String> params = new HashMap<>();
            params.put("id", habitItems.get(position).id);
            GlobalApp.getInstance().restClient.api().deleteMyHabit(params).enqueue(new Callback<HabitItem>()
            {
                @Override
                public void onResponse(@NonNull Call<HabitItem> call, @NonNull Response<HabitItem> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        habitItems.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                    dismisslDialog();
                    Snackbar.make(fragmentBinding.getRoot(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(@NonNull Call<HabitItem> call, @NonNull Throwable t)
                {
                    Snackbar.make(fragmentBinding.getRoot(), "오류가 발생하여 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    dismisslDialog();
                    t.printStackTrace();
                }
            });
        }
    };
}

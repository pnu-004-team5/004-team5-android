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
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team5.class004.android.GlobalApp;
import team5.class004.android.R;
import team5.class004.android.activity.BoardDocumentCreateActivity;
import team5.class004.android.activity.CameraActivity;
import team5.class004.android.activity.HabitDetailActivity;
import team5.class004.android.activity.MainActivity;
import team5.class004.android.databinding.FragmentExploreBinding;
import team5.class004.android.databinding.FragmentHabitListBinding;
import team5.class004.android.databinding.ItemBoardDocumentBinding;
import team5.class004.android.databinding.ItemHabitListBinding;
import team5.class004.android.model.BoardDocumentItem;
import team5.class004.android.model.BoardDocumentItem;
import team5.class004.android.widget.LoadingDialog;
import team5.class004.android.widget.SmoothCheckBox;

public class ExploreFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View rootView;
    FragmentExploreBinding fragmentBinding;
    MainActivity mActivity;
    MyRecyclerAdapter adapter = new MyRecyclerAdapter();
    ArrayList<BoardDocumentItem> boardDocumentItems = new ArrayList<>();
    LoadingDialog dialog;


    public ExploreFragment()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)getActivity();
        dialog = new LoadingDialog(mActivity);


        setHasOptionsMenu(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();
    }

    @Override
    public void onPause() {
        super.onPause();

        refresh();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_explore, container, false);
        rootView = fragmentBinding.getRoot();

        fragmentBinding.swipeRefreshLayout.setOnRefreshListener(ExploreFragment.this);

        fragmentBinding.recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        fragmentBinding.recyclerView.setAdapter(adapter);

        fragmentBinding.fabVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, CameraActivity.class));
            }
        });

        return rootView;
    }

    @Override
    public void onRefresh() {
        refresh();
    }

//    void refresh() {
////        fragmentBinding.swipeRefreshLayout.setRefreshing(true);
////        HashMap<String, String> params = new HashMap<>();
//////        params.put("level", String.valueOf(boxLevel));
////        GlobalApp.getInstance().restClient.api().getMyHabits(params).enqueue(new Callback<ArrayList<BoardDocumentItem>>()
////        {
////            @Override
////            public void onResponse(@NonNull Call<ArrayList<BoardDocumentItem>> call, @NonNull Response<ArrayList<BoardDocumentItem>> response)
////            {
////                if (response.isSuccessful() && response.body() != null)
////                {
////                    boardDocumentItems = response.body();
////                    fragmentBinding.recyclerView.getAdapter().notifyDataSetChanged();
////                }
////                fragmentBinding.swipeRefreshLayout.setRefreshing(false);
////            }
////
////            @Override
////            public void onFailure(@NonNull Call<ArrayList<BoardDocumentItem>> call, @NonNull Throwable t)
////            {
////                fragmentBinding.swipeRefreshLayout.setRefreshing(false);
////                t.printStackTrace();
////            }
////        });
//
//    }

    void refresh() {
        fragmentBinding.swipeRefreshLayout.setRefreshing(true);
        HashMap<String, String> params = new HashMap<>();
//        params.put("level", String.valueOf(boxLevel));
        GlobalApp.getInstance().restClient.api().getBoardDocuments(params).enqueue(new Callback<ArrayList<BoardDocumentItem>>()
        {
            @Override
            public void onResponse(@NonNull Call<ArrayList<BoardDocumentItem>> call, @NonNull Response<ArrayList<BoardDocumentItem>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    boardDocumentItems = response.body();
                    fragmentBinding.recyclerView.getAdapter().notifyDataSetChanged();
                }
                fragmentBinding.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<BoardDocumentItem>> call, @NonNull Throwable t)
            {
                fragmentBinding.swipeRefreshLayout.setRefreshing(false);
                t.printStackTrace();
            }
        });

    }

    public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

        public MyRecyclerAdapter() {

        }

        @Override
        public MyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_board_document, viewGroup, false);
            return new MyRecyclerAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyRecyclerAdapter.ViewHolder holder, int position) {
            try {

                holder.itemBinding.myVideoView.setVideoPath("http://techslides.com/demos/sample-videos/small.mp4");
//                int color = Color.parseColor(boardDocumentItems.get(position).color);
//                holder.itemBinding.container.setBackgroundColor(color);
//                holder.itemBinding.tvHabitName.setText(boardDocumentItems.get(position).name);
//                holder.itemBinding.tvHabitDate.setText(boardDocumentItems.get(position).fromDate + " ~ " + boardDocumentItems.get(position).toDate);
//                holder.itemBinding.tvHabitMemo.setText(boardDocumentItems.get(position).memo);
//
//                JSONArray completeDateJsonArr = new JSONArray(boardDocumentItems.get(position).completeDate);
//                Calendar calendar = Calendar.getInstance();
//                java.util.Date date = calendar.getTime();
//                String today = (new SimpleDateFormat("yyyy-MM-dd").format(date));
//
//                boolean found = false;
//                for(int i = 0; i < completeDateJsonArr.length(); i++) {
//                    if(completeDateJsonArr.get(i).equals(today)) {
//                        found = true;
//                    }
//                }
//
//                holder.itemBinding.scb.setOnCheckedChangeListener(null);
//                if(found) {
//                    holder.itemBinding.scb.setChecked(true);
//                } else {
//                    holder.itemBinding.scb.setChecked(false);
//                }
//                holder.itemBinding.scb.setOnCheckedChangeListener(holder);
            } catch (Exception e) {
//                holder.itemBinding.container.setBackgroundColor(colors.get(position % colors.size()));
            }
        }

        @Override
        public int getItemCount() {
            return boardDocumentItems.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, SmoothCheckBox.OnCheckedChangeListener {
            public ItemBoardDocumentBinding itemBinding;

            public ViewHolder(View itemView){
                super(itemView);

                itemBinding = DataBindingUtil.bind(itemView);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
//                itemBinding.scb.setOnCheckedChangeListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Intent intent = new Intent(getContext(), HabitDetailActivity.class);
                intent.putExtra("habitItem", boardDocumentItems.get(position));
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

            }
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

}

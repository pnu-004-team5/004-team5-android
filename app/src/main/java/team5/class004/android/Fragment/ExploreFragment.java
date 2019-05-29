package team5.class004.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.media.MediaPlayer;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

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

public class ExploreFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SurfaceHolder.Callback {
    View rootView;
    FragmentExploreBinding fragmentBinding;
    MainActivity mActivity;
    MyRecyclerAdapter adapter = new MyRecyclerAdapter();
    ArrayList<BoardDocumentItem> boardDocumentItems = new ArrayList<>();
    LoadingDialog dialog;
    boolean hasActiveHolder;
    MediaPlayer mediaPlayer;
//    String currentDataSource = "http://techslides.com/demos/sample-videos/small.mp4";
    String currentDataSource;
    int isVideoRunning = 0;
    SurfaceView thisSurface;

    public ExploreFragment()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)getActivity();
        dialog = new LoadingDialog(mActivity);
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.e("ready", currentDataSource);
                mp.start();
            }
        });
//        mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
//
//            @Override
//            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//
////                setFitToFillAspectRatio(mp, width, height);
//
//            }
//        });
        setHasOptionsMenu(false);
    }
//    private void setFitToFillAspectRatio(MediaPlayer mp, int videoWidth, int videoHeight)
//    {
//        if(mp != null)
//        {
//            Integer screenWidth = mActivity.getWindowManager().getDefaultDisplay().getWidth();
//            Integer screenHeight = mActivity.getWindowManager().getDefaultDisplay().getHeight();
//            android.view.ViewGroup.LayoutParams videoParams = getLayoutParams();
//
//
//            if (videoWidth > videoHeight)
//            {
//                videoParams.width = screenWidth;
//                videoParams.height = screenWidth * videoHeight / videoWidth;
//            }
//            else
//            {
//                videoParams.width = screenHeight * videoWidth / videoHeight;
//                videoParams.height = screenHeight;
//            }
//
//
//            setLayoutParams(videoParams);
//        }
//    }

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

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("dasd", "surface destryed");
//        if(isVideoRunning == 1) {
//            mediaPlayer.setDisplay(null);
//        }
//                    mediaPlayer.prepare();
//                    mediaPlayer.start();
//        synchronized (this) {
//            hasActiveHolder = false;
//
//            synchronized(this)          {
//                this.notifyAll();
//            }
//        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("dasd", "surface created");
        try {
            Log.e("dasd", currentDataSource);
            mediaPlayer.setDataSource(currentDataSource);
            mediaPlayer.setDisplay(holder);
            mediaPlayer.prepareAsync();
            mediaPlayer.setLooping(true);
//            mediaPlayer.start();
        }catch (Exception e) {
            e.printStackTrace();
        }
//        synchronized (this) {
//            hasActiveHolder = true;
//            this.notifyAll();
//        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


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
//                if(isVideoRunning == 0) {
//
////                currentDataSource = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4";
//                }
                isVideoRunning = position;
                holder.itemBinding.videoSurfaceView.getHolder().addCallback(ExploreFragment.this);
                thisSurface = holder.itemBinding.videoSurfaceView;

                currentDataSource = boardDocumentItems.get(position).videoUrl;
                RequestOptions myOptions = new RequestOptions().error(R.mipmap.ic_launcher);
                Glide.with(mActivity).load(GlobalApp.getInstance().userItem.profileImagePath).apply(myOptions).into(holder.itemBinding.ivProfile);
                holder.itemBinding.tvNickname.setText(GlobalApp.getInstance().userItem.name);
                holder.itemBinding.tvContent.setText(boardDocumentItems.get(position).content);
//                synchronized (this) {
//                    while (!hasActiveHolder) {
//                        try {
//                            this.wait();
//                        } catch (InterruptedException e) {
//                            //Print something
//                        }
//                    }
//                    mediaPlayer.setDisplay(holder.itemBinding.videoSurfaceView.getHolder());
//                    mediaPlayer.prepare();
//                    mediaPlayer.start();
//                }
//                mediaPlayer.setDisplay(holder.itemBinding.videoSurfaceView.getHolder());
//                mediaPlayer.prepare();
// mediaPlayer.prepareAsync();

//                holder.itemBinding.myVideoView.setVideoPath("http://techslides.com/demos/sample-videos/small.mp4");

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
                e.printStackTrace();
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
//                Intent intent = new Intent(getContext(), HabitDetailActivity.class);
//                intent.putExtra("habitItem", boardDocumentItems.get(position));
//                startActivity(intent);
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

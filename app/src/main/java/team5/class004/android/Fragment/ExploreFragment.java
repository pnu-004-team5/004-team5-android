package team5.class004.android.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team5.class004.android.GlobalApp;
import team5.class004.android.R;
import team5.class004.android.activity.CameraActivity;
import team5.class004.android.activity.MainActivity;
import team5.class004.android.databinding.FragmentExploreBinding;
import team5.class004.android.databinding.ItemBoardCommentBinding;
import team5.class004.android.databinding.ItemBoardDocumentBinding;
import team5.class004.android.model.BoardCommentItem;
import team5.class004.android.model.BoardDocumentItem;
import team5.class004.android.model.HabitItem;
import team5.class004.android.utils.ExploreBottomSheetDialog;
import team5.class004.android.widget.LoadingDialog;
import team5.class004.android.widget.SmoothCheckBox;

public class ExploreFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SurfaceHolder.Callback {
    View rootView;
    FragmentExploreBinding fragmentBinding;
    MainActivity mActivity;
    MyRecyclerAdapter adapter = new MyRecyclerAdapter();
    CommentRecyclerAdapter commentRecyclerAdapter = new CommentRecyclerAdapter();
    ArrayList<BoardDocumentItem> boardDocumentItems = new ArrayList<>();
    ArrayList<BoardCommentItem> boardCommentOriginalItems = new ArrayList<>();
    ArrayList<BoardCommentItem> boardCommentItems = new ArrayList<>();
    LoadingDialog dialog;
    boolean hasActiveHolder;
    MediaPlayer mediaPlayer;
    //    String currentDataSource = "http://techslides.com/demos/sample-videos/small.mp4";
    String currentDataSource;
    int isVideoRunning = 0;
    SurfaceView thisSurface;
    int currentSelectedDocument;
    Handler mHandler = new Handler();
    boolean isHandlerRunning = false;
    public static String board = "total";


    public ExploreFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
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
//            Log.e("dasd", currentDataSource);
//            mediaPlayer.setDataSource(currentDataSource);
//            mediaPlayer.setDisplay(holder);
//            mediaPlayer.prepareAsync();
//            mediaPlayer.setLooping(true);
//            mediaPlayer.start();
        } catch (Exception e) {
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

        fragmentBinding.recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));
        fragmentBinding.recyclerView.setAdapter(adapter);


        fragmentBinding.fabVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, CameraActivity.class));
            }
        });

        fragmentBinding.btnTotal.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorSecondaryDark));
        fragmentBinding.btnTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                board = "total";
                fragmentBinding.btnTotal.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorSecondaryDark));
                fragmentBinding.btnBook.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
                fragmentBinding.btnExercise.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
                fragmentBinding.btnWakeup.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
                refresh();
            }
        });
        fragmentBinding.btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                board = "book";
                fragmentBinding.btnBook.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorSecondaryDark));
                fragmentBinding.btnExercise.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
                fragmentBinding.btnTotal.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
                fragmentBinding.btnWakeup.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
                refresh();
            }
        });
        fragmentBinding.btnExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                board = "exercise";
                fragmentBinding.btnExercise.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorSecondaryDark));
                fragmentBinding.btnWakeup.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
                fragmentBinding.btnBook.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
                fragmentBinding.btnTotal.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
                refresh();
            }
        });
        fragmentBinding.btnWakeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                board = "wakeup";
                fragmentBinding.btnWakeup.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorSecondaryDark));
                fragmentBinding.btnExercise.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
                fragmentBinding.btnBook.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
                fragmentBinding.btnTotal.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorAccent));
                refresh();
            }
        });


//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
//        itemTouchHelper.attachToRecyclerView(fragmentBinding.recyclerView);

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
        params.put("board", board);
        GlobalApp.getInstance().restClient.api().getBoardDocuments(params).enqueue(new Callback<ArrayList<BoardDocumentItem>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<BoardDocumentItem>> call, @NonNull Response<ArrayList<BoardDocumentItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boardDocumentItems = response.body();
                    fragmentBinding.recyclerView.getAdapter().notifyDataSetChanged();
                }
                fragmentBinding.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<BoardDocumentItem>> call, @NonNull Throwable t) {
                fragmentBinding.swipeRefreshLayout.setRefreshing(false);
                t.printStackTrace();
            }
        });

    }

    final Runnable mAddComment = new Runnable() {
        @Override
        public void run() {
            try {
                boardCommentItems.add(0, boardCommentOriginalItems.get(boardCommentItems.size()));
                commentRecyclerAdapter.notifyDataSetChanged();
                if(boardCommentOriginalItems.size() != boardCommentItems.size())
                    mHandler.postDelayed(mAddComment, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

        public MyRecyclerAdapter() {

        }

        @Override
        public MyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_board_document, viewGroup, false);
            return new MyRecyclerAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyRecyclerAdapter.ViewHolder holder, int position) {
            try {
//                if(isVideoRunning == 0) {
//
////                currentDataSource = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4";
//                }
                //미디어컨트롤러 추가하는 부분
//                MediaController controller = new MediaController(mActivity);
//                holder.itemBinding.videoView.setMediaController(controller);
                //비디오뷰 포커스를 요청함
//                holder.itemBinding.videoView.requestFocus();
//                holder.itemBinding.videoView.seekTo(1);
//                holder.itemBinding.videoView.start();
//
//
//
//                if(getItemCount() > 0) {
//                    if(holder.itemBinding.videoView.getTag() == null) {
//                        holder.itemBinding.videoView.setVideoURI(Uri.parse(boardDocumentItems.get(position).videoUrl));
//                        holder.itemBinding.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                            @Override
//                            public void onCompletion(MediaPlayer mediaPlayer) {
//                                holder.itemBinding.videoView.start();
//                            }
//                        });
//                    }
//                    holder.itemBinding.videoView.setTag("true");
//                }

                SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(mActivity);
                player.setRepeatMode(Player.REPEAT_MODE_ALL);
                holder.itemBinding.exoPlayerView.setPlayer(player);
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mActivity,
                        Util.getUserAgent(mActivity, "habit"));
                MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(boardDocumentItems.get(position).videoUrl));
                player.prepare(videoSource);
                player.setPlayWhenReady(true);
                holder.itemBinding.exoPlayerView.hideController();


                isVideoRunning = position;
//                holder.itemBinding.videoSurfaceView.getHolder().addCallback(ExploreFragment.this);
//                thisSurface = holder.itemBinding.videoSurfaceView;

                currentDataSource = boardDocumentItems.get(position).videoUrl;
                RequestOptions myOptions = new RequestOptions().error(R.mipmap.ic_launcher);
                Glide.with(mActivity).load(GlobalApp.getInstance().userItem.profileImagePath).apply(myOptions).into(holder.itemBinding.ivProfile);
                holder.itemBinding.tvNickname.setText(GlobalApp.getInstance().userItem.name);
                holder.itemBinding.tvContent.setText(boardDocumentItems.get(position).content);


                holder.itemBinding.commentRecyclerView.setAdapter(commentRecyclerAdapter);
                HashMap<String, String> params = new HashMap<>();
                params.put("documentId", boardDocumentItems.get(position).id);
                GlobalApp.getInstance().restClient.api().getBoardComments(params).enqueue(new Callback<ArrayList<BoardCommentItem>>() {
                    @Override
                    public void onResponse(@NonNull Call<ArrayList<BoardCommentItem>> call, @NonNull Response<ArrayList<BoardCommentItem>> response) {
                        if (response.isSuccessful() && response.body() != null) {

                            mHandler.removeCallbacks(mAddComment);
                            boardCommentOriginalItems = response.body();
                            boardCommentItems.clear();
                            mAddComment.run();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ArrayList<BoardCommentItem>> call, @NonNull Throwable t) {
                        t.printStackTrace();
                    }
                });


                holder.itemBinding.btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("userId", GlobalApp.getInstance().userItem.id);
                        params.put("documentId", boardDocumentItems.get(currentSelectedDocument).id);
                        params.put("content", holder.itemBinding.etComment.getText().toString());
                        params.put("board", board);
                        GlobalApp.getInstance().restClient.api().createBoardComment(params).enqueue(new Callback<BoardCommentItem>() {
                            @Override
                            public void onResponse(@NonNull Call<BoardCommentItem> call, @NonNull Response<BoardCommentItem> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    holder.itemBinding.etComment.setText("");
                                    refresh();
                                    commentRecyclerAdapter.notifyDataSetChanged();
                                }
                                dismisslDialog();
                                Snackbar.make(fragmentBinding.getRoot(), "등록되었습니다.", Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(@NonNull Call<BoardCommentItem> call, @NonNull Throwable t) {
                                Snackbar.make(fragmentBinding.getRoot(), "오류가 발생하여 등록에 실패했습니다.", Snackbar.LENGTH_SHORT).show();
                                dismisslDialog();
                                t.printStackTrace();
                            }
                        });
                    }
                });
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

            public ViewHolder(View itemView) {
                super(itemView);

                itemBinding = DataBindingUtil.bind(itemView);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
                itemBinding.commentRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity, RecyclerView.VERTICAL, false));

//                itemBinding.scb.setOnCheckedChangeListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
//                Intent intent = new Intent(getContext(), HabitDetailActivity.class);
//                intent.putExtra("habitItem", boardDocumentItems.get(position));
//                startActivity(intent);
                currentSelectedDocument = getAdapterPosition();
                ExploreBottomSheetDialog bottomSheetDialog = ExploreBottomSheetDialog.getInstance();
                bottomSheetDialog.show(getFragmentManager(), "bottomSheet");
                bottomSheetDialog.setTargetFragment(ExploreFragment.this, 111);
                Bundle args = new Bundle();
                args.putString("boardDocumentItemId", boardDocumentItems.get(currentSelectedDocument).id);
                args.putString("boardDocumentItemContent", boardDocumentItems.get(currentSelectedDocument).content);
                bottomSheetDialog.setArguments(args);

            }

            @Override
            public boolean onLongClick(final View view) {
                view.findViewById(R.id.heart_container).animate().alpha(1);
                view.findViewById(R.id.heart).animate().scaleX(1).scaleY(1);
                new Handler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        view.findViewById(R.id.heart_container).animate().alpha(0);
                        view.findViewById(R.id.heart).animate().scaleX(0).scaleY(0);
                    }
                }, 2000);
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


    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            Toast.makeText(mActivity, "on Move", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (requestCode == 111) { // delete
            new AlertDialog.Builder(getActivity())
                    .setTitle("삭제")
                    .setMessage("정말 삭제하시겠습니까?")
                    .setIcon(android.R.drawable.ic_menu_delete)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("id", boardDocumentItems.get(currentSelectedDocument).id);
                            GlobalApp.getInstance().restClient.api().deleteBoardDocument(params).enqueue(new Callback<BoardDocumentItem>() {
                                @Override
                                public void onResponse(@NonNull Call<BoardDocumentItem> call, @NonNull Response<BoardDocumentItem> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        boardDocumentItems.remove(currentSelectedDocument);
                                        adapter.notifyDataSetChanged();
                                    }
                                    dismisslDialog();
                                    Snackbar.make(fragmentBinding.getRoot(), "삭제되었습니다.", Snackbar.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(@NonNull Call<BoardDocumentItem> call, @NonNull Throwable t) {
                                    Snackbar.make(fragmentBinding.getRoot(), "오류가 발생하여 삭제에 실패했습니다.", Snackbar.LENGTH_SHORT).show();
                                    dismisslDialog();
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

        super.onActivityResult(requestCode, resultCode, data);
    }






    public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder> {

        public CommentRecyclerAdapter() {

        }

        @Override
        public CommentRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_board_comment, viewGroup, false);
            return new CommentRecyclerAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final CommentRecyclerAdapter.ViewHolder holder, int position) {
            try {
                RequestOptions myOptions = new RequestOptions().error(R.mipmap.ic_launcher);
                Glide.with(mActivity).load(GlobalApp.getInstance().userItem.profileImagePath).apply(myOptions).into(holder.itemBinding.ivProfile);
                holder.itemBinding.tvNickname.setText(GlobalApp.getInstance().userItem.name);
                holder.itemBinding.tvContent.setText(boardCommentItems.get(position).content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return boardCommentItems.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder  {
            public ItemBoardCommentBinding itemBinding;

            public ViewHolder(View itemView) {
                super(itemView);

                itemBinding = DataBindingUtil.bind(itemView);
//                itemBinding.scb.setOnCheckedChangeListener(this);
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

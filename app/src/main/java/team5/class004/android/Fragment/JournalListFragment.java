package team5.class004.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.snackbar.Snackbar;

import org.jcodec.api.android.SequenceEncoder;
import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team5.class004.android.GlobalApp;
import team5.class004.android.R;
import team5.class004.android.activity.HabitDetailActivity;
import team5.class004.android.activity.JournalCreateActivity;
import team5.class004.android.databinding.FragmentHabitListBinding;
import team5.class004.android.databinding.FragmentJournalListBinding;
import team5.class004.android.databinding.ItemHabitListBinding;
import team5.class004.android.databinding.ItemJournalListBinding;
import team5.class004.android.interfaces.AppConstants;
import team5.class004.android.model.HabitItem;
import team5.class004.android.model.JournalItem;
import team5.class004.android.widget.LoadingDialog;
import team5.class004.android.widget.SmoothCheckBox;
import team5.class004.android.widget.VideoLoadingDialog;

public class JournalListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    View rootView;
    FragmentJournalListBinding fragmentBinding;
    Activity mActivity;
    MyRecyclerAdapter adapter = new MyRecyclerAdapter();
    ArrayList<JournalItem> journalItems = new ArrayList<>();
    LoadingDialog dialog;
    VideoLoadingDialog videoDialog;


    public JournalListFragment()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        dialog = new LoadingDialog(mActivity);
        videoDialog = new VideoLoadingDialog(mActivity);

        setHasOptionsMenu(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_journal_list, container, false);
        rootView = fragmentBinding.getRoot();

        fragmentBinding.swipeRefreshLayout.setOnRefreshListener(JournalListFragment.this);

        fragmentBinding.recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 4));
        fragmentBinding.recyclerView.setAdapter(adapter);

        fragmentBinding.btnCreateVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ImagePicker.create(JournalListFragment.this).multi().includeVideo(false).start();

                try {
//                    ArrayList<JournalItem> reverseJournalItems = new ArrayList<>();
//                    for(int i = journalItems.size(); i >= 0; i--) {
//                        reverseJournalItems.add(journalItems.get(i));
//                    }
                    MyAsyncTask myAsyncTask = new MyAsyncTask();
                    myAsyncTask.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
//        itemTouchHelper.attachToRecyclerView(fragmentBinding.recyclerView);
//        for(int i = 0; i < 50; i++) {
//            habitItems.add(new HabitItem());
//        }

        return rootView;
    }

    String madeVideoPath;
    public class MyAsyncTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            showVideoDialog();


            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Integer... integers)  {
            Log.e("aaa", "asd");
            ArrayList<File> image_files = new ArrayList<>();
            for(int i = journalItems.size() - 1; i >= 0; i--) {
                File file = new File(mActivity.getCacheDir(), journalItems.get(i).id + ".jpg");
                image_files.add(file);
            }
            madeVideoPath = softwareMakeVideo(image_files, Environment.getExternalStorageDirectory().toString(), String.valueOf(new Random().nextInt()));
            Snackbar.make(fragmentBinding.btnCreateVideo, "완료", Snackbar.LENGTH_SHORT).show();

            if(madeVideoPath != null)
                return 1;
            else
                return 0;
        }

        @Override
        protected void onProgressUpdate(Integer... params) {

        }

        @Override
        protected void onPostExecute(Integer result) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(madeVideoPath);
            intent.setDataAndType(uri, "video/*");
            startActivity(intent);
            dismissVideoDialog();
            super.onPostExecute(result);
        }
    }

    public String softwareMakeVideo(ArrayList<File> images, String location,
                                    String name)
    {
        File directory = new File(location);
        if(!directory.exists())
        {
            directory.mkdir();
        }
        File file = new File(directory, name + ".mp4");
        try
        {
            SequenceEncoder encoder = new SequenceEncoder(file);
            for (Iterator<File> iterator = images.iterator(); iterator
                    .hasNext();)
            {
                File image = (File) iterator.next();
                if (!image.exists() || image.length() == 0)
                {
                    continue;
                }
                Bitmap frame = BitmapFactory
                        .decodeFile(image.getAbsolutePath());
                try
                {
                    for(int i = 0; i < 24; i++)
                        encoder.encodeImage(frame);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            encoder.finish();
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            mActivity.sendBroadcast(intent);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }


    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        Log.e("asd", "2222222222");
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            List<Image> images = ImagePicker.getImages(data);
            ArrayList<File> image_files = new ArrayList<>();
            for(int i = 0; i < images.size(); i++) {
                image_files.add(new File(images.get(i).getPath()));
            }

            Log.e("---------------", images.get(0).getPath());
            softwareMakeVideo(image_files, Environment.getExternalStorageDirectory().toString(), "asdoihqw");

//            selectedImage = ImagePicker.getFirstImageOrNull(data);
//            printImages(image);
//            uploadWithTransferUtility(selectedImage.getPath());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    void refresh() {
        fragmentBinding.swipeRefreshLayout.setRefreshing(true);
        HashMap<String, String> params = new HashMap<>();
//        params.put("level", String.valueOf(boxLevel));
        GlobalApp.getInstance().restClient.api().getJournals(params).enqueue(new Callback<ArrayList<JournalItem>>()
        {
            @Override
            public void onResponse(@NonNull Call<ArrayList<JournalItem>> call, @NonNull Response<ArrayList<JournalItem>> response)
            {
                if (response.isSuccessful() && response.body() != null)
                {
                    journalItems = response.body();
                    fragmentBinding.recyclerView.getAdapter().notifyDataSetChanged();
                }
                fragmentBinding.swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<JournalItem>> call, @NonNull Throwable t)
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

    void showVideoDialog() {
        videoDialog.setCancelable(false);
        videoDialog.getWindow().setGravity(Gravity.CENTER);
        videoDialog.show();
    }

    void dismissVideoDialog() {
        videoDialog.dismiss();
    }

    public  void saveBitmapToFileCache(Bitmap bitmap, String strFilePath,
                                       String filename) {

        File file = new File(strFilePath);

        // If no folders
        if (!file.exists()) {
            file.mkdirs();
            // Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }

        File fileCacheItem = new File(strFilePath + filename);
        OutputStream out = null;

        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String saveBitmapToJpeg(Context context, Bitmap bitmap, String name){

        File storage = context.getCacheDir(); // 이 부분이 임시파일 저장 경로

        String fileName = name + ".jpg";  // 파일이름은 마음대로!

        File tempFile = new File(storage,fileName);

        try{
            tempFile.createNewFile();  // 파일을 생성해주고

            FileOutputStream out = new FileOutputStream(tempFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90 , out);  // 넘거 받은 bitmap을 jpeg(손실압축)으로 저장해줌

            out.close(); // 마무리로 닫아줍니다.

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFile.getAbsolutePath();   // 임시파일 저장경로를 리턴해주면 끝!
    }

    public Bitmap drawTextToBitmap(Context mContext, Bitmap bitmap, String mText, String content) {
        try {
            Resources resources = mContext.getResources();
            float scale = resources.getDisplayMetrics().density;
//            Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);
            android.graphics.Bitmap.Config bitmapConfig =   bitmap.getConfig();
            if(bitmapConfig == null) {
                bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
            }
            bitmap = bitmap.copy(bitmapConfig, true);

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//            paint.setColor(Color.rgb(110,110, 110));
            paint.setColor(Color.WHITE);
            paint.setTextSize((int) (36 * scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width())/6;
//            int y = (bitmap.getHeight() + bounds.height())/5;
            int y = (bitmap.getHeight() + bounds.height())/5;

            canvas.drawText(mText, x * scale, y * scale, paint);


            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//            paint.setColor(Color.rgb(110,110, 110));
            paint.setColor(Color.WHITE);
            paint.setTextSize((int) (16 * scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);
            canvas.drawText(content, x * scale, y * scale / 2, paint);

            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception

            return null;
        }

    }

    public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {

        public MyRecyclerAdapter() {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_journal_list, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            try {
//                holder.itemBinding.container.setBackgroundColor(color);

                Date date = (new SimpleDateFormat("yyyy-MM-dd").parse(journalItems.get(position).date));

                RequestOptions myOptions = new RequestOptions().error(R.mipmap.ic_launcher);
                Log.e("asd", journalItems.get(position).imageUrl);
                Glide.with(mActivity).load(journalItems.get(position).imageUrl).apply(myOptions).into(holder.itemBinding.ivImage);
                Glide.with(mActivity).asBitmap().load(journalItems.get(position).imageUrl)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                Bitmap textBitmap = drawTextToBitmap(mActivity, resource, journalItems.get(position).date, journalItems.get(position).content);

                                saveBitmapToJpeg(mActivity, textBitmap, journalItems.get(position).id);
                            }
                        });
                holder.itemBinding.tvDate.setText(new SimpleDateFormat("MM-dd").format(date));
            } catch (Exception e) {
                e.printStackTrace();
//                holder.itemBinding.container.setBackgroundColor(colors.get(position % colors.size()));
            }
        }

        @Override
        public int getItemCount() {
            return journalItems.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            public ItemJournalListBinding itemBinding;

            public ViewHolder(View itemView){
                super(itemView);

                itemBinding = DataBindingUtil.bind(itemView);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                Intent intent = new Intent(getContext(), JournalCreateActivity.class);
                intent.putExtra("journalItem", journalItems.get(position));
                startActivity(intent);
            }

            @Override
            public boolean onLongClick(View view) {
//                startActivity(new Intent(getContext(), HabitDetailActivity.class));
                return true;
            }
        }
    }

//    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//
//        @Override
//        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//            Toast.makeText(mActivity, "on Move", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//
//        @Override
//        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
////            final int position = viewHolder.getAdapterPosition();
////            showDialog();
////            HashMap<String, String> params = new HashMap<>();
////            params.put("id", journalItems.get(position).id);
////            GlobalApp.getInstance().restClient.api().deleteMyHabit(params).enqueue(new Callback<HabitItem>()
////            {
////                @Override
////                public void onResponse(@NonNull Call<HabitItem> call, @NonNull Response<HabitItem> response)
////                {
////                    if (response.isSuccessful() && response.body() != null)
////                    {
////                        journalItems.remove(position);
////                        adapter.notifyDataSetChanged();
////                    }
////                    dismisslDialog();
////                    Snackbar.make(fragmentBinding.getRoot(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
////                }
////
////                @Override
////                public void onFailure(@NonNull Call<HabitItem> call, @NonNull Throwable t)
////                {
////                    Snackbar.make(fragmentBinding.getRoot(), "오류가 발생하여 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
////                    dismisslDialog();
////                    t.printStackTrace();
////                }
////            });
//        }
//    };
}

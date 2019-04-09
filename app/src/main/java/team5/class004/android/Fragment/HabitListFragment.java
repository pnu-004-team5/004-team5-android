package team5.class004.android.fragment;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Random;

import team5.class004.android.R;
import team5.class004.android.activity.HabitDetailActivity;
import team5.class004.android.databinding.FragmentHabitListBinding;
import team5.class004.android.databinding.ItemHabitListBinding;
import team5.class004.android.model.HabitItem;

public class HabitListFragment extends Fragment {
    View rootView;
    FragmentHabitListBinding fragmentBinding;
    Activity mActivity = getActivity();
    MyRecyclerAdapter adapter = new MyRecyclerAdapter();
    ArrayList<HabitItem> habitItems = new ArrayList<>();
    ArrayList<Integer> colors = new ArrayList<>();


    public HabitListFragment()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        colors.add(Color.parseColor("#e57373"));
        colors.add(Color.parseColor("#f06292"));
        colors.add(Color.parseColor("#ba68c8"));
        colors.add(Color.parseColor("#9575cd"));
        colors.add(Color.parseColor("#4dd0e1"));
        colors.add(Color.parseColor("#81c784"));
        colors.add(Color.parseColor("#90a4ae"));
        colors.add(Color.parseColor("#ff8a65"));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_habit_list, container, false);
        fragmentBinding.setFragment(this);
        rootView = fragmentBinding.getRoot();

        fragmentBinding.recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        fragmentBinding.recyclerView.setAdapter(adapter);

        for(int i = 0; i < 50; i++) {
            habitItems.add(new HabitItem());
        }

        return rootView;
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
            Random r = new Random();
            int color = r.nextInt(colors.size());
            holder.itemBinding.container.setBackgroundColor(colors.get(color));
        }

        @Override
        public int getItemCount() {
            return habitItems.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            public ItemHabitListBinding itemBinding;

            public ViewHolder(View itemView){
                super(itemView);

                itemBinding = DataBindingUtil.bind(itemView);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
                startActivity(new Intent(getContext(), HabitDetailActivity.class));
            }

            @Override
            public boolean onLongClick(View view) {
//                startActivity(new Intent(getContext(), HabitDetailActivity.class));
                return true;
            }
        }
    }
}

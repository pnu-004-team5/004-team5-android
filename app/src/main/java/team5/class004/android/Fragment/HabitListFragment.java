package team5.class004.android.fragment;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import team5.class004.android.R;
import team5.class004.android.databinding.FragmentHabitListBinding;
import team5.class004.android.databinding.ItemHabitListBinding;
import team5.class004.android.model.HabitItem;

public class HabitListFragment extends Fragment {
    View rootView;
    FragmentHabitListBinding fragmentBinding;
    Activity mActivity = getActivity();
    MyRecyclerAdapter adapter;
    ArrayList<HabitItem> habitItems = new ArrayList<>();


    public HabitListFragment()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_habit_list, container, false);
        fragmentBinding.setFragment(this);
        rootView = fragmentBinding.getRoot();

        fragmentBinding.recyclerView.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false));
        fragmentBinding.recyclerView.setAdapter(adapter);

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

        }

        @Override
        public int getItemCount() {
            return habitItems.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public ItemHabitListBinding itemBinding;

            public ViewHolder(View itemView){
                super(itemView);

                itemBinding = DataBindingUtil.bind(itemView);
            }

            @Override
            public void onClick(View view) {
                int position = getAdapterPosition();
            }
        }
    }
}

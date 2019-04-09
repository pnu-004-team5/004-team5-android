package team5.class004.android.fragment;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import team5.class004.android.R;
import team5.class004.android.databinding.FragmentHabitListBinding;

public class HabitListFragment extends Fragment {
    View rootView;
    FragmentHabitListBinding fragmentBinding;
    Activity mActivity = getActivity();

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

        return rootView;
    }
}

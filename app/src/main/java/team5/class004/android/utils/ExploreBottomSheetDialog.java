package team5.class004.android.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import team5.class004.android.GlobalApp;
import team5.class004.android.R;
import team5.class004.android.activity.BoardDocumentCreateActivity;
import team5.class004.android.model.HabitItem;

import static org.bytedeco.opencv.global.opencv_core.finish;

public class ExploreBottomSheetDialog extends BottomSheetDialogFragment implements View.OnClickListener{

    public static ExploreBottomSheetDialog getInstance() { return new ExploreBottomSheetDialog(); }

    private LinearLayout btnEdit;
    private LinearLayout btnDelete;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.explore_bottom_sheet_dialog, container,false);
        btnEdit = (LinearLayout) view.findViewById(R.id.btn_edit);
        btnDelete = (LinearLayout) view.findViewById(R.id.btn_delete);

        btnEdit.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_edit:
                Intent intent = new Intent(getActivity(), BoardDocumentCreateActivity.class);
                intent.putExtra("boardDocumentItemId", getArguments().getString("boardDocumentItemId"));
                intent.putExtra("boardDocumentItemContent", getArguments().getString("boardDocumentItemContent"));
                startActivity(intent);
                break;
            case R.id.btn_delete:
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());
                break;
        }
        dismiss();
    }
}
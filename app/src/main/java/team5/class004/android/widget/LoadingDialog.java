package team5.class004.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import team5.class004.android.R;
import team5.class004.android.databinding.DialogLoadingBinding;

public class LoadingDialog extends Dialog {
    DialogLoadingBinding dialogBinding;

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public LoadingDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_loading, null, false);
        setContentView(dialogBinding.getRoot());

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = 0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(layoutParams);
    }
}

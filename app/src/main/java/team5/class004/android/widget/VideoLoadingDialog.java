package team5.class004.android.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import androidx.databinding.DataBindingUtil;

import team5.class004.android.R;
import team5.class004.android.databinding.DialogLoadingBinding;
import team5.class004.android.databinding.DialogVideoLoadingBinding;

public class VideoLoadingDialog extends Dialog {
    DialogVideoLoadingBinding dialogBinding;

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public VideoLoadingDialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialogBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_video_loading, null, false);
        setContentView(dialogBinding.getRoot());

        dialogBinding.tvLabel.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade));

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.dimAmount = 0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(layoutParams);
    }
}

package com.nightfarmer.lightdialog.progress;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.nightfarmer.lightdialog.R;
import com.nightfarmer.lightdialog.util.AnimateUtil;

/**
 * Created by zhangfan on 15-12-31.
 */
public class ProgressHUD {
    private Context context;
    private static final long DISMISSDELAYED = 1000;
    private SVProgressHUDMaskType mSVProgressHUDMaskType;

    boolean isShowing;

    public enum SVProgressHUDMaskType {
//        None,  // 允许遮罩下面控件点击
        Clear,     // 不允许遮罩下面控件点击
        Black,     // 不允许遮罩下面控件点击，背景黑色半透明
        Gradient,   // 不允许遮罩下面控件点击，背景渐变半透明
        ClearCancel,     // 不允许遮罩下面控件点击，点击遮罩消失
        BlackCancel,     // 不允许遮罩下面控件点击，背景黑色半透明，点击遮罩消失
        GradientCancel   // 不允许遮罩下面控件点击，背景渐变半透明，点击遮罩消失
        ;
    }

    private final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM
    );
    private ViewGroup decorView;//activity的根View
    private ViewGroup rootView;// mSharedView 的 根View
    private ProgressDefaultView mSharedView;

    private Animation outAnim;
    private Animation inAnim;
    private Animation rootOutAnim;
    private Animation rootInAnim;
    private int gravity = Gravity.CENTER;


    public ProgressHUD(Context context) {
        this.context = context;
        gravity = Gravity.CENTER;
        initViews();
        initDefaultView();
        initAnimation();
    }

    protected void initViews() {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        decorView = (ViewGroup) ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
        rootView = (ViewGroup) layoutInflater.inflate(R.layout.layout_progresshud, decorView, false);
        rootView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));
    }

    protected void initDefaultView() {
        mSharedView = new ProgressDefaultView(context);
        params.gravity = gravity;
        mSharedView.setLayoutParams(params);
    }

    protected void initAnimation() {
        if (inAnim == null)
            inAnim = getInAnimation();
        if (outAnim == null)
            outAnim = getOutAnimation();
        if (rootInAnim == null)
            rootInAnim = AnimateUtil.getRootAnimation(context, true);
        if (rootOutAnim == null)
            rootOutAnim = AnimateUtil.getRootAnimation(context, false);

    }

    /**
     * show的时候调用
     */
    private void onAttached() {
        decorView.addView(rootView);
        if (mSharedView.getParent() != null)
            ((ViewGroup) mSharedView.getParent()).removeView(mSharedView);
        rootView.addView(mSharedView);
    }

    /**
     * 添加这个View到Activity的根视图
     */
    private void svShow() {

        mHandler.removeCallbacksAndMessages(null);
        if (!isShowing()) {
            onAttached();
        }

        mSharedView.startAnimation(inAnim);
        rootView.startAnimation(rootInAnim);
        isShowing = true;
    }

    public void show() {
        setMaskType(SVProgressHUDMaskType.Black);
        mSharedView.show();
        svShow();
    }

    public void showWithMaskType(SVProgressHUDMaskType maskType) {
        //判断maskType
        setMaskType(maskType);
        mSharedView.show();
        svShow();
    }

    public void showWithStatus(String string) {
        setMaskType(SVProgressHUDMaskType.Black);
        mSharedView.showWithStatus(string);
        svShow();
    }

    public void showWithStatus(String string, SVProgressHUDMaskType maskType) {
        setMaskType(maskType);
        mSharedView.showWithStatus(string);
        svShow();
    }

    public void showInfoWithStatus(String string) {
        setMaskType(SVProgressHUDMaskType.Black);
        mSharedView.showInfoWithStatus(string);
        svShow();
        scheduleDismiss();
    }

    public void showInfoWithStatus(String string, SVProgressHUDMaskType maskType) {
        setMaskType(maskType);
        mSharedView.showInfoWithStatus(string);
        svShow();
        scheduleDismiss();
    }

    public void showSuccessWithStatus(String string) {
        setMaskType(SVProgressHUDMaskType.Black);
        mSharedView.showSuccessWithStatus(string);
        svShow();
        scheduleDismiss();
    }

    public void showSuccessWithStatus(String string, SVProgressHUDMaskType maskType) {
        setMaskType(maskType);
        mSharedView.showSuccessWithStatus(string);
        svShow();
        scheduleDismiss();
    }

    public void showErrorWithStatus(String string) {
        setMaskType(SVProgressHUDMaskType.Black);
        mSharedView.showErrorWithStatus(string);
        svShow();
        scheduleDismiss();
    }

    public void showErrorWithStatus(String string, SVProgressHUDMaskType maskType) {
        setMaskType(maskType);
        mSharedView.showErrorWithStatus(string);
        svShow();
        scheduleDismiss();
    }

    public void showWithProgress(String string, SVProgressHUDMaskType maskType) {
        setMaskType(maskType);
        mSharedView.showWithProgress(string);
        svShow();
    }

    public CircleProgressBar getProgressBar() {
        return mSharedView.getCircleProgressBar();
    }

    public void setText(String string) {
        mSharedView.setText(string);
    }

    private void setMaskType(SVProgressHUDMaskType maskType) {
        mSVProgressHUDMaskType = maskType;
        switch (mSVProgressHUDMaskType) {
//            case None:
//                configMaskType(android.R.color.transparent, false, false);
//                break;
            case Clear:
                configMaskType(android.R.color.transparent, true, false);
                break;
            case ClearCancel:
                configMaskType(android.R.color.transparent, true, true);
                break;
            case Black:
                configMaskType(R.color.bgColor_overlay, true, false);
                break;
            case BlackCancel:
                configMaskType(R.color.bgColor_overlay, true, true);
                break;
            case Gradient:
                //TODO 设置半透明渐变背景
                configMaskType(R.drawable.bg_overlay_gradient, true, false);
                break;
            case GradientCancel:
                //TODO 设置半透明渐变背景
                configMaskType(R.drawable.bg_overlay_gradient, true, true);
                break;
            default:
                break;
        }
    }

    private void configMaskType(int bg, boolean clickable, boolean cancelable) {
        rootView.setBackgroundResource(bg);
        rootView.setClickable(clickable);
        setCancelable(cancelable);
    }

    /**
     * 检测该View是不是已经添加到根视图
     *
     * @return 如果视图已经存在该View返回true
     */
    public boolean isShowing() {
        return isShowing;
    }

    public synchronized void dismiss() {
        //消失动画
        if (!isShowing) return;
        isShowing = false;
        outAnim.setAnimationListener(outAnimListener);
        mSharedView.startAnimation(outAnim);
        rootView.startAnimation(rootOutAnim);
    }

    public void dismissImmediately() {
        mSharedView.dismiss();
        rootView.removeView(mSharedView);
        decorView.removeView(rootView);
        context = null;
    }

    public Animation getInAnimation() {
        int res = AnimateUtil.getAnimationResource(this.gravity, true);
        return AnimationUtils.loadAnimation(context, res);
    }

    public Animation getOutAnimation() {
        int res = AnimateUtil.getAnimationResource(this.gravity, false);
        return AnimationUtils.loadAnimation(context, res);
    }



    private void setCancelable(boolean isCancelable) {
        View view = rootView.findViewById(R.id.sv_outmost_container);

        if (isCancelable) {
            view.setOnTouchListener(onCancelableTouchListener);
        } else {
            view.setOnTouchListener(null);
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dismiss();
        }
    };

    private void scheduleDismiss() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendEmptyMessageDelayed(0, DISMISSDELAYED);
    }

    /**
     * Called when the user touch on black overlay in order to dismiss the dialog
     */
    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dismiss();
                setCancelable(false);
            }
            return false;
        }
    };

    Animation.AnimationListener outAnimListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            dismissImmediately();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
}

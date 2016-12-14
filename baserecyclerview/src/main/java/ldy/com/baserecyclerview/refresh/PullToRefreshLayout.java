package ldy.com.baserecyclerview.refresh;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import ldy.com.baserecyclerview.R;

/**
 * @author qinbaowei
 * @createtime 2015/10/10 09:52
 * @description must be only one child view
 */
public class PullToRefreshLayout extends FrameLayout {
    private static final String TAG = "PullToRefreshLayout";
    private static final long ANIM_TIME = 250;
    private OnRefreshListener mOnRefreshListener;
    private boolean mRefreshing = false;
    private boolean canRefresh = true;

    private OnViewHeightListener mOnViewHeightListener;
    private int mHeaderHeight = 100;    //default 100px
    private int mMaxHeaderHeight = 150; //default 150px=2*mHeaderHeight

    private HeaderView mHeaderView;
    private View mChildView;
    private boolean mCanTouch = true;//可以继续处理
    private float mCurrentX;
    private float mCurrentY;
    private boolean mDealChildren = false;
    private float mTouchY;

    private int mHeaderViewResId;

    private DecelerateInterpolator mDecelerateInterpolator = new DecelerateInterpolator(10);

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mHeaderHeight = dp2Px(context, mHeaderHeight);
        mMaxHeaderHeight = dp2Px(context, mMaxHeaderHeight);
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.PullToRefreshLayout);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mHeaderViewResId = a.getResourceId(R.styleable.PullToRefreshLayout_headerView,
                R.layout.header_view);
        a.recycle();
        mHeaderView = (HeaderView) mInflater.inflate(mHeaderViewResId, this, false);
    }

    public void setCanRefresh(boolean canRefresh) {
        this.canRefresh = canRefresh;
    }

    public boolean isCanRefresh() {
        return canRefresh;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mChildView = getChildAt(0);
        addHeaderView();
    }

    private void addHeaderView() {
        if (mHeaderView == null) {
            mHeaderView = new HeaderView(getContext());
        }
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        mHeaderView.setLayoutParams(layoutParams);//先不显示，高度为0
        if (mHeaderView.getParent() != null) {
            ((ViewGroup) mHeaderView.getParent()).removeAllViews();
        }
        addView(mHeaderView, 0);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!canRefresh) {
            return super.dispatchTouchEvent(ev);
        }
        if (mRefreshing) {
            return true;//当前布局处理
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchY = ev.getY();
                mCurrentX = ev.getX();
                mCurrentY = mTouchY;
                mDealChildren = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (canRefresh) {
                    float distance = ev.getY() - mCurrentY;
                    float distanceX = ev.getX() - mCurrentX;
                    if (!canChildScrollUp()) {
                        if (distance > 0 && (Math.abs(distance)>Math.abs(distanceX))) {
                            if (distance > 0 && canRefresh) {
                                mCurrentX = ev.getX();
                                distance = mDecelerateInterpolator.getInterpolation(distance/3 / mMaxHeaderHeight) * distance/3;

                                distance = Math.min(mMaxHeaderHeight, distance);
                                distance = Math.max(0, distance);

                                mHeaderView.getLayoutParams().height = mHeaderHeight;
                                ViewCompat.setTranslationY(mHeaderView, distance - mHeaderHeight);
                                ViewCompat.setTranslationY(mChildView, distance);
                                requestLayout();
                                if (mHeaderView instanceof OnViewHeightListener) {
                                    mOnViewHeightListener = (OnViewHeightListener) mHeaderView;
                                    mOnViewHeightListener.onHeight(distance, mMaxHeaderHeight);
                                }

                                //时间内部消化了，就把子界面上的事件设置成取消
                                if (!mDealChildren) {
                                    mDealChildren = true;
                                    ev.setAction(MotionEvent.ACTION_CANCEL);
                                    int childConut = getChildCount();
                                    for (int i = 0; i < childConut; i++) {
                                        View child = getChildAt(i);
                                        child.dispatchTouchEvent(ev);
                                    }
                                }
                            }
                            return true;
                        }else if(distance > 0 && (Math.abs(distance)<Math.abs(distanceX))){
                            //防止手指离开后，ACTION_UP的测距离的时候会导致界面抖动一下
                            mCurrentY = ev.getY();
                        }else if (canRefresh && (-mHeaderView.getTranslationY() != mHeaderHeight)) {
                            //上移动，解决出现空白的情况
                            ViewCompat.setTranslationY(mHeaderView, -mHeaderHeight);
                            ViewCompat.setTranslationY(mChildView, 0);
                            requestLayout();
                            //这里不能添加true,还需要把事件给下层处理
                        }
                    } else {
                        mCurrentY = ev.getY();
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!canChildScrollUp()) {
                    mCanTouch = true;
                    float dy2 = (int) (ev.getY() - mCurrentY);
                    dy2 = (mDecelerateInterpolator.getInterpolation(dy2/3 / mMaxHeaderHeight) * dy2/3);
                    if (dy2 > 0 && canRefresh) {
                        if (dy2 >= mHeaderHeight) {
                            startRefresh(dy2 > mMaxHeaderHeight ? mMaxHeaderHeight : (int)dy2, mHeaderHeight);
                        } else if (dy2 > 0 && dy2 < mHeaderHeight) {
                            endRefresh((int)dy2);
                        }
                        reset();
                        return true;
                    }

                }

                reset();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void reset() {
        mCurrentY = 0;
        mTouchY = 0;
    }

    public static final int REFRESH = 0;

    private void startRefresh(int startY, int endY) {
        createTranslationYAnimation(REFRESH, startY, endY, new CallBack() {
            @Override
            public void onSuccess() {
                mHeaderView.begin();
                mRefreshing = true;
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            }
        });
    }

    private void endRefresh(int currentY) {
        createTranslationYAnimation(REFRESH, currentY, 0, new CallBack() {
            @Override
            public void onSuccess() {
                mRefreshing = false;
                mHeaderView.end();
                if (mHeaderView instanceof OnViewHeightListener) {
                    mOnViewHeightListener = (OnViewHeightListener) mHeaderView;
                    mOnViewHeightListener.end();
                }
            }
        });
    }

    private void createTranslationYAnimation(final int state, int startY, final int endY,
                                             final CallBack callBack) {
        ValueAnimator mValueAnimator = ValueAnimator.ofInt(startY, endY);
        mValueAnimator.setDuration(ANIM_TIME);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                if (state == REFRESH) {
//                    mHeaderView.getLayoutParams().height = value;
                    ViewCompat.setTranslationY(mHeaderView, value - mHeaderHeight);
                    ViewCompat.setTranslationY(mChildView, value);
                    if (mHeaderView instanceof OnViewHeightListener) {
                        mOnViewHeightListener = (OnViewHeightListener) mHeaderView;
                        mOnViewHeightListener.onHeight(value, mMaxHeaderHeight);
                    }
                }
                if (value == endY) {
                    if (callBack != null) {
                        callBack.onSuccess();
                    }
                }
                requestLayout();
            }
        });
        mValueAnimator.start();
    }

    /**
     * 自动刷新
     */
    public void autoRefresh() {
        if (canRefresh) {
            Log.i(TAG, "autoRefresh: mHeaderView" + mHeaderView);
            if (mHeaderView != null && mHeaderView instanceof PullToRefreshLayout.OnViewHeightListener) {
                ((OnViewHeightListener) mHeaderView).begin();//开始动画
            }
            startRefresh(0, mHeaderHeight);
        }
    }

    /**
     * 结束刷新
     */
    public void endRefresh() {
        if (mHeaderView != null && mHeaderView.getLayoutParams().height > 0 && mRefreshing) {
            endRefresh(mHeaderHeight);
        }
    }

    /**
     * 判断子控件是否还可以下拉
     *
     * @return false:已经拉到顶部
     */
    private boolean canChildScrollUp() {
        return mChildView != null && ViewCompat.canScrollVertically(mChildView, -1);
    }

    /**
     * 判断子控件是否还可以上拉
     *
     * @return false:已经到达底部
     */
    private boolean canChildScrollDown() {
        return mChildView != null && ViewCompat.canScrollVertically(mChildView, 1);
    }

    public interface OnRefreshListener {
        void onRefresh();
    }

    public interface OnViewHeightListener {
        void begin();

        /**
         * 用来做刷新动画进度监听
         *
         * @param currentHeight //滑动高度
         * @param maxHeight     //定义的最大滑动高度
         */
        void onHeight(float currentHeight, float maxHeight);

        void end();
    }

    private interface CallBack {
        void onSuccess();
    }

    private int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}

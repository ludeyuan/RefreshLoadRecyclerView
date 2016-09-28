package ldy.com.baserecyclerview.refresh;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ldy.com.baserecyclerview.R;

/**
 * Created by ludeyuan on 16/9/28.
 */

public class HeaderView extends RelativeLayout{

    private ImageView mRefreshImage;

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //添加布局
        LayoutInflater.from(getContext()).inflate(R.layout.baserecycler_header_view,this);
        mRefreshImage = (ImageView)findViewById(R.id.baserecycler_header_view_image_refresh);
    }


    public void begin(){
        mRefreshImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(){
            @Override
            public boolean onPreDraw() {
                mRefreshImage.getViewTreeObserver().removeOnPreDrawListener(this);
                AnimationDrawable ad = (AnimationDrawable) mRefreshImage.getBackground();
                ad.start();
                return true;
            }
        });
    }

    public void end(){
        mRefreshImage.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener(){
            @Override
            public boolean onPreDraw() {
                mRefreshImage.getViewTreeObserver().removeOnPreDrawListener(this);
                AnimationDrawable ad = (AnimationDrawable) mRefreshImage.getBackground();
                ad.stop();
                return true;
            }
        });
    }

}

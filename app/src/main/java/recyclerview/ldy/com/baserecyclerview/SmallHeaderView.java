package recyclerview.ldy.com.baserecyclerview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import ldy.com.baserecyclerview.refresh.BaseHeaderView;

/**
 * Created by ludeyuan on 16/9/28.
 */

public class SmallHeaderView extends BaseHeaderView{

    private ImageView mRefreshImage;

    public SmallHeaderView(Context context) {
        this(context, null);
    }

    public SmallHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmallHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //添加布局
        LayoutInflater.from(getContext()).inflate(R.layout.small_header_view,this);
        mRefreshImage = (ImageView)findViewById(R.id.small_header_view_image_refresh);
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

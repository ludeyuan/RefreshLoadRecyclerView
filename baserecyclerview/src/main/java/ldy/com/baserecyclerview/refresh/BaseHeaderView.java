package ldy.com.baserecyclerview.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by ludeyuan on 2016/12/14.
 */

public abstract class BaseHeaderView extends RelativeLayout {

    public BaseHeaderView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context,attrs,defStyleAttr);
    }

    public abstract void begin();

    public abstract void end();

}

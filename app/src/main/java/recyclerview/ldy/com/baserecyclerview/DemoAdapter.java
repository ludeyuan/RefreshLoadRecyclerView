package recyclerview.ldy.com.baserecyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ldy.com.baserecyclerview.BaseQuickAdapter;
import ldy.com.baserecyclerview.BaseViewHolder;


/**
 * Created by ludeyuan on 16/8/31.
 */
public class DemoAdapter extends BaseQuickAdapter<String> {


    public DemoAdapter(Context context, List<String> lists){
        super(R.layout.layout_adapter_item,lists);
        mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        DemoHolder demoHolder = (DemoHolder)helper;
        demoHolder.bindText(item);
    }

    @Override
    protected BaseViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        DemoHolder demoHolder = new DemoHolder(getItemView(R.layout.layout_adapter_item,parent));
        return demoHolder;
    }

    public class DemoHolder extends BaseViewHolder implements View.OnClickListener{

        private TextView mTitleText;

        public DemoHolder(View view){
            super(view);
            view.setOnClickListener(this);
            mTitleText = (TextView)view.findViewById(R.id.item_text_position);
        }

        public void bindText(String str){
            mTitleText.setText(str+"");
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(mContext,"点击了",Toast.LENGTH_SHORT).show();
        }
    }
}

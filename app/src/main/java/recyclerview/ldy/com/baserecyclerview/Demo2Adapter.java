package recyclerview.ldy.com.baserecyclerview;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ludeyuan on 16/8/31.
 */
public class Demo2Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;

    private List<String> mDataList = new ArrayList<String>();

    public Demo2Adapter(Context context) {
        this.mContext = context;
    }

    public void addRefreshData(List<String> dataList) {
        this.mDataList.addAll(0, dataList);
        notifyDataSetChanged();
    }

    public void addLoadData(List<String> dataList) {
        this.mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (0 == viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_adapter_item, parent, false);
            view.setBackgroundColor(Color.parseColor("#aabbcc"));
            return new MyViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (0 == getItemViewType(position)) {
            MyViewHolder holder1 = (MyViewHolder) holder;
            holder1.mTxt.setText(mDataList.get(position) + "");
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTxt;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTxt = (TextView) itemView.findViewById(R.id.item_text_position);
        }
    }

}

package recyclerview.ldy.com.baserecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ldy.com.baserecyclerview.BaseQuickAdapter;
import ldy.com.baserecyclerview.demo1.PullToRefreshLayout;


public class MainActivity extends AppCompatActivity implements
        BaseQuickAdapter.RequestLoadMoreListener{

    private PullToRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private DemoAdapter mAdapter;
//    private Demo2Adapter mAdapter2;

    private List<String> mLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (PullToRefreshLayout)findViewById(R.id.refreshLoadMoreLayout);
//        mSwipeRefreshLayout.init(new RefreshLoadMoreLayout.Config(this).canRefresh(false).canLoadMore(false).autoLoadMore().showLastRefreshTime(MainActivity.class, "yyyy-MM-dd").multiTask());
        mSwipeRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.endRefresh();
                    }
                },1000);
            }
        });
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<String> lists = getDatas(null);
        mLists.addAll(lists);
        mAdapter = new DemoAdapter(this,lists);
        mAdapter.openLoadAnimation();
        mAdapter.setOnLoadMoreListener(this);
        mAdapter.openLoadMore(lists.size(),true);


        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoadMoreRequested() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> lists = getDatas(mAdapter);
                mAdapter.addData(lists);
                mAdapter.notifyDataChangedAfterLoadMore(true);
            }
        },2000);
    }

    public List<String> getDatas(DemoAdapter demoAdapter){
        int firstPosition = 0;

        if(demoAdapter==null || demoAdapter.getItemCount()==0){
            firstPosition = 0;
        }else{
            firstPosition = demoAdapter.getItemCount();
        }
        firstPosition++;
        List<String> lists = new ArrayList<>();
        //在现在的基础上增加24条数据
        for(int i=0;i<24;i++){
            lists.add((i+firstPosition)+"");
        }
        return lists;
    }

}

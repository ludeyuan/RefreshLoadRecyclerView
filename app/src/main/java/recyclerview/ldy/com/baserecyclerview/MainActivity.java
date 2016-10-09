package recyclerview.ldy.com.baserecyclerview;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ldy.com.baserecyclerview.BaseQuickAdapter;
import ldy.com.baserecyclerview.recyclerview.Closeable;
import ldy.com.baserecyclerview.recyclerview.OnSwipeMenuItemClickListener;
import ldy.com.baserecyclerview.recyclerview.SwipeMenu;
import ldy.com.baserecyclerview.recyclerview.SwipeMenuCreator;
import ldy.com.baserecyclerview.recyclerview.SwipeMenuItem;
import ldy.com.baserecyclerview.recyclerview.SwipeMenuRecyclerView;
import ldy.com.baserecyclerview.refresh.PullToRefreshLayout;


public class MainActivity extends AppCompatActivity implements
        BaseQuickAdapter.RequestLoadMoreListener{

    private PullToRefreshLayout mSwipeRefreshLayout;
    private SwipeMenuRecyclerView mRecyclerView;
    private DemoAdapter mAdapter;

    private List<String> mLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (PullToRefreshLayout)findViewById(R.id.refreshLoadMoreLayout);
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
        mRecyclerView = (SwipeMenuRecyclerView)findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        // 设置菜单创建器。
        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
//         设置菜单Item点击监听。
        mRecyclerView.setSwipeMenuItemClickListener(menuItemClickListener);

        List<String> lists = getDatas(null);
        mLists.addAll(lists);
        mAdapter = new DemoAdapter(this,lists);
        mAdapter.openLoadAnimation();
        mAdapter.setOnLoadMoreListener(this);
        mAdapter.openLoadMore(lists.size(),true);
        mAdapter.setSwipeMenuCreator(swipeMenuCreator);
        mAdapter.setSwipeMenuItemClickListener(menuItemClickListener);
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

    /**
     * 菜单创建器。在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = getResources().getDimensionPixelSize(R.dimen.item_height);

            // MATCH_PARENT 自适应高度，保持和内容一样高；也可以指定菜单具体高度，也可以用WRAP_CONTENT。
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(MainActivity.this)
                        .setBackgroundColor(Color.GRAY)
//                        .setBackgroundDrawable(R.drawable.selector_red)
//                        .setImage(R.mipmap.ic_action_delete)
                        .setText("取消收藏") // 文字，还可以设置文字颜色，大小等。。
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加一个按钮到右侧侧菜单。
            }
        }
    };



    /**
     * 菜单点击监听。
     */
    private OnSwipeMenuItemClickListener menuItemClickListener = new OnSwipeMenuItemClickListener() {
        /**
         * Item的菜单被点击的时候调用。
         * @param closeable       closeable. 用来关闭菜单。
         * @param adapterPosition adapterPosition. 这个菜单所在的item在Adapter中position。
         * @param menuPosition    menuPosition. 这个菜单的position。比如你为某个Item创建了2个MenuItem，那么这个position可能是是 0、1，
         * @param direction       如果是左侧菜单，值是：SwipeMenuRecyclerView#LEFT_DIRECTION，如果是右侧菜单，值是：SwipeMenuRecyclerView#RIGHT_DIRECTION.
         */
        @Override
        public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {
            closeable.smoothCloseMenu();// 关闭被点击的菜单。

            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                Toast.makeText(MainActivity.this, "list第" + adapterPosition + "; 右侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                Toast.makeText(MainActivity.this, "list第" + adapterPosition + "; 左侧菜单第" + menuPosition, Toast.LENGTH_SHORT).show();
            }

            // TODO 如果是删除：推荐调用Adapter.notifyItemRemoved(position)，不推荐Adapter.notifyDataSetChanged();
            if (menuPosition == 0) {// 删除按钮被点击。
                mLists.remove(adapterPosition);
                mAdapter.notifyItemRemoved(adapterPosition);
            }
        }
    };

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

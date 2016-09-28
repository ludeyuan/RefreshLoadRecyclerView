# RefreshLoadRecyclerView

这里只是把两个比较好的框架合并了下
第一个（A）：https://github.com/changjiashuai/PullToRefreshLayout
第二个 (B)：https://github.com/CymChad/BaseRecyclerViewAdapterHelper

A实现了下拉刷新和上拉加载（已经满足最基本的需求）；但是突然间看到B，觉得代码的风格很好，但是B刷新布局使用的是系统
自带，无法修改；

如果只想实现基本的功能，请直接参考A，只保留上拉加载，请使用B；


#效果图
![RefreshLoadRecyclerView](screenshots/recyclerview.gif)

#介绍：
这个Demo,下拉刷新的放在了PullToRefreshLayout中，而“加载更多”的布局，放在了Adapter中；

#导入:
allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}

	dependencies {
    	        compile 'com.github.ludeyuan:RefreshLoadRecyclerView:1.0'
    	}
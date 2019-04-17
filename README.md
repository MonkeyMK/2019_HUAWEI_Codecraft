# 2019华为软件精英挑战赛，武长区第8名
2019华为软件精英挑战赛，武长区第8名，Java实现判题器，判题器运行时间5s

## 使用方式


1. config文件夹：该文件夹存放数据文件（不同的地图）。
2. src.com.huawei：该文件夹下存放所有的源码。

将你的数据放入config文件夹，运行src.com.huawei下的Main.java文件即可，注意输入命令行参数：

> config/car.txt config/road.txt config/cross.txt config/presetAnswer.txt config/answer.txt


## 其他

该程序包括了实时发车，以及实时规划路径，在每个时刻根据当前道路的情况，动态改变图的权值，然后使用dijkstra算法求单源最短路，程序总运行时间为40s左右，单独跑判题器文件运行时间为5s不到。

---
今年比赛最后两天比较懒散，导致复赛当天纯xjbs调参大法，以至于没进决赛，比较可惜，明年再战一次，

> 有兴趣的朋友可以加我qq一起交流863956237。
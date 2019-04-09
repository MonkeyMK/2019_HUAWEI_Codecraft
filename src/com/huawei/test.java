package com.huawei;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;
import java.util.Vector;
 
//class Edge implements Comparable<Edge>{
//	int to , cost;
//	Edge(int to_,int cost_){
//		to = to_;
//		cost = cost_;
//	}
//	@Override
//	public int compareTo(Edge o) {
//		// TODO Auto-generated method stub
//		return this.cost - o.cost;
//	}
//}

public class test {
	  
	static int maxn = 1000+10;
	static int dis[];
	static int path[];
	
	static ArrayList< ArrayList<Edge> > e;
	
	public static void dijkstra(int s) {
		int vis[] = new int[maxn];
		path = new int[maxn];
		dis = new int[maxn];
		for(int i = 0 ; i < maxn ; i++) dis[i] = 2147483647;
		Queue<Edge> que= new PriorityQueue<>();
		que.add(new Edge(s, 0));
		dis[s] = 0;
		path[s] = -1;
		while(que.isEmpty() == false) {
			Edge now = que.poll();
			int u = now.to;
			if(dis[u] < now.cost)continue;
			if(vis[u] == 1)continue;
			vis[u] = 1;
			for(int i = 0; i < e.get(u).size() ; i++) {
				int next = e.get(u).get(i).to;
				int cost = e.get(u).get(i).cost;
				if(vis[next] == 0 && dis[next] > dis[u] + cost) {
					dis[next] = dis[u] + cost;
					path[next] = u;
					que.add(new Edge(next,dis[next]));
				}
			}
		}
	} 
	 
	public static void main(String args[]) {
		
//		int dis = (int)Math.exp((45+0.0)/45 * 20);
//		System.out.println(dis);
//		System.out.println((10+0.0)/45 * 20);
//		int a = Integer.MAX_VALUE;
//		System.out.println(a);
//		System.out.println(a+10);
		
//		Scanner scan = new Scanner(System.in);
//		e = new ArrayList<ArrayList<Edge> >();
//		for(int i = 0 ; i < maxn ; i++) {
//			ArrayList<Edge> temp = new ArrayList<Edge>();
//			e.add(temp);
//		}
//		int n = scan.nextInt();
//		int m = scan.nextInt();
//		int s = scan.nextInt(); 
//		for(int i = 0 ; i < m ; i++) {
//			int from = scan.nextInt();
//			int to = scan.nextInt();
//			int cost = scan.nextInt();	  
//			e.get(from).add(new Edge(to,cost));
//		}
//		
//		long startTime=System.currentTimeMillis();   //��ȡ��ʼʱ��
//		
//		dijkstra(s);
//		int first = 0;
//	    for(int i=1;i<=n;i++) 
//	    {
//	        if(first > 0)
//	            System.out.print(" ");;
//	        System.out.print(dis[i]);;
//	        first = 1;
//	    }
//	    System.out.println(); 
//	    int dest = 4;
//	    while(dest!=-1) {
//	    	System.out.println(dest);
//	    	dest = path[dest];
//	    }
//	    
//	    long endTime=System.currentTimeMillis(); //��ȡ����ʱ��
//        System.out.println("��������ʱ�䣺 "+(endTime-startTime)+"ms");
	 }
}

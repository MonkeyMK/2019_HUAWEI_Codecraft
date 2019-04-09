package com.huawei;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

class Edge implements Comparable<Edge>{
	int to;
	int cost;
	Edge(int to_,int cost_){
		to = to_;
		cost = cost_;
	}
	
	@Override
	public int compareTo(Edge o) {
		// TODO Auto-generated method stub
		return this.cost - o.cost;
	}

	@Override
	public String toString() {
		return "Edge [to=" + to + ", cost=" + cost + "]";
	}
	
	
}

public class Graph {
	// 节点个数
	public int cross_num = Main.cross_dict.size();
	// cross id to array index
	public Map<Integer, Integer> id_to_index = new HashMap<>();
	// array index to cross id
	public int[] index_to_id = new int[cross_num];
	// 存放start_end的road id
	public Map<String, Integer> node_road = new HashMap<>();
	
	public boolean[] vis = new boolean[cross_num];
	public int[] dis = new int[cross_num];
	public int[] path = new int[cross_num];
	public Queue<Edge> que= new PriorityQueue<>();
	
	// 存放所有边的数组，长度等于cross的个数，每个cross最多四条边
	public List<Map<Integer, Edge>> edges = new ArrayList<>();
	
	public Graph() {
		// 1、遍历cross_dict，建立索引和id的关系
		this.create_id_and_index_relation();
		
		// 2、遍历road，建立edges，即整个图，后期只需要更改道路的cost
		this.build_graph();
		
		// 3、安排初始路径
		this.init_car_route_plan();
		
	}
	
	public void create_id_and_index_relation() {
		// 1、遍历cross_dict，建立索引和id的关系
		Iterator<Map.Entry<Integer, Cross>> iter = Main.cross_dict.entrySet().iterator();
		int i=0;
		while (iter.hasNext()) {
			Map.Entry<Integer, Cross> entry = iter.next();
			int cross_id = entry.getKey();
			index_to_id[i] = cross_id; // 通过索引找cross的id
			id_to_index.put(cross_id, i); // 通过cross的id找数组索引
			
			edges.add(new HashMap<>());
			i++;
		}
	}
	
	public void build_graph() {
		// 2、遍历road，建立edges，即整个图，后期只需要更改道路的cost
		Iterator<Map.Entry<Integer, Road>> iter1 = Main.road_dict.entrySet().iterator();
		while (iter1.hasNext()) {
			Map.Entry<Integer, Road> entry = iter1.next();
			Road road = entry.getValue();
			int start = road.road_from; // 起始点cross的id
			int start_index = this.id_to_index.get(start);
			int end = road.road_to; // 终止点cross的id
			int end_index = this.id_to_index.get(end);
			
			// a. from_to方向
			String route1 = road.road_from + "_" + road.road_to;
			this.node_road.put(route1, road.road_id);
			edges.get(start_index).put(end_index, new Edge(end_index, road.road_length));
			
			// b. to_from方向
			if(road.road_isDuplex == 1) {
				String route2 = road.road_to + "_" + road.road_from;
				this.node_road.put(route2, road.road_id);
				edges.get(end_index).put(start_index, new Edge(start_index, road.road_length));
			}
		}
	}
	
	public void cost_accumulation(List<Integer> route_plan, int start) {
		int start_index = start;
		int end_index;
		Road road;
		for(int i=0;i<route_plan.size();i++) {
			road = Main.road_dict.get(route_plan.get(i));
			if(road.road_from == start_index) {
				end_index = road.road_to;
			}else {
				end_index = road.road_from;
			}
			this.edges.get(this.id_to_index.get(start_index)).get(this.id_to_index.get(end_index)).cost += 1;
			start_index = end_index;
		}
		
	}
	
	// 3、安排初始路径
	public void init_car_route_plan() {
		// 1、预置车辆的路径进行权值累加
		for(int i=0;i<Main.preset_car_list.size();i++) {
			Car car = Main.car_dict.get(Main.preset_car_list.get(i));
			this.cost_accumulation(car.route_plan, car.car_from);
		}
		
		// 2、规划其他车的路径
		int start_index;
		int end_index;
		Iterator<Map.Entry<Integer, Car>> iter = Main.car_dict.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, Car> entry = iter.next();
			Car car = entry.getValue();
			if(!Main.preset_car_list.contains(car.car_id)) {
				start_index = this.id_to_index.get(car.car_from);
				end_index = this.id_to_index.get(car.car_to);
				this.dijkstra(start_index, end_index);
				car.route_plan = this.generate_route_plan(end_index);
				// 权值累加函数
				this.cost_accumulation(car.route_plan, car.car_from);
				
				// 随机安排发车时间
//				car.car_actual_time = (car.car_id-10000)%1500 + car.car_plan_time;
			}
		}
	}
	
	
	
	public void dijkstra(int start_index, int end_index) {
		// 1、初始化
		for(int i=0;i<cross_num;i++) {
			vis[i] = false;
			dis[i] = 1000000000;
			path[i] = -1;
		}
		
		que.add(new Edge(start_index, 0));
		dis[start_index] = 0;
		path[start_index] = -1;
		while(que.isEmpty() == false) {
			Edge now = que.poll();
			int u = now.to;
			
			if(dis[u] < now.cost)continue;
			if(vis[u] == true) {
				if(u == end_index) {
					if(que.isEmpty() == false)
						this.que.clear();
					return;
				}
				continue;
			}
			
			vis[u] = true;
			Iterator<Map.Entry<Integer, Edge>> iter = this.edges.get(u).entrySet().iterator();
			while(iter.hasNext()) {
				Map.Entry<Integer, Edge> entry = iter.next();
				Edge e = entry.getValue();
				int next = e.to;
				int cost = e.cost;
				if(vis[next] == false && dis[next] > dis[u] + cost) {
					dis[next] = dis[u] + cost;
					path[next] = u;
					que.add(new Edge(next,dis[next]));
				}
			}
		}
	}
	
	public List<Integer> generate_route_plan(int end_index) {
		List<Integer> route_plan = new ArrayList<>();
		int dest = end_index;
		while(this.path[dest]!=-1) {
			int cur_start = this.path[dest];
			String route = this.index_to_id[cur_start] + "_" + this.index_to_id[dest];
			route_plan.add(this.node_road.get(route));
			dest = path[dest];
		}
		Collections.reverse(route_plan);
		return route_plan;
	}
	
	public void real_time_update_path(Car car, int cur_cross_id, int other_cross_id, int end_id) {
		
		// 1、将回头路改为无穷大
		int cur_cross_index = this.id_to_index.get(cur_cross_id);
		int other_cross_index = this.id_to_index.get(other_cross_id);
		// 保留old值，最后需要还原（不同车的回头路不一样）
		int old_cost = -1;
		if(this.edges.get(cur_cross_index).containsKey(other_cross_index)) {
			old_cost = this.edges.get(cur_cross_index).get(other_cross_index).cost;
			this.edges.get(cur_cross_index).get(other_cross_index).cost = 1000000000;
		}
		
		
		// 2、规划新的路径
		int end_index = this.id_to_index.get(end_id);
		this.dijkstra(cur_cross_index, end_index);
		List<Integer> new_route_plan = this.generate_route_plan(end_index);
		
		// 3、更新车辆的路径route_plan
		// dir, next_road, next_road_speed等信息在schedule里更新
		if(new_route_plan.size()!=0) {
			car.route_plan = car.route_plan.subList(0, car.cur_route_plan_index+1);
			car.route_plan.addAll(new_route_plan);
		}

		
		// 4、还原回头路的无穷大
		if(this.edges.get(cur_cross_index).containsKey(other_cross_index)) {
			this.edges.get(cur_cross_index).get(other_cross_index).cost = old_cost;
		}
	}
	
	public void update_real_time_cost() {
		
		Iterator<Map.Entry<Integer, Road>> iter1 = Main.road_dict.entrySet().iterator();
		while (iter1.hasNext()) {
			Map.Entry<Integer, Road> entry = iter1.next();
			Road road = entry.getValue();
			int start = road.road_from; // 起始点cross的id
			int start_index = this.id_to_index.get(start);
			int end = road.road_to; // 终止点cross的id
			int end_index = this.id_to_index.get(end);
			
			// a. from_to方向
//			int t1 = edges.get(start_index).get(end_index).cost;
			edges.get(start_index).get(end_index).cost = this.dis_compute(road, 0);
//			int t2 = edges.get(start_index).get(end_index).cost;
//			if(t1!=t2) {
//				System.out.println(t1);
//				System.out.println(t2);
//			}
			
			// b. to_from方向
			if(road.road_isDuplex == 1) {
				edges.get(end_index).get(start_index).cost = this.dis_compute(road, 1);
			}
		}
		
	}

	@Override
	public String toString() {
		return "Graph [edges=" + edges + ", cross_num=" + cross_num + ", id_to_index=" + id_to_index + ", index_to_id="
				+ Arrays.toString(index_to_id) + ", node_road=" + node_road + ", vis=" + Arrays.toString(vis) + ", dis="
				+ Arrays.toString(dis) + ", path=" + Arrays.toString(path) + ", que=" + que + "]";
	}
	
	public int dis_compute(Road road, int dir) {
		return road.road_length + 
				(int)Math.exp((road.car_nums[dir]+0.0)/road.amount_all_position * 10) + 
				10/(road.road_channel*road.road_channel);
	}
	
	
}


















 
package com.huawei;

import java.util.List;

/** 
* @author 小炉子 863956237@qq.com: 
* @version 创建时间：2019年4月5日 上午10:02:36 
* 类说明 
*/
public class Car {
	// origin info
	public int car_id;
	public int car_from;
	public int car_to;
	public int car_max_speed;
	public int car_plan_time;
	public int is_priority;
	public int is_preset;
	
	// info need
	public int car_actual_time;
	public List<Integer> route_plan = null;
	public int cur_route_plan_index = 0;
	public int cur_speed = 0;
	public int on_road = 0;
	public int car_state = 0;
	public int dir = 0;
	public int next_road = 0;
	public int next_road_speed = 0;
	public int is_init = 0;
	
	// new
	public int cur_position = -1;
	public int cur_channel = -1;
	
	public boolean has_real_time_plan = false;
	
	
	public Car(int car_id, int car_from, int car_to, int car_max_speed, 
			int car_plan_time, int is_priority, int is_preset) {
		this.car_id = car_id;
		this.car_from = car_from;
		this.car_to = car_to;
		this.car_max_speed = car_max_speed;
		this.car_plan_time = car_plan_time;
		this.is_priority = is_priority;
		this.is_preset = is_preset;
		
		this.car_actual_time = this.car_plan_time;
		
	}
	
	public int get_real_time() {
		return this.car_actual_time;
	}
	
	public void set_route_plan(List<Integer> route_plan) {
		this.route_plan = route_plan;
	}

	@Override
	public String toString() {
		return "Car [car_id=" + car_id + ", car_from=" + car_from + ", car_to=" + car_to + ", car_max_speed="
				+ car_max_speed + ", car_plan_time=" + car_plan_time + ", is_priority=" + is_priority + ", is_preset="
				+ is_preset + ", car_actual_time=" + car_actual_time + ", route_plan=" + route_plan
				+ ", cur_route_plan_index=" + cur_route_plan_index + ", cur_speed=" + cur_speed + ", on_road=" + on_road
				+ ", car_state=" + car_state + ", dir=" + dir + ", next_road=" + next_road + ", next_road_speed="
				+ next_road_speed + ", is_init=" + is_init + ", cur_position=" + cur_position + ", cur_channel="
				+ cur_channel + "]";
	}
}
 
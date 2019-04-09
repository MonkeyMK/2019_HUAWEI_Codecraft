package com.huawei; 
/** 
* @author 小炉子 863956237@qq.com: 
* @version 创建时间：2019年4月5日 上午10:51:43 
* 类说明 
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;

public class Road {
	// origin info
	public int road_id;
	public int road_length;
	public int road_speed;
	public int road_channel;
	public int road_from;
	public int road_to;
	public int road_isDuplex;
	
	// info need
	public ArrayList<LinkedList<Car>> from_to_road_matrix = new ArrayList<>();
	public ArrayList<LinkedList<Car>> to_from_road_matrix = null;
	public ArrayList<Integer> init_list = new ArrayList<>();
	public int[] from_to_first_priority_car = {-1, -1, -1};
	public int[] to_from_first_priority_car = {-1, -1, -1};
	public boolean has_waiting_car = false;
	
	// new info, 道路拥挤情况, 0->from_to;  1->to_from;
	public int[] car_nums = {0, 0};
	public int amount_all_position;
	
	public Road(int road_id, int road_length, int road_speed, int road_channel, int road_from, int road_to,
			int road_isDuplex) {
		super();
		this.road_id = road_id;
		this.road_length = road_length;
		this.road_speed = road_speed;
		this.road_channel = road_channel;
		this.road_from = road_from;
		this.road_to = road_to;
		this.road_isDuplex = road_isDuplex;
		
		this.amount_all_position = this.road_length * this.road_channel;
		
		this.from_to_road_matrix = new ArrayList<>();
		for(int i=0;i<road_channel;i++) {
			this.from_to_road_matrix.add(new LinkedList<>());
		}
		if(road_isDuplex==1) {
			this.to_from_road_matrix = new ArrayList<>();
			for(int i=0;i<road_channel;i++) {
				this.to_from_road_matrix.add(new LinkedList<>());
			}
		}
	}
	
	
	
	@Override
	public String toString() {
		return "Road [road_id=" + road_id + ", road_length=" + road_length + ", road_speed=" + road_speed
				+ ", road_channel=" + road_channel + ", road_from=" + road_from + ", road_to=" + road_to
				+ ", road_isDuplex=" + road_isDuplex + ", from_to_road_matrix=" + from_to_road_matrix
				+ ", to_from_road_matrix=" + to_from_road_matrix + ", init_list=" + init_list
				+ ", from_to_first_priority_car=" + Arrays.toString(from_to_first_priority_car)
				+ ", to_from_first_priority_car=" + Arrays.toString(to_from_first_priority_car) + ", has_waiting_car="
				+ has_waiting_car + "]";
	}



	public int get_dir(Car cur_car,Map<Integer, Road> road_dict, Map<Integer, Cross> cross_dict) {
		int next_road_id = cur_car.next_road;
        Road next_road = road_dict.get(next_road_id);
        int cross_id;
        if(this.road_from == next_road.road_from || this.road_from == next_road.road_to)
            cross_id = this.road_from;
        else
            cross_id = this.road_to;
        Cross cross = cross_dict.get(cross_id);
		int cur_road_index = cross.roads.indexOf(this.road_id);
        int next_road_index = cross.roads.indexOf(next_road_id);
        
        if(next_road_index == (cur_road_index + 1) % 4)
            return 1;
        else if(next_road_index == (cur_road_index + 2) % 4)
            return 0;
        else
            return 2;
	}
	
	public ArrayList<LinkedList<Car>> get_road_matrix(int direction){
        if(direction == 0)
            return this.from_to_road_matrix;
        else
            return this.to_from_road_matrix;
	}
	
	public boolean run_to_road(Map<Integer, Road> road_dict, 
			Map<Integer, Cross> cross_dict, Car car, Map<String, Integer> statistics_info) {
		ArrayList<LinkedList<Car>> cur_road_matrix;
        if(car.car_from == this.road_from)
            cur_road_matrix = this.from_to_road_matrix;
        else
            cur_road_matrix = this.to_from_road_matrix;
        
        int next_road_id;
        if(car.is_init == 0) {
        	car.cur_speed = Math.min(this.road_speed, car.car_max_speed);
        	if(car.route_plan.size()>1) {
        		next_road_id = car.route_plan.get(1);
                car.next_road = next_road_id;
                car.next_road_speed = road_dict.get(next_road_id).road_speed;
                car.dir = this.get_dir(car, road_dict, cross_dict);
        	}else {
        		car.next_road = -1;
                car.next_road_speed = -1;
                car.dir = 0;
        	}
        	car.is_init = 1;
        }
        int channel;
        for(channel=0; channel<this.road_channel; channel++) {
        	if(cur_road_matrix.get(channel).size()==0) {
        		cur_road_matrix.get(channel).add(car);
                car.cur_position = car.cur_speed - 1;
                car.cur_channel = channel;
                car.on_road = 1;
                car.car_state = 2;
                statistics_info.put("running_car_num", statistics_info.get("running_car_num")+1);
                statistics_info.put("cur_time_depart_car_num", statistics_info.get("cur_time_depart_car_num")+1);
                return true;
        	}
        	Car front_car = cur_road_matrix.get(channel).get(0);
        	if(front_car.cur_position == 0) {
        		if(front_car.car_state == 1)
        			return false;
        		else {
        			if(channel == this.road_channel - 1) {
        				return false;
        			}else {
        				continue;
        			}
        		}
        	}else if(car.cur_speed > front_car.cur_position) {
        		if(front_car.car_state == 1) {
        			return false;
        		}else {
        			cur_road_matrix.get(channel).add(0, car);
                    car.cur_position = front_car.cur_position - 1;
                    car.cur_channel = channel;
                    car.on_road = 1;
                    car.car_state = 2;
                    statistics_info.put("running_car_num", statistics_info.get("running_car_num")+1);
                    statistics_info.put("cur_time_depart_car_num", statistics_info.get("cur_time_depart_car_num")+1);
                    return true;
        		}
        	}else {
        		cur_road_matrix.get(channel).add(0, car);
                car.cur_position = car.cur_speed - 1;
                car.cur_channel = channel;
                car.on_road = 1;
                car.car_state = 2;
                statistics_info.put("running_car_num", statistics_info.get("running_car_num")+1);
                statistics_info.put("cur_time_depart_car_num", statistics_info.get("cur_time_depart_car_num")+1);
                return true;
        	}
        }
        
        System.out.println("讲道理，发车是不会进入这里的，你错了！！！！！！！！！！");
        return false;
	}
	
	public void run_car_in_init_list(Map<Integer, Car> car_dict, Map<Integer, Road> road_dict, 
			Map<Integer, Cross> cross_dict, int cur_time, boolean priority, 
			Map<String, Integer> statistics_info, int direction) {
		ArrayList<Integer> init_list = this.init_list;
		ArrayList<Integer> copy_init_list = (ArrayList<Integer>) init_list.clone();
		if(init_list.size() == 0) {
			return;
		}
		int car_id;
		Car car;
		for(int i=0;i<copy_init_list.size();i++) {
			car_id = copy_init_list.get(i);
			car = car_dict.get(car_id);
			
			if(priority==true && car.is_priority==0) {
				break;
			}
			
			if(direction != -1) {
				if(direction == 0) {
					if(car.car_from != this.road_from) {
						continue;
					}
				}else {
					if(car.car_from != this.road_to) {
						continue;
					}
				}
			}
			
			if(cur_time < car.get_real_time()) {
				continue;
			}
			
			if(this.run_to_road(road_dict, cross_dict, car, statistics_info)) {
				int dir;
				if(direction==-1) {
					if(car.car_from == this.road_from)
						dir = 0;
					else
						dir = 1;
				}else {
					dir = direction;
				}
				this.car_nums[dir]++;
				init_list.remove((Integer)car_id);
			}
		}
	}
	
	public void create_sequeue(int direction) {
		ArrayList<LinkedList<Car>> cur_road_matrix;
        if (direction == 0)
            cur_road_matrix = this.from_to_road_matrix;
        else
            cur_road_matrix = this.to_from_road_matrix;
        
        ArrayList<Car> channel_first_car_list = new ArrayList<>();
        Car cur_car;
        for(int i=0; i<this.road_channel; i++) {
        	if(cur_road_matrix.get(i).size() == 0) {
        		continue;
        	}else {
        		int index = cur_road_matrix.get(i).size() - 1;
        		cur_car = cur_road_matrix.get(i).get(index);
        		// 直接做优先级车辆的判读并return
        		if(cur_car.car_state == 1 && cur_car.is_priority == 1) {
                    if(direction == 0)
                        this.from_to_first_priority_car = new int[] {cur_car.car_id, cur_car.cur_channel, cur_car.cur_position};
                    else
                        this.to_from_first_priority_car = new int[] {cur_car.car_id, cur_car.cur_channel, cur_car.cur_position};
                    return;
        		}
        		channel_first_car_list.add(cur_car);
        	}
        }
        
        if(channel_first_car_list.size() == 0) {
        	if(direction == 0)
                this.from_to_first_priority_car = new int[] {-1, -1, -1};
            else
                this.to_from_first_priority_car = new int[] {-1, -1, -1};
            return;
        }
        
        Car first_priority_car = null;
        for(int i=0; i<channel_first_car_list.size(); i++) {
        	cur_car = channel_first_car_list.get(i);
        	if(cur_car.car_state == 1) {
        		if(first_priority_car == null) {
        			first_priority_car = cur_car;
        		}else {
        			if(cur_car.cur_position > first_priority_car.cur_position) {
        				first_priority_car = cur_car;
        			}
        		}
        	}
        }
        
        if(first_priority_car == null) {
        	if(direction == 0)
                this.from_to_first_priority_car = new int[] {-1, -1, -1};
            else
                this.to_from_first_priority_car = new int[] {-1, -1, -1};
            return;
        }else {
        	cur_car = first_priority_car;
        	if(direction == 0)
                this.from_to_first_priority_car = new int[] {cur_car.car_id, cur_car.cur_channel, cur_car.cur_position};
            else
                this.to_from_first_priority_car = new int[] {cur_car.car_id, cur_car.cur_channel, cur_car.cur_position};
            return;
        }
	}
	
	public int[] get_car_from_sequeue(int dir) {
        if(dir == 0)
            return this.from_to_first_priority_car;
        else
            return this.to_from_first_priority_car;
	}
	
	public ArrayList<Integer> get_init_list() {
		return this.init_list;
	}
        
	
}
 










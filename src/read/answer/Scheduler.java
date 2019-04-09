package read.answer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/** 
* @author 小炉子 863956237@qq.com: 
* @version 创建时间：2019年4月5日 下午12:39:11 
* 类说明 
*/
public class Scheduler {
	public Map<Integer, Car> car_dict = null;  // （已完成初始化）
	public Map<Integer, Road> road_dict = null;  // （已完成初始化）
	public Map<Integer, Cross> cross_dict = null;  // （已完成初始化）
	public ArrayList<Integer> priority_car_list = null;  // （已完成初始化）
	public ArrayList<Integer> preset_car_list = null;  // （已完成初始化）
	
	public int all_car_num;  // （已完成初始化）
	public int priority_car_num;  // （已完成初始化）
	public int normal_car_num;  // （已完成初始化）
	
	// info need
	public int finished_priority_car_num = 0;  // （已完成初始化）
	public int finished_normal_car_num = 0;  // （已完成初始化）
	public int T_pri = -1;  // （已完成初始化）
	public int finished_time_normal_car = -1;  // （已完成初始化）
	public int T = -1;  // （已完成初始化）
	
	// 其他info
	public int time = 0;  // （已完成初始化）
	public Map<String, Integer> statistics_info = new HashMap<>();  // （已完成初始化）
	public int[] sorted_cross_id_list = null;  // （已完成初始化）
//	public int[] sorted_road_id_list = null;  // （已完成初始化）
	
	// new info
//	public Graph g;
	
	
	public Scheduler(Map<Integer, Car> car_dict, Map<Integer, Road> road_dict, Map<Integer, Cross> cross_dict,
			ArrayList<Integer> priority_car_list, ArrayList<Integer> preset_car_list) {
		super();
		this.car_dict = car_dict;
		this.road_dict = road_dict;
		this.cross_dict = cross_dict;
		this.priority_car_list = priority_car_list;
		this.preset_car_list = preset_car_list;
		this.preset_car_list.sort(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				Car car1 = Main.car_dict.get(o1);
				Car car2 = Main.car_dict.get(o2);
				
				if(car1.is_priority > car2.is_priority) {
					return -1;
				}else if(car1.is_priority == car2.is_priority) {
					if(car1.car_actual_time < car2.car_actual_time) {
						return -1;
					}else if(car1.car_actual_time == car2.car_actual_time) {
						if(car1.car_id < car2.car_id) {
							return -1;
						}else if(car1.car_id == car2.car_id) {
							System.out.println("不可能，你错了！！！！！！！");
						}else {
							return 1;
						}
					}else {
						return 1;
					}
				}else {
					return 1;
				}
				System.out.println("不可能，你错了！！！！！！！");
				return 0;
			}
		});
		
		this.all_car_num = this.car_dict.size();
		this.priority_car_num = this.priority_car_list.size();
		this.normal_car_num = this.all_car_num - this.priority_car_num;
		
		// 函数调用
		this.init_statics_dict();
		this.sort_cross_and_road();
		
		// new info
//		g = new Graph();
		this.arrange_cars_by_road();
		
		
	}
	
	private void arrange_cars_by_road() {
		Iterator<Map.Entry<Integer, Car>> iter = this.car_dict.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, Car> entry = iter.next();
			int car_id = entry.getKey();
			Car car = entry.getValue();
			int road_id = car.route_plan.get(0);
			Road road = this.road_dict.get(road_id);
			road.init_list.add(car_id);
		}
		
		Iterator<Map.Entry<Integer, Road>> iter1 = this.road_dict.entrySet().iterator();
		while (iter1.hasNext()) {
			Map.Entry<Integer, Road> entry = iter1.next();
			Road road = entry.getValue();
			Collections.sort(road.init_list, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					Car car1 = Main.car_dict.get(o1);
					Car car2 = Main.car_dict.get(o2);
					
					if(car1.is_priority > car2.is_priority) {
						return -1;
					}else if(car1.is_priority == car2.is_priority) {
						if(car1.car_actual_time < car2.car_actual_time) {
							return -1;
						}else if(car1.car_actual_time == car2.car_actual_time) {
							if(car1.car_id < car2.car_id) {
								return -1;
							}else if(car1.car_id == car2.car_id) {
								System.out.println("不可能，你错了！！！！！！！");
							}else {
								return 1;
							}
						}else {
							return 1;
						}
					}else {
						return 1;
					}
					System.out.println("不可能，你错了！！！！！！！");
					return 0;
				}
			});
		}
		
	}
	
	private void sort_cross_and_road() {
		ArrayList<Integer> sorted_cross_id_list = new ArrayList<Integer>(this.cross_dict.keySet());
		
		Collections.sort(sorted_cross_id_list);
		
		this.sorted_cross_id_list = new int[sorted_cross_id_list.size()];
		for(int i=0;i<sorted_cross_id_list.size();i++) {
			this.sorted_cross_id_list[i] = sorted_cross_id_list.get(i);
		}
	}
	
	private void init_statics_dict() {
		String info1 = "waiting_car_num";  // 等待态的车辆总数，用于判断路口调度是否结束 (已完成)
		String info2 = "finish_car_num";  // 当前时刻，已经到达目的地的车辆总数 (已完成)
		String info3 = "pre_finish_car_num";  // 上一时刻已经到达目的地的车辆总数，用于计算当前时刻到达目的地的车数
		String info4 = "cur_time_finish_car_num";  // 该时刻达到目的地的车数量，用于控制发车量 (已完成)
		String info5 = "running_car_num";  // 当前时刻在道路中运行的车辆总数 (已完成)
		String info6 = "cur_time_depart_car_num"; // (已完成)

        this.statistics_info.put(info1, 0);
        this.statistics_info.put(info2, 0);
        this.statistics_info.put(info3, 0);
        this.statistics_info.put(info4, 0);
        this.statistics_info.put(info5, 0);
        this.statistics_info.put(info6, 0);
	}

	@Override
	public String toString() {
		return "Scheduler [all_car_num=" + all_car_num + ", priority_car_num=" + priority_car_num + ", normal_car_num="
				+ normal_car_num + ", finished_priority_car_num=" + finished_priority_car_num
				+ ", finished_normal_car_num=" + finished_normal_car_num + ", T_pri=" + T_pri
				+ ", finished_time_normal_car=" + finished_time_normal_car + ", T=" + T + ", time=" + time
				+ ", statistics_info=" + statistics_info + ", sorted_cross_id_list=" + sorted_cross_id_list
				 + "]";
	}
	
	public void update_running_cars_cur_channel(LinkedList<Car> cur_road_channel, int cur_road_length, Road road_obj) {
		int i;
		Car cur_car;
		for(i=cur_road_channel.size()-1; i>=0; i--) {
			cur_car = cur_road_channel.get(i);
			if(i == cur_road_channel.size()-1) {
				if(cur_road_length - cur_car.cur_position - 1 < cur_car.cur_speed) {
					cur_car.car_state = 1;
                    this.statistics_info.put("waiting_car_num", this.statistics_info.get("waiting_car_num")+1);
                    road_obj.has_waiting_car = true;
                    continue;
				}else {
					cur_car.cur_position = cur_car.cur_position + cur_car.cur_speed;
                    cur_car.car_state = 2;
				}
			}else {
				Car front_car = cur_road_channel.get(i+1);
				if(front_car.cur_position - cur_car.cur_position > cur_car.cur_speed) {
					cur_car.cur_position = cur_car.cur_position + cur_car.cur_speed;
                    cur_car.car_state = 2;
				}else {
					if(front_car.car_state == 2) {
						cur_car.cur_position = front_car.cur_position - 1;
                        cur_car.car_state = 2;
					}else {
						cur_car.car_state = 1;
						this.statistics_info.put("waiting_car_num", this.statistics_info.get("waiting_car_num")+1);
                        road_obj.has_waiting_car = true;
                        continue;
					}
				}
			}
		}
	}
	
	public void drive_just_current_road() {
		//# 第一步
        //# 对各个道路进行处理（遍历顺序无要求）
        Iterator<Map.Entry<Integer, Road>> iter1 = this.road_dict.entrySet().iterator();
		while (iter1.hasNext()) {
			Map.Entry<Integer, Road> entry = iter1.next();
			Road road_obj = entry.getValue();
			// # 获取这条道路的channel数目
            int channel_num = road_obj.road_channel;
            int road_length = road_obj.road_length;
            for(int channel=0; channel<channel_num; channel++) {
            	if(road_obj.from_to_road_matrix.get(channel).size()!=0) {
            		this.update_running_cars_cur_channel(road_obj.from_to_road_matrix.get(channel), road_length, road_obj);
            	}
            	if(road_obj.road_isDuplex == 1) {
            		if(road_obj.to_from_road_matrix.get(channel).size()!=0) {
                		this.update_running_cars_cur_channel(road_obj.to_from_road_matrix.get(channel), road_length, road_obj);
                	}
            	}
            }
		}
	}
    
	public void drive_car_init_list(boolean priority) {
		Iterator<Map.Entry<Integer, Road>> iter1 = this.road_dict.entrySet().iterator();
		while (iter1.hasNext()) {
			Map.Entry<Integer, Road> entry = iter1.next();
			Road road = entry.getValue();
			road.run_car_in_init_list(this.car_dict, this.road_dict,
					this.cross_dict, this.time, priority, this.statistics_info, -1);
		}
	}
	
	public void create_car_sequeue(Road road,int direction) {
		if(road == null) {
			Iterator<Map.Entry<Integer, Road>> iter1 = this.road_dict.entrySet().iterator();
			while (iter1.hasNext()) {
				Map.Entry<Integer, Road> entry = iter1.next();
				Road cur_road = entry.getValue();
				if(cur_road.has_waiting_car) {
					cur_road.create_sequeue(0);
					if (cur_road.road_isDuplex == 1){
						cur_road.create_sequeue(1);
					}
				}
			}
		}else {
			road.create_sequeue(direction);
		}
	}
	
	public int get_direction(int cross_id, Road road) {
		if(cross_id == road.road_to) {
			return 0;
		}else {
			return 1;
		}
	}
	
	public int get_conflict_road_direction(Road conflict_road, int cross_id) {
		if(conflict_road.road_to == cross_id)
            return 0;
        else
            return 1;
	}
        
	
	public boolean conflict(Car car, Road road, Cross cross) {
		int conflict_road_id;
		Road conflict_road;
		int dir;
		int[] conflict_road_first_priority_car_list;
		int conflict_car_id;
		Car conflict_car;
		
		if(car.is_priority==1) {
			if(car.dir == 0) {
				return false;
			}else {
				if(car.dir == 1) {
					conflict_road_id = cross.roads.get((cross.roads.indexOf(road.road_id)+3)%4);
					if(conflict_road_id == -1) {
						return false;
					}
					conflict_road = this.road_dict.get(conflict_road_id);
					dir = this.get_conflict_road_direction(conflict_road, cross.cross_id);
					
					if(dir==1 && conflict_road.road_isDuplex==0) {
						conflict_road_first_priority_car_list = new int[] {-1,-1,-1};
					}else {
						conflict_road_first_priority_car_list = conflict_road.get_car_from_sequeue(dir);
					}
					
					if(conflict_road_first_priority_car_list[0] == -1) {
						return false;
					}else {
						conflict_car_id = conflict_road_first_priority_car_list[0];
		                conflict_car = this.car_dict.get(conflict_car_id);
		                if(conflict_car.is_priority==1 && conflict_car.dir == 0) {
		                	return true;
		                }else {
		                	return false;
		                }
					}
				}else {// 右转
					conflict_road_id = cross.roads.get((cross.roads.indexOf(road.road_id)+1)%4);
					if(conflict_road_id != -1) {
						conflict_road = this.road_dict.get(conflict_road_id);
						dir = this.get_conflict_road_direction(conflict_road, cross.cross_id);
						
						if(dir==1 && conflict_road.road_isDuplex == 0) {
							conflict_road_first_priority_car_list = new int[] {-1,-1,-1};
						}else {
							conflict_road_first_priority_car_list = conflict_road.get_car_from_sequeue(dir);
						}
						
						if(conflict_road_first_priority_car_list[0] != -1) {
							conflict_car_id = conflict_road_first_priority_car_list[0];
							conflict_car = this.car_dict.get(conflict_car_id);
							if(conflict_car.is_priority==1 && conflict_car.dir == 0)
								return true;
						}
					}
					
					conflict_road_id = cross.roads.get((cross.roads.indexOf(road.road_id)+2)%4);
					if(conflict_road_id == -1) {
						return false;
					}
					conflict_road = this.road_dict.get(conflict_road_id);
					dir = this.get_conflict_road_direction(conflict_road, cross.cross_id);
					
					if(dir==1 && conflict_road.road_isDuplex==0) {
						conflict_road_first_priority_car_list = new int[] {-1,-1,-1};
					}else {
						conflict_road_first_priority_car_list = conflict_road.get_car_from_sequeue(dir);
					}
					
					if(conflict_road_first_priority_car_list[0] == -1) {
						return false;
					}else {
						conflict_car_id = conflict_road_first_priority_car_list[0];
		                conflict_car = this.car_dict.get(conflict_car_id);
		                if(conflict_car.is_priority==1 && conflict_car.dir == 1) {
		                	return true;
		                }else {
		                	return false;
		                }
					}
				}
			}
		}else {
			if(car.dir == 0) {
				conflict_road_id = cross.roads.get((cross.roads.indexOf(road.road_id)+1)%4);
				if(conflict_road_id != -1) {
					conflict_road = this.road_dict.get(conflict_road_id);
					dir = this.get_conflict_road_direction(conflict_road, cross.cross_id);
					
					if(dir==1 && conflict_road.road_isDuplex == 0) {
						conflict_road_first_priority_car_list = new int[] {-1,-1,-1};
					}else {
						conflict_road_first_priority_car_list = conflict_road.get_car_from_sequeue(dir);
					}
					
					if(conflict_road_first_priority_car_list[0] != -1) {
						conflict_car_id = conflict_road_first_priority_car_list[0];
						conflict_car = this.car_dict.get(conflict_car_id);
						if(conflict_car.is_priority==1 && conflict_car.dir == 1)
							return true;
					}
				}
				
				conflict_road_id = cross.roads.get((cross.roads.indexOf(road.road_id)+3)%4);
				if(conflict_road_id == -1) {
					return false;
				}
				conflict_road = this.road_dict.get(conflict_road_id);
				dir = this.get_conflict_road_direction(conflict_road, cross.cross_id);
				
				if(dir==1 && conflict_road.road_isDuplex==0) {
					conflict_road_first_priority_car_list = new int[] {-1,-1,-1};
				}else {
					conflict_road_first_priority_car_list = conflict_road.get_car_from_sequeue(dir);
				}
				
				if(conflict_road_first_priority_car_list[0] == -1) {
					return false;
				}else {
					conflict_car_id = conflict_road_first_priority_car_list[0];
	                conflict_car = this.car_dict.get(conflict_car_id);
	                if(conflict_car.is_priority==1 && conflict_car.dir == 2) {
	                	return true;
	                }else {
	                	return false;
	                }
				}
			}else if(car.dir==1) {
				conflict_road_id = cross.roads.get((cross.roads.indexOf(road.road_id)+3)%4);
				if(conflict_road_id != -1) {
					conflict_road = this.road_dict.get(conflict_road_id);
					dir = this.get_conflict_road_direction(conflict_road, cross.cross_id);
					
					if(dir==1 && conflict_road.road_isDuplex == 0) {
						conflict_road_first_priority_car_list = new int[] {-1,-1,-1};
					}else {
						conflict_road_first_priority_car_list = conflict_road.get_car_from_sequeue(dir);
					}
					
					if(conflict_road_first_priority_car_list[0] != -1) {
						conflict_car_id = conflict_road_first_priority_car_list[0];
						conflict_car = this.car_dict.get(conflict_car_id);
						if(conflict_car.dir == 0)
							return true;
					}
				}
				
				conflict_road_id = cross.roads.get((cross.roads.indexOf(road.road_id)+2)%4);
				if(conflict_road_id == -1) {
					return false;
				}
				conflict_road = this.road_dict.get(conflict_road_id);
				dir = this.get_conflict_road_direction(conflict_road, cross.cross_id);
				
				if(dir==1 && conflict_road.road_isDuplex==0) {
					conflict_road_first_priority_car_list = new int[] {-1,-1,-1};
				}else {
					conflict_road_first_priority_car_list = conflict_road.get_car_from_sequeue(dir);
				}
				
				if(conflict_road_first_priority_car_list[0] == -1) {
					return false;
				}else {
					conflict_car_id = conflict_road_first_priority_car_list[0];
	                conflict_car = this.car_dict.get(conflict_car_id);
	                if(conflict_car.is_priority==1 && conflict_car.dir == 2) {
	                	return true;
	                }else {
	                	return false;
	                }
				}
			}else {
				conflict_road_id = cross.roads.get((cross.roads.indexOf(road.road_id)+1)%4);
				if(conflict_road_id != -1) {
					conflict_road = this.road_dict.get(conflict_road_id);
					dir = this.get_conflict_road_direction(conflict_road, cross.cross_id);
					
					if(dir==1 && conflict_road.road_isDuplex == 0) {
						conflict_road_first_priority_car_list = new int[] {-1,-1,-1};
					}else {
						conflict_road_first_priority_car_list = conflict_road.get_car_from_sequeue(dir);
					}
					
					if(conflict_road_first_priority_car_list[0] != -1) {
						conflict_car_id = conflict_road_first_priority_car_list[0];
						conflict_car = this.car_dict.get(conflict_car_id);
						if(conflict_car.dir == 0)
							return true;
					}
				}
				
				conflict_road_id = cross.roads.get((cross.roads.indexOf(road.road_id)+2)%4);
				if(conflict_road_id == -1) {
					return false;
				}
				conflict_road = this.road_dict.get(conflict_road_id);
				dir = this.get_conflict_road_direction(conflict_road, cross.cross_id);
				
				if(dir==1 && conflict_road.road_isDuplex==0) {
					conflict_road_first_priority_car_list = new int[] {-1,-1,-1};
				}else {
					conflict_road_first_priority_car_list = conflict_road.get_car_from_sequeue(dir);
				}
				
				if(conflict_road_first_priority_car_list[0] == -1) {
					return false;
				}else {
					conflict_car_id = conflict_road_first_priority_car_list[0];
	                conflict_car = this.car_dict.get(conflict_car_id);
	                if(conflict_car.dir == 1) {
	                	return true;
	                }else {
	                	return false;
	                }
				}
			}
		}
	}
	
	public ArrayList<LinkedList<Car>> get_other_road_matrix(Road cur_road, Road next_road){
		int cross_id;
		
        if(cur_road.road_from == next_road.road_from || cur_road.road_from == next_road.road_to)
            cross_id = cur_road.road_from;
        else
            cross_id = cur_road.road_to;
        
        if(next_road.road_from == cross_id)
            return next_road.from_to_road_matrix;
        else
            return next_road.to_from_road_matrix;
	}
	
	public int get_dir(Car cur_car) {
		int cur_road_id = cur_car.route_plan.get(cur_car.cur_route_plan_index);
        int next_road_id = cur_car.next_road;
        int cross_id;
        
        Road cur_road = this.road_dict.get(cur_road_id);
        Road next_road = this.road_dict.get(next_road_id);
        if(cur_road.road_from == next_road.road_from || cur_road.road_from == next_road.road_to)
            cross_id = cur_road.road_from;
        else
            cross_id = cur_road.road_to;
        Cross cross = this.cross_dict.get(cross_id);
        int cur_road_index = cross.roads.indexOf(cur_road_id);
        int next_road_index = cross.roads.indexOf(next_road_id);
        
        if(next_road_index == (cur_road_index + 1) % 4)
            return 1;
        else if(next_road_index == (cur_road_index + 2) % 4)
            return 0;
        else
            return 2;
	}
	
	public int get_next_road_dir(Road cur_road, Road next_road) {
        if(next_road.road_from == cur_road.road_from || next_road.road_from == cur_road.road_to)
        	return 0;
        else
            return 1;
	}
        
	
	public boolean move_to_next_road(Car cur_car, Road cur_road, int dir, int channel, int position) {
		ArrayList<LinkedList<Car>> cur_road_matrix;
		int next_road_id;
		Road next_road;
		ArrayList<LinkedList<Car>> next_road_matrix;
		int next_road_actual_speed;
		
		cur_road_matrix = cur_road.get_road_matrix(dir);
		if(cur_car.dir == 0 && cur_car.cur_route_plan_index == cur_car.route_plan.size()-1) {
			cur_road_matrix.get(channel).pollLast();
			cur_car.car_state = 3;
			this.statistics_info.put("finish_car_num", this.statistics_info.get("finish_car_num")+1);
			this.statistics_info.put("cur_time_finish_car_num", this.statistics_info.get("cur_time_finish_car_num")+1);
			this.statistics_info.put("waiting_car_num", this.statistics_info.get("waiting_car_num")-1);
			this.statistics_info.put("running_car_num", this.statistics_info.get("running_car_num")-1);
			
			cur_road.car_nums[dir]--;
			
			if(cur_car.is_priority == 1) {
				this.finished_priority_car_num += 1;
			}else {
				this.finished_normal_car_num += 1;
			}
			return true;
		}
		
		next_road_id = cur_car.next_road;
        next_road = this.road_dict.get(next_road_id);
        next_road_matrix = this.get_other_road_matrix(cur_road, next_road);
        next_road_actual_speed = Math.min(next_road.road_speed, cur_car.car_max_speed);
        
        if(next_road_actual_speed - (cur_road.road_length - cur_car.cur_position - 1) <= 0) {
            cur_car.cur_position = cur_road.road_length - 1;
            cur_car.car_state = 2;
            this.statistics_info.put("waiting_car_num", this.statistics_info.get("waiting_car_num")-1);
            return true;
        }else {
        	int next_channel_num = next_road.road_channel;
        	for(int next_road_channel=0;next_road_channel<next_channel_num;next_road_channel++) {
        		if(next_road_matrix.get(next_road_channel).size()==0) {
        			cur_car = cur_road_matrix.get(channel).pollLast();
                    next_road_matrix.get(next_road_channel).add(0, cur_car);
                    cur_car.cur_position = next_road_actual_speed - cur_road.road_length + cur_car.cur_position;
                    cur_car.cur_channel = next_road_channel;
                    cur_car.car_state = 2;
                    this.statistics_info.put("waiting_car_num", this.statistics_info.get("waiting_car_num")-1);
                    cur_car.cur_speed = next_road_actual_speed;
                    cur_car.cur_route_plan_index += 1;
                    if((((cur_car.cur_route_plan_index) == (cur_car.route_plan.size() - 1)))) {
                    	cur_car.dir = 0;
                        cur_car.next_road = -1;
                        cur_car.next_road_speed = -1;
                    }else {
                    	cur_car.next_road = cur_car.route_plan.get(cur_car.cur_route_plan_index + 1);
                        cur_car.next_road_speed = this.road_dict.get(cur_car.next_road).road_speed;
                        cur_car.dir = this.get_dir(cur_car);
                    }
                    
                    cur_road.car_nums[dir]--;
                    int next_road_dir = this.get_next_road_dir(cur_road, next_road);
                    next_road.car_nums[next_road_dir]++;
                    
                    cur_car.has_real_time_plan = false;
                    return true;
        		}else {
        			Car front_car = next_road_matrix.get(next_road_channel).get(0);
        			if(front_car.cur_position == 0 && front_car.car_state == 2) {
        				if(next_road_channel == next_channel_num - 1) {
        					cur_car.cur_position = cur_road.road_length - 1;
                            cur_car.car_state = 2;
                            this.statistics_info.put("waiting_car_num", this.statistics_info.get("waiting_car_num")-1);
                            return true;
        				}else {
        					continue;
        				}
        			}else if(front_car.cur_position == 0 && front_car.car_state == 1) {
        				return false;
        			}else if(next_road_actual_speed - (cur_road.road_length - cur_car.cur_position - 1) <= front_car.cur_position) {
        				cur_car = cur_road_matrix.get(channel).pollLast();
                        next_road_matrix.get(next_road_channel).add(0, cur_car);
                        cur_car.cur_position = next_road_actual_speed - cur_road.road_length + cur_car.cur_position;
                        cur_car.cur_channel = next_road_channel;
                        cur_car.car_state = 2;
                        this.statistics_info.put("waiting_car_num", this.statistics_info.get("waiting_car_num")-1);
                        cur_car.cur_speed = next_road_actual_speed;
                        cur_car.cur_route_plan_index += 1;
                        if((((cur_car.cur_route_plan_index) == (cur_car.route_plan.size() - 1)))) {
                        	cur_car.dir = 0;
                            cur_car.next_road = -1;
                            cur_car.next_road_speed = -1;
                        }else {
                        	cur_car.next_road = cur_car.route_plan.get(cur_car.cur_route_plan_index + 1);
                            cur_car.next_road_speed = this.road_dict.get(cur_car.next_road).road_speed;
                            cur_car.dir = this.get_dir(cur_car);
                        }
                        
                        cur_road.car_nums[dir]--;
                        int next_road_dir = this.get_next_road_dir(cur_road, next_road);
                        next_road.car_nums[next_road_dir]++;
                        
                        cur_car.has_real_time_plan = false;
                        
                        return true;
        			}else if(front_car.car_state == 2) {
        				cur_car = cur_road_matrix.get(channel).pollLast();
                        next_road_matrix.get(next_road_channel).add(0, cur_car);
                        cur_car.cur_position = front_car.cur_position - 1;
                        cur_car.cur_channel = next_road_channel;
                        cur_car.car_state = 2;
                        this.statistics_info.put("waiting_car_num", this.statistics_info.get("waiting_car_num")-1);
                        cur_car.cur_speed = next_road_actual_speed;
                        cur_car.cur_route_plan_index += 1;
                        if((((cur_car.cur_route_plan_index) == (cur_car.route_plan.size() - 1)))) {
                        	cur_car.dir = 0;
                            cur_car.next_road = -1;
                            cur_car.next_road_speed = -1;
                        }else {
                        	cur_car.next_road = cur_car.route_plan.get(cur_car.cur_route_plan_index + 1);
                            cur_car.next_road_speed = this.road_dict.get(cur_car.next_road).road_speed;
                            cur_car.dir = this.get_dir(cur_car);
                        }
                        
                        cur_road.car_nums[dir]--;
                        int next_road_dir = this.get_next_road_dir(cur_road, next_road);
                        next_road.car_nums[next_road_dir]++;
                        
                        cur_car.has_real_time_plan = false;
                        
                        return true;
        			}else {
        				return false;
        			}
        		}
        	}
        }
        System.out.println("不可能进入这里！！！！！！！！！！！！！");
        return false;
	}
	
	public void update_cur_channel(ArrayList<LinkedList<Car>> cur_road_matrix,
			int cur_channel, int cur_road_length) {
		LinkedList<Car> cur_channel_matrix = cur_road_matrix.get(cur_channel);
		
		if(cur_channel_matrix.size() == 0) {
			return;
		}
		
		Car cur_car;
		
		for(int i=cur_channel_matrix.size()-1; i>=0; i--) {
			cur_car = cur_channel_matrix.get(i);
			if(cur_car.car_state == 2) {
				continue;
			}else {
				if(i == cur_channel_matrix.size()-1) {
					if(cur_road_length - cur_car.cur_position - 1 < cur_car.cur_speed) {
						cur_car.car_state = 1;
                        return;
					}else {
						cur_car.cur_position = cur_car.cur_position + cur_car.cur_speed;
                        cur_car.car_state = 2;
                        this.statistics_info.put("waiting_car_num", this.statistics_info.get("waiting_car_num")-1);
                        continue;
					}
				}else {
					Car front_car = cur_channel_matrix.get(i+1);
					if(front_car.cur_position - cur_car.cur_position > cur_car.cur_speed) {
						cur_car.cur_position = cur_car.cur_position + cur_car.cur_speed;
                        cur_car.car_state = 2;
                        this.statistics_info.put("waiting_car_num", this.statistics_info.get("waiting_car_num")-1);
                        continue;
					}else {
						if(front_car.car_state == 2) {
							cur_car.cur_position = front_car.cur_position - 1;
                            cur_car.car_state = 2;
                            this.statistics_info.put("waiting_car_num", this.statistics_info.get("waiting_car_num")-1);
                            continue;
						}else {
							return;
						}
					}
				}
			}
		}
	}
	
	public boolean drive_car_in_wait_state() {
		while(this.statistics_info.get("waiting_car_num")!=0) {
			int deak_lock = 0;
			int pre_waiting_car_num = this.statistics_info.get("waiting_car_num");
			Cross cross;
			Road road;
			Car car;
			int dir;
			int[] first_priority_car_list;
			int channel;
			int position;
			
			for(int cross_id: this.sorted_cross_id_list) {
				cross = this.cross_dict.get(cross_id);
				for(int road_id: cross.sorted_roads) {
					if(road_id==-1) {
						continue;
					}
					road = this.road_dict.get(road_id);
					dir = this.get_direction(cross_id, road);
					
					if(dir==1 && road.road_isDuplex==0) {
						continue;
					}
					
					first_priority_car_list = road.get_car_from_sequeue(dir);
					label :{
						
					while(first_priority_car_list[0] != -1) {
						car = this.car_dict.get(first_priority_car_list[0]);
						channel = first_priority_car_list[1];
						position = first_priority_car_list[2];
						
						if(this.conflict(car, road, cross)){
							break label;
						}
						
						if(this.move_to_next_road(car, road, dir, channel, position)) {
							this.update_cur_channel(road.get_road_matrix(dir), channel, road.road_length);
							this.create_car_sequeue(road, dir);
							road.run_car_in_init_list(this.car_dict, this.road_dict,
									this.cross_dict, this.time, true, this.statistics_info, dir);
//							this.drive_car_init_list(true);
						}else {
							break label;
						}
						first_priority_car_list = road.get_car_from_sequeue(dir);
//						if(first_priority_car_list[0]!=-1)
//							update_route_plan_of_cur_car(this.car_dict.get(first_priority_car_list[0]), road);
					}
					road.has_waiting_car = false;
					}
				}
			}
			
			if(pre_waiting_car_num == this.statistics_info.get("waiting_car_num")) {
				deak_lock = 1;
			}
			
			if(deak_lock == 1) {
				return false;
			}
			
			// real time route plan
//			this.update_route_plan_of_first_priority_car();
		}
		return true;
	}
	
	public boolean is_finish() {
		return this.statistics_info.get("finish_car_num") == this.all_car_num;
	}
	
	public void update_route_plan_of_first_priority_car() {
		// 1、更新图的权重
//		g.update_real_time_cost();
		
		// 2、对第一优先级的车进行重新规划，规划完之后要更新信息：dir, next_road, next_road_speed等信息
		Iterator<Map.Entry<Integer, Road>> iter1 = Main.road_dict.entrySet().iterator();
		while (iter1.hasNext()) {
			Map.Entry<Integer, Road> entry = iter1.next();
			Road road = entry.getValue();
			int[] first_priority_car;
			for(int dir=0;dir<2;dir++) {
				
				first_priority_car = road.get_car_from_sequeue(dir);
				
				if(first_priority_car[0]!=-1) {
					Car car = this.car_dict.get(first_priority_car[0]);
					
					if(Main.preset_car_list.contains(car.car_id))
						continue;
					
					if(car.cur_route_plan_index == car.route_plan.size()-1)
						continue;
					
					if(car.has_real_time_plan != false) {
						continue;
					}
					car.has_real_time_plan = true;
					
					Road next_road = this.road_dict.get(car.route_plan.get(car.cur_route_plan_index+1));
					int cur_cross_id;
					int other_cross_id;
					
			        if(road.road_from == next_road.road_from || road.road_from == next_road.road_to) {
			        	cur_cross_id = road.road_from;
			        	other_cross_id = road.road_to;
			        }else {
			        	cur_cross_id = road.road_to;
			        	other_cross_id = road.road_from;
			        }
			        
//			        int car_num_condition = 20;
//			        if(cur_cross_id == next_road.road_from) {
//			        	if(next_road.car_nums[0] < car_num_condition)
//			        		continue;
//			        }else {
//			        	if(next_road.car_nums[1] < car_num_condition)
//			        		continue;
//			        }
			        
			        
			        // 3、路径的实时规划
//			        System.out.println(car.route_plan);
//					g.real_time_update_path(car, cur_cross_id, other_cross_id, car.car_to);
//					System.out.println(car.route_plan);
					
					// 4、更新dir, next_road, next_road_speed
					car.next_road = car.route_plan.get(car.cur_route_plan_index+1);
					car.next_road_speed = this.road_dict.get(car.next_road).road_speed;
					car.dir = this.get_dir(car);
				}
			}
		}
	}
	
	public void update_route_plan_of_cur_car(Car car, Road road) {
		// 1、更新图的权重
//		g.update_real_time_cost();
		
		// 2、车进行重新规划，规划完之后要更新信息：dir, next_road, next_road_speed等信息
		if(car.cur_route_plan_index == car.route_plan.size()-1)
			return;
			
		if(car.has_real_time_plan != false) {
			return;
		}
		car.has_real_time_plan = true;
		
		if(Main.preset_car_list.contains(car.car_id))
			return;
		
		Road next_road = this.road_dict.get(car.route_plan.get(car.cur_route_plan_index+1));
		int cur_cross_id;
		int other_cross_id;
			
        if(road.road_from == next_road.road_from || road.road_from == next_road.road_to) {
        	cur_cross_id = road.road_from;
        	other_cross_id = road.road_to;
        }else {
        	cur_cross_id = road.road_to;
        	other_cross_id = road.road_from;
        }
	        
//        int car_num_condition = 20;
//        if(cur_cross_id == next_road.road_from) {
//        	if(next_road.car_nums[0] < car_num_condition)
//        		return;
//        }else {
//        	if(next_road.car_nums[1] < car_num_condition)
//        		return;
//        }
	        
	        
        // 3、路径的实时规划
//		g.real_time_update_path(car, cur_cross_id, other_cross_id, car.car_to);
			
		// 4、更新dir, next_road, next_road_speed
		car.next_road = car.route_plan.get(car.cur_route_plan_index+1);
		car.next_road_speed = this.road_dict.get(car.next_road).road_speed;
		car.dir = this.get_dir(car);
	}
	
	public int computer_score() {
		// 1、车速
		int max_speed_all_car = -1;
		int max_speed_priority_car = -1;
		int min_speed_all_car = 1000;
		int min_speed_priority_car = 1000;
		
		// 2、出发时间
		int latest_time_all_car = -1;
		int latest_time_priority_car = -1;
		int earlist_time_all_car = 1000;
		int earlist_time_priority_car = 1000;
		
		// 3、分布
		List<Integer> list_from_dis_all_car = new ArrayList<>();
		List<Integer> list_from_dis_priority_car = new ArrayList<>();
		List<Integer> list_to_dis_all_car = new ArrayList<>();
		List<Integer> list_to_dis_priority_car = new ArrayList<>();
		
		Iterator<Map.Entry<Integer, Car>> iter = this.car_dict.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, Car> entry = iter.next();
			Car car = entry.getValue();
			
			// 1、所有车最大速度
			if(car.car_max_speed > max_speed_all_car)
				max_speed_all_car = car.car_max_speed;
			// 2、所有车最小速度
			if(car.car_max_speed < min_speed_all_car)
				min_speed_all_car = car.car_max_speed;
			// 3、所有车最晚出发时间
			if(car.car_plan_time > latest_time_all_car)
				latest_time_all_car = car.car_plan_time;
			// 4、所有车最早出发时间
			if(car.car_plan_time < earlist_time_all_car)
				earlist_time_all_car = car.car_plan_time;
			// 5、所有车出发地分布
			if(!list_from_dis_all_car.contains(car.car_from)) {
				list_from_dis_all_car.add(car.car_from);
			}
			// 6、所有车目的地分布
			if(!list_to_dis_all_car.contains(car.car_from)) {
				list_to_dis_all_car.add(car.car_from);
			}
			
			if(car.is_priority == 1) {
				if(car.car_max_speed > max_speed_priority_car)
					max_speed_priority_car = car.car_max_speed;
				if(car.car_max_speed < min_speed_priority_car)
					min_speed_priority_car = car.car_max_speed;
				if(car.car_plan_time > latest_time_priority_car)
					latest_time_priority_car = car.car_plan_time;
				if(car.car_plan_time < earlist_time_priority_car)
					earlist_time_priority_car = car.car_plan_time;
				if(!list_from_dis_priority_car.contains(car.car_from)) {
					list_from_dis_priority_car.add(car.car_from);
				}
				if(!list_to_dis_priority_car.contains(car.car_from)) {
					list_to_dis_priority_car.add(car.car_from);
				}
			}
		} // end while
		this.T_pri -= earlist_time_priority_car;
		
		double a1 = (this.all_car_num + 0.0) / this.priority_car_num * 0.05;
		double a2 = ((max_speed_all_car + 0.0)/min_speed_all_car) / ((max_speed_priority_car+0.0)/min_speed_priority_car) * 0.2375;
		double a3 = ((latest_time_all_car + 0.0)/earlist_time_all_car) / ((latest_time_priority_car+0.0)/earlist_time_priority_car) * 0.2375;
		double a4 = (list_from_dis_all_car.size()+0.0)/list_from_dis_priority_car.size() * 0.2375;
		double a5 = (list_to_dis_all_car.size()+0.0)/list_to_dis_priority_car.size() * 0.2375;
		double a = a1 + a2 + a3 + a4 + a5;
		int final_score = (int)(a*this.T_pri + this.T);
		return final_score;
	}
	
	public void schedule() {
		while(true) {
			this.time++;
			// ########################## 统计信息 ############################
			this.statistics_info.put("cur_time_finish_car_num", 0);
			this.statistics_info.put("cur_time_depart_car_num", 0);
			// ###############################################################
			
			this.drive_just_current_road();
			this.drive_car_init_list(true);
			this.create_car_sequeue(null, -1); // 传入null表示对所有车道都更新
			
			// real time route plan
//			this.update_route_plan_of_first_priority_car();
			
			if(!this.drive_car_in_wait_state()) {
				System.out.println("发生了死锁！！！！！！！！！！！！！！！！！！！！！");
				System.out.println("死锁的车辆如下：");
				
				Iterator<Map.Entry<Integer, Car>> iter = this.car_dict.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<Integer, Car> entry = iter.next();
					Car car = entry.getValue();
					if(car.car_state==1) {
						System.out.println(car);
					}
				}
				return;
			}
			
			this.drive_car_init_list(false);
			
            if(this.T_pri == -1 && this.finished_priority_car_num == this.priority_car_num)
            	this.T_pri = this.time;
            if(this.finished_time_normal_car == -1 && this.finished_normal_car_num == this.normal_car_num)
            	this.finished_time_normal_car = this.time;
			
            // ----------------------------------- 打印信息 --------------------------------------------
            System.out.println("----------------------------------------------------------------------------------");
            System.out.println("当前系统调度时间为                     ：" + this.time);
            System.out.println("当前时刻已经完成的车数量为       ：" + this.statistics_info.get("finish_car_num"));
            System.out.println("该时间片内完成的车辆数目为       ：" + this.statistics_info.get("cur_time_finish_car_num"));
            System.out.println("当前时间片系统中运行的车辆数目：" + this.statistics_info.get("running_car_num"));
            System.out.println("当前时刻的发车数量为                 ：" + this.statistics_info.get("cur_time_depart_car_num"));
            // ----------------------------------------------------------------------------------------
            
            if(this.is_finish()){
            	this.T = this.time;
                break;
            }
		}
		
		System.out.println("----------------------------------------------------------------------------------");
		System.out.println("系统调度完成！！！！");
		System.out.println("根据公式，整个系统得分为 ：" + this.computer_score());
		System.out.println("优先级车辆的完成时间为    ：" + this.T_pri);
		System.out.println("整个系统的完成时间为        ：" + this.T);
		
	}
		
	
	
	
	
	
	
	
}
 
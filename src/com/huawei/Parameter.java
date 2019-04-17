package com.huawei; 
/** 
* @author 小炉子 863956237@qq.com: 
* @version 创建时间：2019年4月12日 下午3:11:28 
* 类说明 
*/
public class Parameter {
	public static int p_N; // 发车量
	public static int p_clear_num; // 清空的时间片
	public static double p_min_degree_of_crowding; // 小于该值，不加入拥挤度
	
	// 实时发车
	public static int p_base;
	public static double p_k;
	public static int p_NA;
	public static int p_NMAX;
	public static double p_car_num_condition;
	public static double p_max_crowd;
	
	public static void set_parameter() {
		if(Main.car_dict.get(25826).car_from == 690) {
			// 地图一的参数
			Parameter.p_min_degree_of_crowding = 0.3; // 小于该值，该道路实施规划的权重不加拥挤指标（即直接length和channel）
			
			Parameter.p_base = 79; // 基础发车量，小于最大发车量NMAX
			Parameter.p_k = 0.3; // 调节系数（不知道咋调，随机）
			Parameter.p_NA = 6900; // 最大道路容纳量
			Parameter.p_NMAX = 86; // 最大发车量，前期系统车少时，基本都是这个数量
			
			Parameter.p_car_num_condition = 0.1; // 实时规划的时候，如果他下一条道路拥挤程度小于该值，不实时规划
			Parameter.p_max_crowd = 1.0; // 实时规划时，某条道路的拥挤度大于该值，权重变成100w（相当于删除）
			
		}else {
			// 地图二的参数
			Parameter.p_min_degree_of_crowding = 0.3;
			Parameter.p_base = 40;
			Parameter.p_k = 0.28;
			Parameter.p_NA = 5300;
			Parameter.p_NMAX = 70;
			Parameter.p_car_num_condition = 0.1;
			Parameter.p_max_crowd = 1.0;
		}
	}
}
 
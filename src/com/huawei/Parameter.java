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
	
	public static void set_parameter() {
		if(Main.car_dict.containsKey(37819)) {
			// 地图一的参数
			Parameter.p_min_degree_of_crowding = 0.3;
			Parameter.p_base = 42;
			Parameter.p_k = 0.3;
			Parameter.p_NA = 5500;
			Parameter.p_NMAX = 45;
			
		}else {
			// 地图二的参数
			Parameter.p_min_degree_of_crowding = 0.3;
			Parameter.p_base = 42;
			Parameter.p_k = 0.3;
			Parameter.p_NA = 5500;
			Parameter.p_NMAX = 48;
		}
	}
}
 
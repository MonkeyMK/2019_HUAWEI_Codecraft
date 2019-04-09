package read.answer;

import java.util.ArrayList;
import java.util.Arrays;

/** 
* @author 小炉子 863956237@qq.com: 
* @version 创建时间：2019年4月5日 上午10:41:41 
* 类说明 
*/
public class Cross {
	public int cross_id;
	public ArrayList<Integer> roads = new ArrayList<>();
	public int[] sorted_roads = null;
	
	public Cross(int cross_id, int[] roads) {
		this.cross_id = cross_id;
		for(int i=0;i<roads.length;i++) {
			this.roads.add(roads[i]);
		}
		this.sorted_roads = roads;
		Arrays.sort(this.sorted_roads);
	}

	@Override
	public String toString() {
		return "Cross [cross_id=" + cross_id + ", roads=" + roads + ", sorted_roads=" + Arrays.toString(sorted_roads)
				+ "]";
	}


	
}
 
package com.huawei;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Main {
	public static Map<Integer, Car> car_dict = new HashMap<>();
	public static Map<Integer, Road> road_dict = new HashMap<>();
	public static Map<Integer, Cross> cross_dict = new HashMap<>();
	public static ArrayList<Integer> priority_car_list = new ArrayList<>();
	public static ArrayList<Integer> preset_car_list = new ArrayList<>();
	
	// 1、建立car_dict； 首先建立Car对象，然后put进car_dict
	public static void create_car_dict(String carPath) throws NumberFormatException, IOException {
		FileReader carFile = new FileReader(carPath);
		String carLine;
		BufferedReader carbuf = new BufferedReader(carFile);
        while((carLine = carbuf.readLine())!=null) {
        	if(carLine.contains("#"))
        		continue;
        	else {
        		String newCarLine [] = new String[7];
				int intCarLine [] = new int[7];
				carLine = carLine.replace("(","");
				carLine = carLine.replace(")","");
				newCarLine = carLine.split(",");
				for(int i=0;i<newCarLine.length;i++) {
					newCarLine[i] = newCarLine[i].replaceAll(" ","");
					intCarLine [i] = Integer.parseInt(newCarLine[i]);
				}
				// intCarLine: 0-id，5-priority，6-preset
				Car car = new Car(intCarLine[0],intCarLine[1],intCarLine[2],
						intCarLine[3],intCarLine[4],intCarLine[5],intCarLine[6]);
				Main.car_dict.put(intCarLine[0], car);
				// 优先级车
				if(intCarLine[5] == 1) {
					Main.priority_car_list.add(intCarLine[0]);
				}
				// 预置车辆
				if(intCarLine[6] == 1) {
					Main.preset_car_list.add(intCarLine[0]);
				}
//				System.out.println(Main.priority_car_list);
        	}
        }
	}
	
	public static void create_road_dict(String roadPath) throws NumberFormatException, IOException {
		FileReader roadFile = new FileReader(roadPath);
		String roadLine;
		BufferedReader roadbuf = new BufferedReader(roadFile);
        while((roadLine = roadbuf.readLine())!=null) {
        	if(roadLine.contains("#"))
        		continue;
        	else {
        		String newRoadLine [] = new String[7];
				int intRoadLine [] = new int[7];
				roadLine = roadLine.replace("(","");
				roadLine = roadLine.replace(")","");
				newRoadLine = roadLine.split(",");
				for(int i=0;i<newRoadLine.length;i++) {
					newRoadLine[i] = newRoadLine[i].replaceAll(" ","");
					intRoadLine [i] = Integer.parseInt(newRoadLine[i]);
				}
				Road road = new Road(intRoadLine[0],intRoadLine[1],intRoadLine[2],
						intRoadLine[3],intRoadLine[4],intRoadLine[5],intRoadLine[6]);
				Main.road_dict.put(intRoadLine[0], road);
//				System.out.println(road);
        	}
        }
	}
	
	public static void create_cross_dict(String crossPath) throws NumberFormatException, IOException {
		FileReader crossFile = new FileReader(crossPath);
		String crossLine;
		BufferedReader crossbuf = new BufferedReader(crossFile);
        while((crossLine = crossbuf.readLine())!=null) {
        	if(crossLine.contains("#"))
        		continue;
        	else {
        		String newCrossLine [] = new String[5];
				int intCrossLine [] = new int[5];
				crossLine = crossLine.replace("(","");
				crossLine = crossLine.replace(")","");
				newCrossLine = crossLine.split(",");
				for(int i=0;i<newCrossLine.length;i++) {
					newCrossLine[i] = newCrossLine[i].replaceAll(" ","");
					intCrossLine [i] = Integer.parseInt(newCrossLine[i]);
				}
				int[] temp = {intCrossLine[1],intCrossLine[2],intCrossLine[3],intCrossLine[4]};
				Cross cross = new Cross(intCrossLine[0],temp);
				Main.cross_dict.put(intCrossLine[0], cross);
//				System.out.println(cross);
        	}
        }
	}
	
	public static void arrange_preset_data(String presetAnswerPath) throws IOException {
		FileReader presetCarFile = new FileReader(presetAnswerPath);
		String presetCarLine;
		BufferedReader presetCarbuf = new BufferedReader(presetCarFile);
		while((presetCarLine = presetCarbuf.readLine())!=null) {
			if(presetCarLine.contains("#"))
        		continue;
        	else {
        		String newpresetCarLine [] = null;
        		presetCarLine = presetCarLine.replace("(","");
        		presetCarLine = presetCarLine.replace(")","");
        		newpresetCarLine = presetCarLine.split(",");
        		
        		int car_id = Integer.parseInt(newpresetCarLine[0].replaceAll(" ",""));
        		int plan_time = Integer.parseInt(newpresetCarLine[1].replaceAll(" ",""));
        		Car car = Main.car_dict.get(car_id);
        		car.car_plan_time = plan_time;
        		car.car_actual_time = plan_time;
        		
        		ArrayList<Integer> route_plan = new ArrayList<>();
				for(int i=2;i<newpresetCarLine.length;i++) {
					newpresetCarLine[i] = newpresetCarLine[i].replaceAll(" ","");
					route_plan.add(Integer.parseInt(newpresetCarLine[i]));
				}
				car.route_plan = route_plan;
        	}
		}
	}
	
	public static void read_answer(String answerPath) throws IOException {
      FileReader answerFile = new FileReader(answerPath);
      String answerLine;
      BufferedReader answerbuf = new BufferedReader(answerFile);
      while((answerLine = answerbuf.readLine())!=null) {
    	  if(answerLine.contains("#"))
    		  continue;
    	  String newanswerLine [] = null;
    	  answerLine = answerLine.replace("(","");
    	  answerLine = answerLine.replace(")","");
    	  newanswerLine = answerLine.split(",");
    	  
    	  int car_id = Integer.parseInt(newanswerLine[0].replaceAll(" ",""));
  		  int actual_time = Integer.parseInt(newanswerLine[1].replaceAll(" ",""));
  		  Car car = Main.car_dict.get(car_id);
  		  car.car_actual_time = actual_time;
  		  
  		ArrayList<Integer> route_plan = new ArrayList<>();
		for(int i=2;i<newanswerLine.length;i++) {
			newanswerLine[i] = newanswerLine[i].replaceAll(" ","");
			route_plan.add(Integer.parseInt(newanswerLine[i]));
		}
		car.route_plan = route_plan;
      }
	}
	
	public static void write_answer(String answerPath) throws IOException {
		FileWriter fw = new FileWriter(answerPath);
		BufferedWriter bw = new BufferedWriter(fw);
		Car car;
		
		bw.write("#(carId,StartTime,RoadId...)\n");
		Iterator<Map.Entry<Integer, Car>> iter = Main.car_dict.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Integer, Car> entry = iter.next();
			car = entry.getValue();
			if(!Main.preset_car_list.contains(car.car_id)) {
				StringBuffer strBuf = new StringBuffer();
				strBuf.append("(" + car.car_id + "," + car.car_actual_time);
				for(int j=0;j<car.route_plan.size();j++) {
					strBuf.append("," + car.route_plan.get(j));
				}
				strBuf.append(")\n");
				bw.write(strBuf.toString());
			}
		}
		bw.flush();
		bw.close();
		fw.close();
	}
	
    public static void main(String[] args) throws IOException
    {	
    	long startTime=System.currentTimeMillis();   //获取开始时间
    	
        if (args.length != 5) {
            return;
        }
        String carPath = args[0];
        String roadPath = args[1];
        String crossPath = args[2];
        String presetAnswerPath = args[3];
        String answerPath = args[4];

        // 1、建立字典对象，和list对象（优先车，预置车）
        create_car_dict(carPath);
        create_road_dict(roadPath);
        create_cross_dict(crossPath);
        arrange_preset_data(presetAnswerPath);
//        read_answer(answerPath);  // 读答案，将path和time安排上
        
        
        // 2、建立调度器对象，并调度
        Scheduler scheduler = new Scheduler(Main.car_dict, Main.road_dict, 
        		Main.cross_dict, Main.priority_car_list, Main.preset_car_list);
        scheduler.schedule();

        // 3、TODO: write answer.txt
        write_answer(answerPath);
        
        
        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
    }
    
}
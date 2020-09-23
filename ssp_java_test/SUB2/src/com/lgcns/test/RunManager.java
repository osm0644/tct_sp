package com.lgcns.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ����1�� ���� �⺻
 * - �������� �����ϴ� ���Ͽ��� ���� ������ ������ �о� Ư�� ������ ó�� �� ����� ���Ͽ� ���
 */
public class RunManager {

	static final String InputFilePath = "./INFILE/LOCATION.TXT";
	static final String OutputFilePath = "./OUTFILE/PREPOST.TXT";
	static List<String> objList;
	static ArrayList<Bus> busLocList;
	
	static final String InputFilePath2 = "./INFILE/STATION.TXT";
	static final String OutputFilePath2 = "./OUTFILE/ARRIVAL.TXT";
	static List<Station> stationList;
	
	public static void main(String[] args) throws Exception {
		//1. ���� ���� ������ �о� ���κ� ��üȭ �ϱ� => String
		objList = getObjectListByFileLineWithStream(InputFilePath, String.class);
		//objList = getLineListByFileLineWithStream(InputFilePath);
		//2. ���� ������ ��üȭ�� �����͸� ���� �߰� �׼� => Print �� ���Ë� ������ ������ Bus ��ü ����Ʈ�� �����
		busLocList = actionWithObjectList();
		//3. �׼��� ���� ������ �����͸� ���Ͽ� ��� => �յ� ����� ����ϴ� �Լ� �߰� 
		printResultToFile();
		
		//����2.1�� : ���� ���� ������ �о� ���κ� ��üȭ �ϱ�
		stationList = getObjectListByFileLineWithStream(InputFilePath2, Station.class);
		//����2�� �߰� ����
		ArrayList<String> result = actionWithObjectListCombination();
		//����2�� : �׼��� ���� ������ �����͸� ���Ͽ� ���
		printResultToFile2(result);

	}

	//2. ���� ������ ��üȭ�� �����͸� ���� �߰� �׼�
	static ArrayList<Bus> actionWithObjectList() throws FileNotFoundException {
		 ArrayList<Bus> busLocList = new ArrayList<Bus>();
		// ������ ó�� �׼�
		String lastLine = null;
		for(String oneObject : objList) {
			if(!oneObject.equals("PRINT")) {
				lastLine = oneObject;
			}
		}
		
		String[] busList = lastLine.split("#");
		for(int i = 1 ; i < busList.length ; i++) {
			busLocList.add(new Bus(busList[i],busList[0]));
		}
		
		Collections.sort(busLocList);
		
		
		return busLocList;
	}

	//3. ��� ���� ���
	static void printResultToFile() throws FileNotFoundException {
		PrintWriter printWriter = new PrintWriter(new File(OutputFilePath));
		//for�� Ȱ�� ���
		
		for(String oneObject : CreatePrePostBusObect()) {
			printWriter.println(oneObject);
		}
		//stream Ȱ�� ���
		//objList.stream().forEach(o->printWriter.println(o));
		printWriter.close();
	}
	
	static ArrayList<String> CreatePrePostBusObect () {
		ArrayList<String> prepostResult = new ArrayList<String>();
	
		
		prepostResult.add(busLocList.get(0).time 
						   + "#" + busLocList.get(0).busId
						   + "#" + busLocList.get(1).busId + "," + String.format("%05d",(busLocList.get(1).distance - busLocList.get(0).distance))
						   + "#" + "NOBUS,00000");
		
		
		for(int i = 1 ; i < busLocList.size() - 1 ; i++) {
			
			prepostResult.add(busLocList.get(i).time 
					   		 	+ "#" + busLocList.get(i).busId
					   		 	+ "#" + busLocList.get(i+1).busId + "," + String.format("%05d",(busLocList.get(i+1).distance - busLocList.get(i).distance))
					   		    + "#" + busLocList.get(i-1).busId + "," + String.format("%05d",(busLocList.get(i).distance - busLocList.get(i-1).distance)));
		}
		
		int last = busLocList.size() -1 ;
		prepostResult.add(busLocList.get(last).time 
				   + "#" + busLocList.get(last).busId
				   + "#" + "NOBUS,00000"
				   + "#" + busLocList.get(last -1 ).busId + "," + String.format("%05d",(busLocList.get(last).distance - busLocList.get(last-1).distance)));
		
		
		Collections.sort(prepostResult);
		
		return prepostResult;
	}
	
	static ArrayList<String> actionWithObjectListCombination() {
		ArrayList<String> actionResult = new ArrayList<String>();
		// ������ ó�� �׼�
		for(Station station : stationList) {
			for(int i = 0; i < busLocList.size() ; i++ ) {
				// actionResult ����
				if(station.distance < busLocList.get(i).distance) {
					if(i == 0) {
						actionResult.add(busLocList.get(i).time 
								         + "#" + station.stationName
								         + "#" + "NOBUS,00000");
						
					}else {
						actionResult.add(busLocList.get(i-1).time 
						         + "#" + station.stationName
						         + "#" + busLocList.get(i-1).busId +"," +String.format("%05d",(station.distance - busLocList.get(i-1).distance)));
						
					}
					
				   break;
				}
				
				if(i == busLocList.size()-1) {
					actionResult.add(busLocList.get(i).time 
					         + "#" + station.stationName
					         + "#" + busLocList.get(i).busId +"," +String.format("%05d",(station.distance - busLocList.get(i).distance)));
					
				}
			}
		}
		
		
		/*
		 * �¿� å�Ӵ� ȿ��ȭ �ڵ�
		 * ArrayList<String> arrivalList = new ArrayList<String>();
		   for(BusStation oneStation : stationList) {
		   		BusLocation closetBus = null;
		  		for(BusLocation oneBus : busLocList) {
					if(oneStation.location>oneBus.location) {
						closetBus = oneBus;
						continue;
					}
					break;
				}
				arrivalList.add(busLocList.get(0).time+"#"+oneStation.name+"#"+(closetBus==null?"NOBUS,00000":closetBus.name+","+String.format("%05d", oneStation.location-closetBus.location)));
		   }
		 */
		
		return actionResult;
	}
	
	//3. ��� ���� ���
	static void printResultToFile2(ArrayList<String> printData) throws FileNotFoundException {
			PrintWriter printWriter = new PrintWriter(new File(OutputFilePath2));
			//for�� Ȱ�� ���
			
			for(String oneObject : printData) {
				printWriter.println(oneObject);
			}
			//stream Ȱ�� ���
			//objList.stream().forEach(o->printWriter.println(o));
			printWriter.close();
	}
	
	
	//Stream�� ����ؼ� ���� ���κ� ��ü ���
	static <T> List<T> getObjectListByFileLineWithStream(String filePath, Class<T> objectClass) throws IOException {
			Stream<String> stream = Files.lines(Paths.get(filePath));
			List<T> objList = stream.map(line->{
				try {
					return objectClass.getConstructor(String.class).newInstance(line);
				} catch (Exception ex) { ex.printStackTrace(); }
				return null;
			}).collect(Collectors.toList());
			stream.close();
			
			return objList;
	}
	
	//Stream�� ����ؼ� ���� ���� ��� ��������
	static List<String> getLineListByFileLineWithStream(String filePath) throws IOException {
		List<String> lineList = null;
		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
			lineList = stream.collect(Collectors.toList());
		}
		
		return lineList;
	}

}

class Bus implements Comparable<Bus> {
	String busId;
	int distance;
	String time;
	

	public Bus(String parseLine, String time) {
		String[] lineDatas = parseLine.split(",");
		
		busId = lineDatas[0];
		distance = Integer.parseInt(lineDatas[1]);
		this.time = time;
		
	}
	
	@Override
	public int compareTo(Bus var1) {
		// ���̵�� ���� ����
		return distance- var1.distance;
	}
	
	
}


class Station implements Comparable<Station> {
	
	String stationName;
	int distance;
	
	public Station(String parseLine) {
		String[] lineDatas = parseLine.split("#");
		stationName = lineDatas[0];
		distance = Integer.parseInt(lineDatas[1]);
	}

	@Override
	public int compareTo(Station var1) {
		return 0;
	}

}
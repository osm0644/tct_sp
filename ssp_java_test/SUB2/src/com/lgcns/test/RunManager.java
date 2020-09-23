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
 * 문제1번 패턴 기본
 * - 문제에서 제시하는 파일에서 라인 단위로 정보를 읽어 특정 데이터 처리 후 결과를 파일에 출력
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
		//1. 파일 라인 단위로 읽어 라인별 객체화 하기 => String
		objList = getObjectListByFileLineWithStream(InputFilePath, String.class);
		//objList = getLineListByFileLineWithStream(InputFilePath);
		//2. 라인 단위로 객체화한 데이터를 갖고 추가 액션 => Print 가 나올떄 마지막 라인을 Bus 객체 리스트로 만들기
		busLocList = actionWithObjectList();
		//3. 액션을 통해 생성된 데이터를 파일에 출력 => 앞뒤 결과를 출력하는 함수 추가 
		printResultToFile();
		
		//문제2.1번 : 파일 라인 단위로 읽어 라인별 객체화 하기
		stationList = getObjectListByFileLineWithStream(InputFilePath2, Station.class);
		//문제2번 추가 동작
		ArrayList<String> result = actionWithObjectListCombination();
		//문제2번 : 액션을 통해 생성된 데이터를 파일에 출력
		printResultToFile2(result);

	}

	//2. 라인 단위로 객체화한 데이터를 갖고 추가 액션
	static ArrayList<Bus> actionWithObjectList() throws FileNotFoundException {
		 ArrayList<Bus> busLocList = new ArrayList<Bus>();
		// 데이터 처리 액션
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

	//3. 결과 파일 출력
	static void printResultToFile() throws FileNotFoundException {
		PrintWriter printWriter = new PrintWriter(new File(OutputFilePath));
		//for문 활용 출력
		
		for(String oneObject : CreatePrePostBusObect()) {
			printWriter.println(oneObject);
		}
		//stream 활용 출력
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
		// 데이터 처리 액션
		for(Station station : stationList) {
			for(int i = 0; i < busLocList.size() ; i++ ) {
				// actionResult 생성
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
		 * 태영 책임님 효율화 코드
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
	
	//3. 결과 파일 출력
	static void printResultToFile2(ArrayList<String> printData) throws FileNotFoundException {
			PrintWriter printWriter = new PrintWriter(new File(OutputFilePath2));
			//for문 활용 출력
			
			for(String oneObject : printData) {
				printWriter.println(oneObject);
			}
			//stream 활용 출력
			//objList.stream().forEach(o->printWriter.println(o));
			printWriter.close();
	}
	
	
	//Stream을 사용해서 파일 라인별 객체 목록
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
	
	//Stream을 사용해서 파일 라인 목록 가져오기
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
		// 아이디로 오름 차순
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
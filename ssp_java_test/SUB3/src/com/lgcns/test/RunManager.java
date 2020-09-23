package com.lgcns.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
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
	
	static int secTogo;
	
	public static void main(String[] args) throws Exception {
		//1.1. 파일 라인 단위로 읽어 라인별 객체화 하기 => String
		objList = getObjectListByFileLineWithStream(InputFilePath, String.class);
		
		//문제2.1번 : 파일 라인 단위로 읽어 라인별 객체화 하기
		stationList = getObjectListByFileLineWithStream(InputFilePath2, Station.class);
		
		//1.2. 라인 단위로 객체화한 데이터를 갖고 추가 액션 => Print 가 나올떄 마지막 라인을 Bus 객체 리스트로 만들기
		//************정상적일떄 => 기존 로직 //// 정보 없을 때 추가
		//단위를 m/s로 맞추기
		//station에서 가져온 제한 속도를 /3.6d으로
		//값 비교
		//초별 값 계산해서 list에 추가		
		busLocList = actionWithObjectList();
		//1.3. 액션을 통해 생성된 데이터를 파일에 출력 => 앞뒤 결과를 출력하는 함수 추가 
		printResultToFile();
		//문제2번 추가 동작
		ArrayList<String> result = actionWithObjectListCombination();
		//문제2번 : 액션을 통해 생성된 데이터를 파일에 출력
		printResultToFile2(result);
		
		
		//3번 외부 프로그램
		//3-1 정류장별로 가장 빨리 도착하는 차량을 찾기
		//ArrayList<String> result3 = calculateArrivalTime();
		//3-2 외부 프로그램에 출력 
		//transferStationArrivalTime(result3);
		
		createFastestArrivalBusInfoAndSendToExternalProgram();
		
		
	

	}
	
	private static void createFastestArrivalBusInfoAndSendToExternalProgram() throws Exception {
		ArrayList<String> fastestArrivalBusList = new ArrayList<String>();
		for(Station oneStation : stationList) {
			Bus closetBus = null;
			int fastTimeToArrive = Integer.MAX_VALUE;
			
			// 정류장보다 뒤세 있는 버스 필터링
			List<Bus> busListBeforeStation =  busLocList.stream().filter(bus->bus.distance<oneStation.distance).collect(Collectors.toList());
			for(Bus oneBus : busListBeforeStation) {
				int timeToArrive = elapsedTimeToStation(oneBus, oneStation);
				if(fastTimeToArrive>timeToArrive) {
					closetBus = oneBus;
					fastTimeToArrive = timeToArrive;
				}
			}
			// 문자열 도착시간과 도착예정 초를 갖고 계산 후 문자열 변경
			String arriveTimeAfterSec = getTimeAfterSec(busLocList.get(0).time, fastTimeToArrive);
			// 도착 정보 문자열 저장
			fastestArrivalBusList.add(busLocList.get(0).time+"#"+oneStation.stationName+"#"+(closetBus==null?"NOBUS,00:00:00":closetBus.busId+","+arriveTimeAfterSec));
		}
		Collections.sort(fastestArrivalBusList);
		
		//외부 프로그램에 데이터 전송
		transferStationArrivalTime(fastestArrivalBusList);
	}
	
	private static int elapsedTimeToStation(Bus bus, Station station) {
		
		int secondsToGo = 0;
		
		double orgianallyLocation = bus.distance;
		int cnt =0 ;
		while(bus.distance<station.distance) {
			secondsToGo++;
			
			if( station.stationName.equals("STA07") && bus.busId.equals("BUS02") ) {
				
				cnt++;
			}
			bus.distance += getValidSpeed(bus);
		}
		bus.distance = orgianallyLocation;
		if( station.stationName.equals("STA07") && bus.busId.equals("BUS02") ) {
			System.out.println(cnt);
			System.out.println(orgianallyLocation);
			System.out.println(station.distance);
			System.out.println(secondsToGo);
			
		}
		return secondsToGo;
	}
	
	private static double getValidSpeed(Bus preBus) {
		
		Station lastValidStation = null;
		for(Station oneStation : stationList) {
			if(preBus.distance > oneStation.distance) {
				lastValidStation = oneStation;
			} else {
				break;
			}
		}
		if(lastValidStation==null) {
			return 0;
		}
		
		return preBus.speed>lastValidStation.speed?lastValidStation.speed:preBus.speed;
		
	}
	
	private static String getTimeAfterSec(String timeString, int seconds) throws ParseException {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		long aferTime = timeFormat.parse(timeString).getTime()+(seconds*1000);
		
		/*
		 * 시간 계산 하는 방법
		 * 
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(new Date(aferTime));
		
		cal.add(Calendar.MINUTE, 5);
		System.out.println("10분후 : " + timeFormat.format(cal.getTime()));
		cal.add(Calendar.MINUTE, 5);
		System.out.println("10분후 : " + timeFormat.format(cal.getTime()));
		 * 
		 * 
		 */


		return timeFormat.format(new Date(aferTime));
		
	}
	
	
	static void transferStationArrivalTime(ArrayList<String> fastestArrivalBusList) throws IOException {
		Process theProcess = Runtime.getRuntime().exec("./SIGNAGE.EXE");
		BufferedWriter outStream =new BufferedWriter(new OutputStreamWriter(theProcess.getOutputStream()));
		
		for(String arriveInfo : fastestArrivalBusList) {
			outStream.write(arriveInfo+"\n");			
		}

		outStream.close();
	}
	
	
	//2. 라인 단위로 객체화한 데이터를 갖고 추가 액션
	static ArrayList<Bus> actionWithObjectList() throws FileNotFoundException {
		 ArrayList<Bus> busLocList = new ArrayList<Bus>();
		 ArrayList<Bus> tmpbusLocList = new ArrayList<Bus>();
		// 데이터 처리 액션
		// 결국은 PRINT 마지막 줄을 return 하면 됨
		// 한줄 한줄 읽으면서 객체 만들기
		
		//맨처음 bus는 하드코딩
		String[] busList = objList.get(0).split("#");
		for(int i = 1 ; i < busList.length ; i++) {
			busLocList.add(new Bus(busList[i],busList[0],Double.MAX_VALUE));
		}
		tmpbusLocList = busLocList;
		
		for( int i = 1 ; i < objList.size() -1 ; i++) {
			
			String[] tmpbusList = objList.get(i).split("#");
			busLocList = new ArrayList<Bus>();
			if(tmpbusList.length > 2) {
		    //정상적인 경우
				for(int j = 1 ; j < tmpbusList.length ; j++) {
					
					busLocList.add(new Bus(tmpbusList[j],tmpbusList[0],0));
					double tmpSpeed = busLocList.get(j-1).distance - tmpbusLocList.get(j-1).distance;
					busLocList.get(j-1).speed = Math.min(getValidSpeed(busLocList.get(j-1).distance), tmpSpeed);
				}
			}else{
			//손실된 경우
				for(int j = 0 ; j < tmpbusLocList.size() ; j++) {
					
					busLocList.add(new Bus(tmpbusLocList.get(j).busId,tmpbusLocList.get(j).distance+tmpbusLocList.get(j).speed,tmpbusList[0],0));
					double tmpSpeed = busLocList.get(j).distance - tmpbusLocList.get(j).distance;
					busLocList.get(j).speed = Math.min(getValidSpeed(busLocList.get(j).distance), tmpSpeed);
					
				}
				
			}
			
			tmpbusLocList = busLocList;
		}
		
		
		Collections.sort(tmpbusLocList);
		
		return busLocList;
	}
	
	static double getValidSpeed(double curDist) {
		
		int validSpeed = 0;
		
		for(Station station :stationList) {
			
			if(curDist > station.distance) {
				validSpeed = station.speed;
			}

		}
		
		return validSpeed;

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
						   + "#" + busLocList.get(1).busId + "," + String.format("%05d",(int)(busLocList.get(1).distance - busLocList.get(0).distance))
						   + "#" + "NOBUS,00000");
		
		
		for(int i = 1 ; i < busLocList.size() - 1 ; i++) {
			
			prepostResult.add(busLocList.get(i).time 
					   		 	+ "#" + busLocList.get(i).busId
					   		 	+ "#" + busLocList.get(i+1).busId + "," + String.format("%05d",(int)(busLocList.get(i+1).distance - busLocList.get(i).distance))
					   		    + "#" + busLocList.get(i-1).busId + "," + String.format("%05d",(int)(busLocList.get(i).distance - busLocList.get(i-1).distance)));
		}
		
		int last = busLocList.size() -1 ;
		prepostResult.add(busLocList.get(last).time 
				   + "#" + busLocList.get(last).busId
				   + "#" + "NOBUS,00000"
				   + "#" + busLocList.get(last -1 ).busId + "," + String.format("%05d",(int)(busLocList.get(last).distance - busLocList.get(last-1).distance)));
		
		
		Collections.sort(prepostResult);
		
		return prepostResult;
	}
	
	static ArrayList<String> calculateArrivalTime() throws ParseException {
		ArrayList<String> actionResult = new ArrayList<String>();
		// 데이터 처리 액션
		//각각 객체가 PRINT시간에 어디 있을지 계산
	    //
		for(Station station : stationList) {
			for(int i = 0; i < busLocList.size() ; i++ ) {
				// actionResult 생성
				if(station.distance < busLocList.get(i).distance) {
					if(i == 0) {
						actionResult.add(busLocList.get(i).time 
								         + "#" + station.stationName
								         + "#" + "NOBUS,00:00:00");
						
					}else {
						actionResult.add(busLocList.get(i-1).time 
						         + "#" + station.stationName
						         + "#" + busLocList.get(i-1).busId +"," + getArrivalTime(station,busLocList.get(i-1)));
						
					}
					
				   break;
				}
				
				if(i == busLocList.size()-1) {
					actionResult.add(busLocList.get(i).time 
					         + "#" + station.stationName
					         + "#" + busLocList.get(i).busId +"," + getArrivalTime(station,busLocList.get(i)));
					
				}
			}
		}
		
		return actionResult;
	}
	
	
	static String getArrivalTime(Station target, Bus cur) throws ParseException {
		
		double sectime = 0;
		double curDist = cur.distance;
		for(int i = 0 ; i < stationList.size() ; i++) {
			
			if(curDist < stationList.get(i).distance) {
				sectime += (stationList.get(i).distance-curDist)/Math.min(getValidSpeed(curDist),cur.speed);
				curDist = stationList.get(i).distance;
			}
			
			if(stationList.get(i).stationName.equals(target.stationName)) {
				break;
			}
			
		}
		
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Calendar cal  = Calendar.getInstance();
		Date dateTIme = timeFormat.parse(cur.time);
		cal.setTime(dateTIme);
		cal.add(Calendar.SECOND, (int) sectime);

	
	
		return timeFormat.format(cal.getTime());
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
						         + "#" + busLocList.get(i-1).busId +"," +String.format("%05d",(int)(station.distance - busLocList.get(i-1).distance)));
						
					}
					
				   break;
				}
				
				if(i == busLocList.size()-1) {
					actionResult.add(busLocList.get(i).time 
					         + "#" + station.stationName
					         + "#" + busLocList.get(i).busId +"," +String.format("%05d",(int)(station.distance - busLocList.get(i).distance)));
					
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
	double distance;
	String time;
	double speed; 
	

	public Bus(String parseLine, String time, double speed) {
		String[] lineDatas = parseLine.split(",");
		
		busId = lineDatas[0];
		distance = Integer.parseInt(lineDatas[1]);
		this.time = time;
		this.speed = speed;
		
	}
	
	public Bus(String busId, double distance, String time, double speed) {
		
		this.busId = busId;
		this.distance = distance;
		this.time = time;
		this.speed = speed;
		
	}
	
	@Override
	public int compareTo(Bus var1) {
		// 아이디로 오름 차순
		return (int) (distance- var1.distance);
	}
	
	
}


class Station implements Comparable<Station> {
	
	String stationName;
	int distance;
	int speed;
	
	public Station(String parseLine) {
		String[] lineDatas = parseLine.split("#");
		stationName = lineDatas[0];
		distance = Integer.parseInt(lineDatas[1]);
		speed = Integer.parseInt(lineDatas[2])*1000/(60*60);
	}

	@Override
	public int compareTo(Station var1) {
		return 0;
	}

}
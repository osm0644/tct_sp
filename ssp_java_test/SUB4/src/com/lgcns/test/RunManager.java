package com.lgcns.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class RunManager {

	//���� ����
	static List<BusLocation> busLocList = new ArrayList<BusLocation>();
	//������ ����
	static List<BusStation> stationList = new ArrayList<BusStation>();
	
	public static void main(String[] args) throws Exception {
		
		//2������ : ������ ���� - ���� ���� ��üȭ - Util�Լ� ���
		stationList = getObjectListByFileLineWithStream("./INFILE/STATION.TXT", BusStation.class);
		
		//4�� ����//////////////////////////////////////////////
		// 1. ���� ������ socket ����� ���� �޾ƿ�
		// 2. ��� ��ȣ(Mobile socket)�� socket���� ��û ���(�°� ���� ����) ����
		// 3. (�ɼ�) Thread Pool ���
		//////////////////////////////////////////////////////
		//ExecutorService executorService = Executors.newFixedThreadPool(10); // Thread Pool ���
		try(ServerSocket serverSocket = new ServerSocket(9876)) {
			while(true) {
				Socket client = serverSocket.accept();
				BusLocationReceiver receiver = new BusLocationReceiver(client);
				receiver.start(); // �⺻ Thread ���
				//executorService.submit(receiver); // Thread Pool ���
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// �۾� ť�� ����ϰ� �ִ� ��� �۾��� ó���� �ڿ� ������Ǯ�� ���� - ������ ���� �����̱� ������ �Ʒ� ȣ�� X
		//executorService.shutdown();
		
	}

	public static String createPassengerArrivalTimeAndSend(String passengerInfo) throws ParseException {
		String[] passengerInfos = passengerInfo.split("#");
		int passengerArrivalElapsedTime = getPassengerArriveTime(passengerInfos[0], Integer.parseInt(passengerInfos[1]));
		String passengerArrivalTime = getTimeAfterSec(busLocList.get(0).time,passengerArrivalElapsedTime);
		return passengerArrivalTime;
	}
	
	private static int getPassengerArriveTime(String stationName, int passengerLocation) {
		
		BusStation getonStation = null;
		for(int i=0; i<stationList.size(); i++) {
			if(stationList.get(i).location>passengerLocation) {
				getonStation = stationList.get(i);
				break;						
			}
		}
	
		int fastBusTime = Integer.MAX_VALUE;
		for(BusStation oneStation : stationList) {
			if(oneStation.name.equals(stationName)) {
				for(BusLocation oneBus : busLocList) {
					if(oneBus.location<getonStation.location) {
						int eslaptedTime = elapsedTimeToStation(oneBus,oneStation);
						if(eslaptedTime<fastBusTime) {
							fastBusTime = eslaptedTime;
						}
					}
				}
			}
		}
		
		return fastBusTime;
	}
	
	public static void createFastestArrivalBusInfoAndSendToExternalProgram() throws Exception {
		ArrayList<String> fastestArrivalBusList = new ArrayList<String>();
		for(BusStation oneStation : stationList) {
			BusLocation closetBus = null;
			int fastTimeToArrive = Integer.MAX_VALUE;
			
			// �����庸�� �ڼ� �ִ� ���� ���͸�
			List<BusLocation> busListBeforeStation =  busLocList.stream().filter(bus->bus.location<oneStation.location).collect(Collectors.toList());
			for(BusLocation oneBus : busListBeforeStation) {
				int timeToArrive = elapsedTimeToStation(oneBus, oneStation);
				if(fastTimeToArrive>timeToArrive) {
					closetBus = oneBus;
					fastTimeToArrive = timeToArrive;
				}
			}
			// ���ڿ� �����ð��� �������� �ʸ� ���� ��� �� ���ڿ� ����
			String arriveTimeAfterSec = getTimeAfterSec(busLocList.get(0).time, fastTimeToArrive);
			// ���� ���� ���ڿ� ����
			fastestArrivalBusList.add(busLocList.get(0).time+"#"+oneStation.name+"#"+(closetBus==null?"NOBUS,00:00:00":closetBus.name+","+arriveTimeAfterSec));
		}
		Collections.sort(fastestArrivalBusList);
		
		//�ܺ� ���α׷��� ������ ����
		transferStationArrivalTime(fastestArrivalBusList);
	}
	
	/**
	 * �ܺ� ���α׷��� ������ ����
	 */
	public static void transferStationArrivalTime(ArrayList<String> fastestArrivalBusList) throws IOException {
		Process theProcess = Runtime.getRuntime().exec("./SIGNAGE.EXE");
		BufferedWriter outStream =new BufferedWriter(new OutputStreamWriter(theProcess.getOutputStream()));
		
		for(String arriveInfo : fastestArrivalBusList) {
			outStream.write(arriveInfo+"\n");			
		}

		outStream.close();
	}
	
	private static String getTimeAfterSec(String timeString, int seconds) throws ParseException {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		long aferTime = timeFormat.parse(timeString).getTime()+(seconds*1000);
		return timeFormat.format(new Date(aferTime));
		
	}
	
	private static int elapsedTimeToStation(BusLocation bus, BusStation station) {
		
		int secondsToGo = 0;
		
		int orgianallyLocation = bus.location;
		while(bus.location<station.location) {
			secondsToGo++;
			
			bus.location += getValidSpeed(bus);
		}
		bus.location = orgianallyLocation;
		
		return secondsToGo;
	}
	
	public static int getValidSpeed(BusLocation preBus) {
		
		BusStation lastValidStation = null;
		for(BusStation oneStation : stationList) {
			if(preBus.location > oneStation.location) {
				lastValidStation = oneStation;
			} else {
				break;
			}
		}
		if(lastValidStation==null) {
			return 0;
		}
		
		return preBus.lastSpeed>lastValidStation.limitSpeed?lastValidStation.limitSpeed:preBus.lastSpeed;
		
	}
	
	public static void createArrivalInfoAndWriteFile() throws FileNotFoundException {
		//���� ���� ����
		ArrayList<String> arrivalList = new ArrayList<String>();
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
		
		//ó�� ��� ���� ����
		PrintWriter printWriter = new PrintWriter(new File("./OUTFILE/ARRIVAL.TXT"));
		for(String oneArrival : arrivalList) {
			printWriter.println(oneArrival);
		}
		printWriter.close();
	}
	
	public static void createBusLocationInfoFromFile(String filePath) throws IOException {
		List<String> fileLineList = getLineListByFileLineWithStream(filePath);
		for(String fileLine : fileLineList) {
			if("PRINT".equals(fileLine)) {
				break;
			}
			
			//���ڿ� ó�� �� ������ ��üȭ
			String[] lineDatas = fileLine.split("#");
			// ��ġ���� ������ ��� 
			if(lineDatas.length > 1 ) {
				int busLocIndex = 0;
				for(int i=1; i<lineDatas.length; i++) {
					String[] busLocDatas = lineDatas[i].split(",");
					
					if(busLocList.size() < lineDatas.length-1) {
						busLocList.add(new BusLocation(lineDatas[0], busLocDatas[0], Integer.parseInt(busLocDatas[1]),0));
						
					} else {
						int lastSpeed = Integer.parseInt(busLocDatas[1]) - busLocList.get(busLocIndex).location;
						
						busLocList.get(busLocIndex).time = lineDatas[0];
						busLocList.get(busLocIndex).name =  busLocDatas[0];
						busLocList.get(busLocIndex).location = Integer.parseInt(busLocDatas[1]);
						busLocList.get(busLocIndex).lastSpeed = lastSpeed;
						
						busLocIndex++;
					}					
				}
			} else { // ��ġ���� �ս��� �߻��ϴ� ��� :  ��ġ���� �ս��� ����� ���� ��ġ ���
				ArrayList<BusLocation> newBusLocList = new ArrayList<BusLocation>();
				for(BusLocation preBus : busLocList) {
					int validSpeed = getValidSpeed(preBus);
					newBusLocList.add(new BusLocation(lineDatas[0], preBus.name, preBus.location+validSpeed, preBus.lastSpeed));
				}
				busLocList = newBusLocList;
			}
		}
		// �Ÿ����� ����
		Collections.sort(busLocList);
	}
	
	public static void createPrePostBusInfoAndPrint() throws FileNotFoundException {
		//�յڹ��� ���� ����
		ArrayList<String> prePostList = new ArrayList<String>();
		prePostList.add(busLocList.get(0).time
				+"#"+busLocList.get(0).name
				+"#"+busLocList.get(1).name+","+String.format("%05d", busLocList.get(1).location-busLocList.get(0).location)
				+"#NOBUS,00000");
		int i=1;
		for(; i<busLocList.size()-1; i++) {
			prePostList.add(busLocList.get(i).time
					+"#"+busLocList.get(i).name
					+"#"+busLocList.get(i+1).name+","+String.format("%05d", busLocList.get(i+1).location-busLocList.get(i).location)
					+"#"+busLocList.get(i-1).name+","+String.format("%05d", busLocList.get(i).location-busLocList.get(i-1).location)
					);
		}
		prePostList.add(busLocList.get(i).time
				+"#"+busLocList.get(i).name
				+"#NOBUS,00000"
				+"#"+busLocList.get(i-1).name+","+String.format("%05d", busLocList.get(i).location-busLocList.get(i-1).location)
				);
		//������ȣ ����
		Collections.sort(prePostList);

		//ó�� ��� ���� ����
		PrintWriter printWriter = new PrintWriter(new File("./OUTFILE/PREPOST.TXT"));
		for(String onePrePost : prePostList) {
			printWriter.println(onePrePost);
		}
		printWriter.close();
	}
	
	// Util - Stream�� ����ؼ� ���� ���� ��� ��������
	public static List<String> getLineListByFileLineWithStream(String filePath) throws IOException {
		List<String> lineList = null;
		try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
			lineList = stream.collect(Collectors.toList());
		}
		
		return lineList;
	}
	
	// Util - Stream�� ����ؼ� ���� ���κ� ��ü ��� - ��� Ŭ������ �����ϵ��� �߻�ȭ
	public static <T> List<T> getObjectListByFileLineWithStream(String filePath, Class<T> objectClass) throws IOException {
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

}

class BusLocation implements Comparable<BusLocation> {

	String time;
	int timeSeconds;
	String name;
	int location;
	int lastSpeed;

	public BusLocation(String time, String name, int location, int lastSpeed) {
		super();
		this.time = time;
		this.name = name;
		this.location = location;
		this.lastSpeed = lastSpeed;
	}

	@Override
	public int compareTo(BusLocation var1) {
		return location- var1.location;
	}
}

class BusStation implements Comparable<BusStation> {

	String name;
	int location;
	int limitSpeed;

	public BusStation(String fileLine) {
		String[] lineDatas = fileLine.split("#");
		this.name = lineDatas[0];
		this.location = Integer.parseInt(lineDatas[1]);
		this.limitSpeed = Integer.parseInt(lineDatas[2])*1000/(60*60);
	}
	
	public BusStation(String name, int location, int limitSpeed) {
		this.name = name;
		this.location = location;
		this.limitSpeed = limitSpeed*1000/(60*60);
	}

	@Override
	public int compareTo(BusStation var1) {
		return location- var1.location;
	}
}


/**
 * Client�κ��� ���� ������ �޾� ó���ϴ� ���α׷�
 *
 */
class BusLocationReceiver extends Thread {
	
	private Socket client;

	public BusLocationReceiver(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		BusLocation lastBusLocation = new BusLocation("", "", 0, 0);
		try {
			BufferedReader clientInput = new BufferedReader(new InputStreamReader(client.getInputStream()));
			String busName = clientInput.readLine();
			System.out.println("Client Connected : " + busName);
			if("MOBILE".equals(busName)) {
				String printCommand = clientInput.readLine();
				String passengerInfo = clientInput.readLine();
				String passengerArriavlTime = RunManager.createPassengerArrivalTimeAndSend(passengerInfo);
				PrintWriter mobileOutput = new PrintWriter(new OutputStreamWriter(client.getOutputStream()),true);
				mobileOutput.print(passengerArriavlTime);
				mobileOutput.close();
				
				// �Ÿ����� ����
				Collections.sort(RunManager.busLocList);
				//1������ : ���
				RunManager.createPrePostBusInfoAndPrint();
				//2������ : ���
				RunManager.createArrivalInfoAndWriteFile();
				//����3�� :  �����嵵�������ð� ��� �� �ܺ� ���α׷����� ó�� ��� ����
				RunManager.createFastestArrivalBusInfoAndSendToExternalProgram();
				
			} else {
				String line = null;
				while((line=clientInput.readLine()) != null) {
					//���ڿ� ó�� �� ������ ��üȭ
					String[] lineDatas = line.split("#");
					// ��ġ���� ������ ��� 
					if(lineDatas.length > 1 ) {
						int location = Integer.parseInt(lineDatas[1]);
						lastBusLocation = new BusLocation(lineDatas[0], busName, location, location-lastBusLocation.location);
					} else { // �սǵ� ���
						int validSpeed = RunManager.getValidSpeed(lastBusLocation);
						lastBusLocation = new BusLocation(lineDatas[0], busName, lastBusLocation.location+validSpeed, validSpeed);
					}
				}
				RunManager.busLocList.add(lastBusLocation);
			}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
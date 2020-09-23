package com.lgcns.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * ����2�� ���� �⺻
 * - ����1�� ���� : �������� �����ϴ� ���Ͽ��� ���� ������ ������ �о� Ư�� ������ ó�� �� ����� ���Ͽ� ���
 * - ����2�� : 2.1 �߰� ������ �ְ� ���� ������ ������ ���� �Ŀ� ����1������ ������ �����Ϳ� �����ؼ� ������ �� ������ ó�� �� ����� ���Ͽ� ���
 * - ����2�� : 2.2 �߰� ������ �ܺ� �����Ϳ��� ���� �Ŀ� ����1������ ������ �����Ϳ� �����ؼ� ������ �� ������ ó�� �� ����� ���Ͽ� ���
 * - ����3�� : 3.1 �߰� ������ ��Ʈ��ũ ����� ���� �а� ����1,2������ ������ �����Ϳ� �����ؼ� ������ �� ������ ó�� �� ����� ���� Ŭ���̾�Ʈ�� ���� 
 *              ������ ó�� ������ �������� �޻��
 * - ����4�� : 4.1 ���� ����� ���� ��Ƽ ���� ȯ��(Thread)
 * - ����4�� : 4.2 �߰� ���� �Ǵ� �ű� �ڹ� ��� ����(Reflection... ) �� ���� ó�� 
 */
public class Quiz4Pattern1 {

	static final String InputFilePath = "./INPUT/READFILE.TXT";
	static final String OutputFilePath = "./OUTPUT/WRITEFILE.TXT";
	static List<FileLineObject1> obj1List;
	
	static final String InputFilePath2 = "./INPUT/READFILE2.TXT";
	static final String InputExternalProgramPath = "./INPUT/EXTPRO.EXE";
	static final String OutputFilePath2 = "./OUTPUT/WRITEFILE2.TXT";
	static List<LineObject2> obj2List;
	
	private static final int SERVER_PORT = 9876;
	static List<SocketLineObject3> obj3ListFromSocket;
	static final String ExternalJarPath = "./Reflection.jar";
	
	public static void main(String[] args) throws Exception {
		//����1�� : ���� ���� ������ �о� ���κ� ��üȭ �ϱ�
		obj1List = getObjectListByFileLineWithStream(InputFilePath, FileLineObject1.class);
		//����1�� :  ���� ������ ��üȭ�� �����͸� ���� �߰� �׼�
		Object actionResult = actionWithObjectList();
		//����1�� : �׼��� ���� ������ �����͸� ���Ͽ� ���
		printResultToFile(actionResult);	
		
		//����2.1�� : ���� ���� ������ �о� ���κ� ��üȭ �ϱ�
		obj2List = getObjectListByFileLineWithStream(InputFilePath2, LineObject2.class);
		//����2.2�� : ���� ���� ������ �о� ���κ� ��üȭ �ϱ�
		obj2List = getObjectListByFileLineFromExternalProgram(InputExternalProgramPath, LineObject2.class);
		//����2�� �߰� ����
		Object result = actionWithObjectListCombination();
		//����2�� : �׼��� ���� ������ �����͸� ���Ͽ� ���
		printResultToFile2(result);
		
		//����3�� : ������ ���� ������ ���� ���� - ������ �ȿ��� ó��
		//����4.1�� : �����带 Ȱ���� ��Ƽ ���� ó�� - ������Ǯ ����
		//https://victorydntmd.tistory.com/135
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		try(ServerSocket serverSocket = new ServerSocket(SERVER_PORT)){
			//Ŭ���̾�Ʈ ���� ��� - blocking
			Socket client = serverSocket.accept();
			//Ŭ���̾�Ʈ ���� Thead�� ó�� �۾� �Ҵ�
			SocketClientHandler clientHandler = new SocketClientHandler(client);
			executorService.submit(clientHandler);
		}
		
		//���⼭�� socket.accept(); �� ��ŷ�� �÷� ������� ���� ����
//		//������ ���� �ñ��� ��� - �� �����ߴ� Thread�� ���� �� �Ŀ� main������ ������ ���� ��
//		executorService.shutdown();
//		//executorService.awaitTermination(10, TimeUnit.SECONDS);
//		while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) {
//            System.out.println("Not yet. Still waiting for termination");
//        }
	}

	static void readFromNetworkSocket(Socket client) throws IOException {
		//byteó���� ���� inputstream/outputstream
		try(InputStream inputStream = client.getInputStream();
				OutputStream outputStream = client.getOutputStream()) {
		
			// stream ó��
//			byte[] inputBytes = new byte[30];
//			//1.1 stream �б�
//			int byteLength = client.getInputStream().read(inputBytes);
//			System.out.println(new String(Arrays.copyOf(inputBytes, byteLength)));
//			//1.2 stream ����
//			outputStream.write("Output Result".getBytes());
//			outputStream.flush();
			
			obj3ListFromSocket = new ArrayList<SocketLineObject3>();
			///////////////////////////////////////////////////////////////////////////////
			//2.1 ���ڿ�(����) ó���� ���� inputstream/outputstream
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));
			PrintWriter outputPrinter = new PrintWriter(new OutputStreamWriter(outputStream), true);
			//2.2 ���ڿ�(����) �б�
			String line = null;
            while((line=inputReader.readLine()) != null) {
            	obj3ListFromSocket.add(new SocketLineObject3(line));
			}
			//2.3 ���ڿ�(����) ����
			outputPrinter.println("Output Result");
			outputPrinter.flush();
			///////////////////////////////////////////////////////////////////////////////
			
		}
	}
	
	static Object actionWithObjectListCombination2() throws Exception {
		Object actionResult = null;
		// ������ ó�� �׼� - ó�� ������ �������� �޻��
		for(FileLineObject1 oneObject1 : obj1List) {
			for(LineObject2 oneObject2 : obj2List) {
				for(SocketLineObject3 oneObject3 : obj3ListFromSocket) {
					ationWithAddedLoginOrTech();
					// actionResult ����
				}
			}
		}
		
		return actionResult;
	}
	
	//����4.2�� : �߰� ���� �Ǵ� �ű� �ڹ� ��� ����(Reflection... ) �� ���� ó�� 
	static Object ationWithAddedLoginOrTech() throws Exception {
		//����4.2�� : �ܺ� jar���� Ŭ���� �޼ҽ� ȣ��
		File jarFile = new File(ExternalJarPath);
		
		URL classURL = new URL("jar:" + jarFile.toURI().toURL() + "!/");
		URLClassLoader classLoader = new URLClassLoader(new URL[] {classURL});
		
		Class<?> c = classLoader.loadClass("Calculator");
		Constructor<?> constructor = c.getConstructor(new Class[]{});
		Object object = constructor.newInstance(new Object[]{});
		
		Method method = c.getMethod("add", new Class[]{Integer.TYPE, Integer.TYPE});
		Object returnValue = method.invoke(object, 1, 2);
		System.out.println(returnValue);
		
		return returnValue;
	}
	
	static void printResultToSocketClient(Socket client, Object result) throws Exception {
		PrintWriter outputPrinter = new PrintWriter(new OutputStreamWriter(client.getOutputStream()), true);
		outputPrinter.println("Output Result");
		outputPrinter.flush();
	}
	

	static Object actionWithObjectListCombination() {
		Object actionResult = null;
		// ������ ó�� �׼�
		for(FileLineObject1 oneObject1 : obj1List) {
			for(LineObject2 oneObject2 : obj2List) {
				// actionResult ����
			}
		}
		
		return actionResult;
	}
	
	static void printResultToFile2(Object result) throws FileNotFoundException {
		PrintWriter printWriter = new PrintWriter(new File(OutputFilePath2));
		//for�� Ȱ�� ���
		printWriter.println("Quiz2 Result");
		
		printWriter.close();
	}

	static Object actionWithObjectList() throws FileNotFoundException {
		Object actionResult = null;
		// ������ ó�� �׼�
		for(FileLineObject1 oneObject : obj1List) {
			// actionResult ����
		}
		return actionResult;
	}

	//3. ��� ���� ���
	static void printResultToFile(Object printData) throws FileNotFoundException {
		PrintWriter printWriter = new PrintWriter(new File(OutputFilePath));
		//for�� Ȱ�� ���
		for(FileLineObject1 oneObject : obj1List) {
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
	
	/**
	 * �ܺ� ���α׷��� ���ؼ� ������ �б� - ��Ʈ�� ���� ���
	 * @param command
	 * @return
	 * @throws Exception
	 */
	static List<String> getStringListByFileLineFromExternalProgram(String command) throws Exception {
		Process theProcess = Runtime.getRuntime().exec(command);
	    BufferedReader inStream = new BufferedReader(new InputStreamReader( theProcess.getInputStream(),"euc-kr"));
	    List<String> readData = new ArrayList<String>();
	    String line = null;
	    while ( ( line = inStream.readLine( ) ) != null ) {
	    	readData.add(line);
	    }
	    return readData;		
	}
	
	/**
	 * �ܺ� ���α׷��� ���ؼ� ������ �б� - ���� ���� ��äȭ �� ��ü ���
	 * @param <T> ���� ��ü Ÿ��
	 * @param command
	 * @param objectClass
	 * @return
	 * @throws Exception
	 */
	static <T> List<T> getObjectListByFileLineFromExternalProgram(String command, Class<T> objectClass) throws Exception {
		Process theProcess = Runtime.getRuntime().exec(command);
	    BufferedReader inStream = new BufferedReader(new InputStreamReader( theProcess.getInputStream(),"euc-kr"));
	    List<String> readData = new ArrayList<String>();
	    String line = null;
	    while ( ( line = inStream.readLine( ) ) != null ) {
	    	readData.add(line);
	    }
	    
	    List<T> objList = readData.stream().map(s->{
			try {
				return objectClass.getConstructor(String.class).newInstance(s);
			} catch (Exception ex) { ex.printStackTrace(); }
			return null;
		}).collect(Collectors.toList());
		
	    return objList;		
	}

}

class FileLineObject1 implements Comparable<FileLineObject1> {
	String id;
	String name;
	int age;

	public FileLineObject1(String parseLine) {
		String[] lineDatas = parseLine.split(",");
		
		id = lineDatas[0];
		name = lineDatas[1];
		age = Integer.parseInt(lineDatas[2]);
	}
	
	public FileLineObject1(String id, String name, int age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}


	@Override
	public int compareTo(FileLineObject1 var1) {
		// ���̵�� ���� ����
		return id.compareTo(var1.id);
	}
	
	@Override
	public String toString() {
		return "FileLineObject1 [id=" + id + ", name=" + name + ", age=" + age + "]";
	}
}

class LineObject2 implements Comparable<LineObject2> {
	
	public LineObject2(String parseLine) {
		String[] lineDatas = parseLine.split(",");
	}

	@Override
	public int compareTo(LineObject2 var1) {
		return 0;
	}

	@Override
	public String toString() {
		return "LineObject2 []";
	}
}

class SocketLineObject3 implements Comparable<SocketLineObject3> {
	
	public SocketLineObject3(String parseLine) {
		String[] lineDatas = parseLine.split(",");
		// ...
	}

	@Override
	public int compareTo(SocketLineObject3 var1) {
		// TODO Auto-generated method stub
		return 0;
	}
}

class SocketClientHandler extends Thread {
	
	Socket client;
	
	public SocketClientHandler(Socket client) {
		this.client = client;		
	}

	@Override
	public void run() {
		try {
			//����3�� : ���Ͽ��� ������ �б�
			Quiz4Pattern1.readFromNetworkSocket(client);
			//����3�� �߰� ����
			Object result3 = Quiz4Pattern1.actionWithObjectListCombination2();
			//����3�� : �׼��� ���� ������ �����͸� ���Ͽ� ���
			Quiz4Pattern1.printResultToSocketClient(client, result3);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
} 
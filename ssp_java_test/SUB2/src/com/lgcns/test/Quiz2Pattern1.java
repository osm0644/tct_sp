package com.lgcns.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * ����2�� ���� �⺻
 * - ����1�� ���� : �������� �����ϴ� ���Ͽ��� ���� ������ ������ �о� Ư�� ������ ó�� �� ����� ���Ͽ� ���
 * - ����2�� : 2.1 �߰� ������ �ְ� ���� ������ ������ ���� �Ŀ� ����1������ ������ �����Ϳ� �����ؼ� ������ �� ������ ó�� �� ����� ���Ͽ� ���
 * - ����2�� : 2.2 �߰� ������ �ܺ� �����Ϳ��� ���� �Ŀ� ����1������ ������ �����Ϳ� �����ؼ� ������ �� ������ ó�� �� ����� ���Ͽ� ��� 
 */
public class Quiz2Pattern1 {

	static final String InputFilePath = "./INPUT/READFILE.TXT";
	static final String OutputFilePath = "./OUTPUT/WRITEFILE.TXT";
	static List<FileLineObject1> obj1List;
	
	static final String InputFilePath2 = "./INPUT/READFILE2.TXT";
	static final String InputExternalProgramPath = "./INPUT/EXTPRO.EXE";
	static final String OutputFilePath2 = "./OUTPUT/WRITEFILE2.TXT";
	static List<LineObject2> obj2List;
	
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
		for(LineObject2 oneObject : obj2List) {
			printWriter.println(oneObject);
		}
		
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
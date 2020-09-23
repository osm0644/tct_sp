package com.lgcns.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ����1�� ���� �⺻
 * - �������� �����ϴ� ���Ͽ��� ���� ������ ������ �о� Ư�� ������ ó�� �� ����� ���Ͽ� ���
 */
public class RunManager2 {

	static final String InputFilePath = "./INFILE/PERSON.TXT";
	static final String OutputFilePath = "./OUTFILE/PERSON_OUTPUT.TXT";
	
	static List<FileLineObject> objList;
	static List<String> stringObjList;
	
	public static void main(String[] args) throws Exception {
		//1. ���� ���� ������ �о� ���κ� ��üȭ �ϱ� => ���δ����� �о FileLineObjectŬ������ 1. ��ülist Ȥ�� 2. String 
		objList = getObjectListByFileLineWithStream(InputFilePath, FileLineObject.class);
		stringObjList = getLineListByFileLineWithStream(InputFilePath); //String���� �������� �̷���
		
		//2. ���� ������ ��üȭ�� �����͸� ���� �߰� �׼�
		Object actionResult = actionWithObjectList();
		
		//3. �׼��� ���� ������ �����͸� ���Ͽ� ���
		printResultToFile(actionResult);

	}

	//2. ���� ������ ��üȭ�� �����͸� ���� �߰� �׼�
	static Object actionWithObjectList() throws FileNotFoundException {
		Object actionResult = null;
		// ������ ó�� �׼�
		for(FileLineObject oneObject : objList) {
			// actionResult ����
		}
		return actionResult;
	}

	//3. ��� ���� ���
	static void printResultToFile(Object printData) throws FileNotFoundException {
		PrintWriter printWriter = new PrintWriter(new File(OutputFilePath));
		//for�� Ȱ�� ���
		for(FileLineObject oneObject : objList) {
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

class FileLineObject implements Comparable<FileLineObject> {
	String id;
	String name;
	int age;

	public FileLineObject(String parseLine) {
		String[] lineDatas = parseLine.split(",");
		
		id = lineDatas[0];
		name = lineDatas[1];
		age = Integer.parseInt(lineDatas[2]);
	}
	
	public FileLineObject(String id, String name, int age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}


	@Override
	public int compareTo(FileLineObject var1) {
		// ���̵�� ���� ����
		return id.compareTo(var1.id);
	}
	
	@Override
	public String toString() {
		return "FileLineObject [id=" + id + ", name=" + name + ", age=" + age + "]";
	}
}


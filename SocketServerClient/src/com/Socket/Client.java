package com.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;



public class Client {
	public static void main(String[] args) {
		Socket socket = null;
		try {
			System.out.println( "[���� ��û]");
			
			 Scanner scanner = new Scanner(System.in);
			
			 System.out.print("port ��ȣ �Է� : ");
			 int port = scanner.nextInt();
			 scanner.nextLine();

			 System.out.print("ip �ּ� �Է� : ");
			 String ip = scanner.nextLine();

			 socket = new Socket(ip, port);
			 
//			System.out.println( "[���� ����]");
//			
			byte[] bytes = null;
//			String message = null;
			OutputStream os = socket.getOutputStream();
			System.out.print("message �Է� : ");
			String message = scanner.nextLine();
			
			bytes = message.getBytes("UTF-8");
			os.write(bytes);
			os.flush();
			
			System.out.println( "[������ ������ ����]");
			
			InputStream is = socket.getInputStream();
			bytes = new byte[100];
			int readByteCount = is.read(bytes);
			message = new String(bytes, 0, readByteCount, "UTF-8");
			
			os.close();
			is.close();
			scanner.close();
			
		} catch(Exception e) {}

		if(!socket.isClosed()) {
			try {
				socket.close();
			} catch (IOException e1) {
			}
		}
	}
}
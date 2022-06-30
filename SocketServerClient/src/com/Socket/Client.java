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
			System.out.println( "[연결 요청]");
			
			 Scanner scanner = new Scanner(System.in);
			
			 System.out.print("port 번호 입력 : ");
			 int port = scanner.nextInt();
			 scanner.nextLine();

			 System.out.print("ip 주소 입력 : ");
			 String ip = scanner.nextLine();

			 socket = new Socket(ip, port);
			 
//			System.out.println( "[연결 성공]");
//			
			byte[] bytes = null;
//			String message = null;
			OutputStream os = socket.getOutputStream();
			System.out.print("message 입력 : ");
			String message = scanner.nextLine();
			
			bytes = message.getBytes("UTF-8");
			os.write(bytes);
			os.flush();
			
			System.out.println( "[데이터 보내기 성공]");
			
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
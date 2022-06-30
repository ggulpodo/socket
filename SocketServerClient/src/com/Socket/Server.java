package com.Socket;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;



public class Server {
	public static void main(String[] args) {
		ServerSocket serverSocket = null;
		System.out.println( "[연결 기다림]");
		
		try {
			serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("127.0.0.1", 8888));
			Socket socket = null;
			socket = serverSocket.accept();

			InetSocketAddress isa = (InetSocketAddress)socket.getRemoteSocketAddress();
			System.out.println("[연결 수락함] " + isa.getHostName());
			
			byte[] bytes = null;
			bytes = new byte[100];
			String name =null;
			
			InputStream is = socket.getInputStream();
			int readidCount = is.read(bytes);
			name = new String(bytes, 0, readidCount, "UTF-8");
			
			while(true) {
				
				if(socket == null || socket.isConnected() == false)
				{
//					socket = serverSocket.accept();
				}
				bytes = null;
				
				String message = null;
				
//				InputStream is = socket.getInputStream();
				bytes = new byte[100];
				
				int readByteCount = is.read(bytes);
				message = new String(bytes, 0, readByteCount, "UTF-8");
				System.out.println(name+" : " + message);
//				String message = null;
				

				OutputStream os = socket.getOutputStream();
				bytes = message.getBytes("UTF-8");
				os.write(bytes);
				os.flush();
				

				
				if(socket.isConnected() == false)
				{
					is.close();
					os.close();
					break;
				}
			}
			
			if(socket != null)
			{
				
				socket.close();
			}
			
		} catch(Exception e) {
//			int a =0;
			System.out.println("[접속 종료함]");
		}

		if(!serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch (IOException e1) {
				}
		}
	}
}
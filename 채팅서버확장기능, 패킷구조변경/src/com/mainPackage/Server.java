package com.mainPackage;

import java.io.DataInputStream;
//import java.io.BufferedReader;
//import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
	// 버퍼 사이즈 설정
	private final static int BUFFER_SIZE = 1024;
	
	// 패킷 구조 변경
//	public static int byte2Int(byte[] src) {
//        int s1 = src[3] & 0xFF;
//        int s2 = src[2] & 0xFF;
//        int s3 = src[1] & 0xFF;
//        int s4 = src[0] & 0xFF;
//
//        return ((s4 << 24) + (s3 << 16) + (s2 << 8) + (s1 << 0));
//    }
	
	public static byte[] intTobyte(int value) {
        byte[] bytes=new byte[4];
        bytes[3]=(byte)((value&0xFF000000)>>24);
        bytes[2]=(byte)((value&0x00FF0000)>>16);
        bytes[1]=(byte)((value&0x0000FF00)>>8);
        bytes[0]=(byte) (value&0x000000FF);

        return bytes;
    }
	
	public static byte[] joinArrays(byte[]... arrays) {
        int len = 0;
        for (byte[] array : arrays) {
            len += array.length;//더하려는 배열의 총 길이
        }
        byte[] result = new byte[len];//(byte[]) Array.newInstance(byte.class, len);//new byte[len];
        int offset = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

	
	public static void main(String[] args) {
		
		try (ServerSocket server = new ServerSocket()) {
			InetSocketAddress ipep = new InetSocketAddress(9999);
			
			server.bind(ipep);
			ExecutorService receiver = Executors.newCachedThreadPool();
			
			// clients: 클라이언트 소켓을 저장하는 변수
			//List<Socket> clients = new ArrayList<>();
			ConcurrentHashMap<String, Socket> clientList = new ConcurrentHashMap<>();
			ConcurrentHashMap<Integer, ConcurrentHashMap<String, Socket>> groupList =new ConcurrentHashMap<>();// Int = 그룹 아이디 , Value = 그룹에 접속한클라이언트 리스트 = GroupclientList
			
			
			System.out.println("클라이언트 기다림");
			
			while (true) {
				try {
					// client가 들어올 때까지 기다리는 함수
					// 새롭게 들어온 클라이언트를 client라는 변수에 저장
					Socket client = server.accept();
					// client가 들어오면 해당 clientSocket을 clients에 저장
					//clients.add(client);
					
					System.out.println("클라이언트와 접속");
					System.out.println("Client connected IP address =" + client.getRemoteSocketAddress().toString());
					
					
					receiver.execute(() -> {
						try (Socket thisClient = client;
								OutputStream send = client.getOutputStream();
								DataInputStream recvpacket = new DataInputStream(thisClient.getInputStream());) {
							String msg = null;
							//DataInputStream recvByte = new DataInputStream(thisClient.getInputStream());
							byte[] b = new byte[BUFFER_SIZE];
							int count = recvpacket.read(b, 0, b.length);//id 읽어옴
							
							//String id = new String(b);
							String id = new String(b, 0, count);
							//group 설정
							clientList.put(id, thisClient);
							
							//%2해서 나머지 값으로 group에 추가 
							int roomID = clientList.size()%2;
							if(groupList.containsKey(roomID)) {
								groupList.get(roomID).put(id, client);
							}
							else {
								ConcurrentHashMap<String, Socket> GroupclientList = new ConcurrentHashMap<>();
								GroupclientList.put(id, thisClient);
								groupList.put(roomID, GroupclientList);
							}
							//System.out.print(roomID+" : "+groupList.get(roomID).keySet()+" , ");
							
							//StringBuffer sb = new StringBuffer();
							while (true) {
								b = new byte[BUFFER_SIZE];
								/*데이터를 클라이언트로부터 읽는 코드 블록*/
								// 클라이언트에서 stream에 작성한 데이터를 읽어드리는 함수
								// 읽어드린 데이터는 b라는 byte array에 저장
//								recv.read(b, 0, b.length);
								
								int headsize = recvpacket.readInt();
								System.out.print(headsize);
								byte[] message = new byte[headsize];
								if(headsize>0) {
									recvpacket.readFully(message, 0 ,message.length);
									String msg2 = new String(message);
									System.out.print(msg2);
								}
//								for(int i = 0 ; i < message.length;i++) {
	//								System.out.print(message[i]+" ");
		//						}
//								recv.read(b, 0, 4);
//								byte[] head = b;
//								 = byte2Int(head);
//								recv.read(b,0,headsize);
								//설명
								//b = b.length만큼 데이터를 받아서 저장된 바이트 배열
								//b= [ 0 0 0 2 ] [ 73 74 ]
								//head = 0 0 0 2
								//msg = 73 74
								
								
								//메세지 read 방법1
//								String str = "";
//					            for (int index = 0; index < cnt; index++) {
//					                str += b[index];
//					            }
								//메세지 read 방법2
//								InputStreamReader sr = new InputStreamReader(recv);
//								BufferedReader br= new BufferedReader(sr);
//								String str = br.readLine();
								
					            
//					            String msg2 = new String(b, 0, cnt);
					            msg = new String(message);
					            msg = id+" : "+ msg;
					            b= msg.getBytes(StandardCharsets.UTF_8);
					            byte[] header = new byte[4];
					            header = intTobyte(msg.length());
					            byte[] sendpacket = new byte[header.length+ b.length];
					            sendpacket = joinArrays(header, b);
								//msg = new String(b);
					            
								/*데이터를 클라이언트들에게 뿌려주는 코드 블록*/
								// write message to the clients
								for(Socket c: groupList.get(roomID).values()) {
									// 클라이언트소켓이 꺼져 있는 경우가 있기 때문에 if문으로 확인
						    		if(! c.isClosed()) {
						    			// write함수를 사용하여 클라이언트에게 메세지 전송
						    			DataOutputStream temp = new DataOutputStream(c.getOutputStream());
						        		temp.write(sendpacket);
						        		temp.flush();
						        	}
						    	}
								//System.out.println(id+":"+msg);
								//System.out.println(msg);
							}
						} catch (SocketException e) {
							System.out.println("클라이언트 접속 종료");
						} catch (Throwable e) {
							e.printStackTrace();
						} 
						//finally {
							//System.out.println("Client disconnected IP address ="
								//	+ client.getRemoteSocketAddress().toString());
						//}
					});
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
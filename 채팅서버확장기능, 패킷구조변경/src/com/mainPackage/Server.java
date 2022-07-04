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
	// ���� ������ ����
	private final static int BUFFER_SIZE = 1024;
	
	// ��Ŷ ���� ����
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
            len += array.length;//���Ϸ��� �迭�� �� ����
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
			
			// clients: Ŭ���̾�Ʈ ������ �����ϴ� ����
			//List<Socket> clients = new ArrayList<>();
			ConcurrentHashMap<String, Socket> clientList = new ConcurrentHashMap<>();
			ConcurrentHashMap<Integer, ConcurrentHashMap<String, Socket>> groupList =new ConcurrentHashMap<>();// Int = �׷� ���̵� , Value = �׷쿡 ������Ŭ���̾�Ʈ ����Ʈ = GroupclientList
			
			
			System.out.println("Ŭ���̾�Ʈ ��ٸ�");
			
			while (true) {
				try {
					// client�� ���� ������ ��ٸ��� �Լ�
					// ���Ӱ� ���� Ŭ���̾�Ʈ�� client��� ������ ����
					Socket client = server.accept();
					// client�� ������ �ش� clientSocket�� clients�� ����
					//clients.add(client);
					
					System.out.println("Ŭ���̾�Ʈ�� ����");
					System.out.println("Client connected IP address =" + client.getRemoteSocketAddress().toString());
					
					
					receiver.execute(() -> {
						try (Socket thisClient = client;
								OutputStream send = client.getOutputStream();
								DataInputStream recvpacket = new DataInputStream(thisClient.getInputStream());) {
							String msg = null;
							//DataInputStream recvByte = new DataInputStream(thisClient.getInputStream());
							byte[] b = new byte[BUFFER_SIZE];
							int count = recvpacket.read(b, 0, b.length);//id �о��
							
							//String id = new String(b);
							String id = new String(b, 0, count);
							//group ����
							clientList.put(id, thisClient);
							
							//%2�ؼ� ������ ������ group�� �߰� 
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
								/*�����͸� Ŭ���̾�Ʈ�κ��� �д� �ڵ� ���*/
								// Ŭ���̾�Ʈ���� stream�� �ۼ��� �����͸� �о�帮�� �Լ�
								// �о�帰 �����ʹ� b��� byte array�� ����
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
								//����
								//b = b.length��ŭ �����͸� �޾Ƽ� ����� ����Ʈ �迭
								//b= [ 0 0 0 2 ] [ 73 74 ]
								//head = 0 0 0 2
								//msg = 73 74
								
								
								//�޼��� read ���1
//								String str = "";
//					            for (int index = 0; index < cnt; index++) {
//					                str += b[index];
//					            }
								//�޼��� read ���2
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
					            
								/*�����͸� Ŭ���̾�Ʈ�鿡�� �ѷ��ִ� �ڵ� ���*/
								// write message to the clients
								for(Socket c: groupList.get(roomID).values()) {
									// Ŭ���̾�Ʈ������ ���� �ִ� ��찡 �ֱ� ������ if������ Ȯ��
						    		if(! c.isClosed()) {
						    			// write�Լ��� ����Ͽ� Ŭ���̾�Ʈ���� �޼��� ����
						    			DataOutputStream temp = new DataOutputStream(c.getOutputStream());
						        		temp.write(sendpacket);
						        		temp.flush();
						        	}
						    	}
								//System.out.println(id+":"+msg);
								//System.out.println(msg);
							}
						} catch (SocketException e) {
							System.out.println("Ŭ���̾�Ʈ ���� ����");
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
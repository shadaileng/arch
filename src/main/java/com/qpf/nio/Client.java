package com.qpf.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) throws Exception {
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		SocketChannel socketChannel = SocketChannel.open();
		
		socketChannel.connect(new InetSocketAddress("127.0.0.1", 9999));
		
		@SuppressWarnings("resource")
		Scanner s = new Scanner(System.in);
		
		while (true) {
			buf.clear();
			String message = s.nextLine();
			buf.put(message .getBytes("UTF-8"));
			if ("exit".equals(message)) break;
			buf.flip();
			socketChannel.write(buf);
		
			buf.clear();
			int len = socketChannel.read(buf);
			if (len == -1) break;
			buf.flip();
			byte[] data = new byte[buf.remaining()];
			buf.get(data);
			System.out.println("Server response: " + new String(data, "UTF-8"));
		}
	}
}

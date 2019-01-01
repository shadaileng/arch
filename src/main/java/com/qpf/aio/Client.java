package com.qpf.aio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		AsynchronousSocketChannel socketChannel;
		try {
			socketChannel = AsynchronousSocketChannel.open();
			socketChannel.connect(new InetSocketAddress("127.0.0.1", 9999));
			

			@SuppressWarnings("resource")
			Scanner s = new Scanner(System.in);
			ByteBuffer buf = ByteBuffer.allocate(1024);
			buf.clear();
			String message = s.nextLine();
			buf.put(message .getBytes("UTF-8"));
			buf.flip();
			socketChannel.write(buf);
			
			buf.clear();
			socketChannel.read(buf).get();
			buf.flip();
			byte[] data = new byte[buf.remaining()];
			buf.get(data);
			System.out.println("Server response: " + new String(data, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

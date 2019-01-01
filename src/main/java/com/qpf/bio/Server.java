package com.qpf.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

	public static void main(String[] args) {
		int port = 9999;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		}
		
		ServerSocket serverSocket = null;
		ExecutorService service = Executors.newFixedThreadPool(10);
		
		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Server is Listening....");
			while (true) {
				Socket socket = serverSocket.accept();
				service.execute(new Handler(socket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	static class Handler implements Runnable {
		private Socket socket = null;
		public Handler(Socket socket) {
			this.socket = socket;
		}
		@Override
		public void run() {
			BufferedReader reader = null;
			PrintWriter writer = null;
			
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				writer = new PrintWriter(socket.getOutputStream(), true);
				while (true) {
					String line = reader.readLine();
					if (line == null) {
						break;
					}
					
					System.out.println("Server recv: " + line);
					writer.println("server send: " + line);
					writer.flush();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

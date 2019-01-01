package com.qpf.bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		Socket socket = new Socket("127.0.0.1", 9999);
		System.out.println("Client started");
		BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
		Scanner s = new Scanner(System.in);
		while (true) {
			String message = s.nextLine();
			if ("exit".equals(message)) {
				break;
			}
			System.out.println("Client: " + message);
			writer.println(message);
			writer.flush();
			String line = reader.readLine();
			if (line == null) {
				break;
			}
			
			System.out.println("Server send: " + line);
		}
		if(s != null) s.close();
		if (reader != null) reader.close();
		if (writer != null) writer.close();
	}
}

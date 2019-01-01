package com.qpf.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {
	private Selector selector;
	private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
	private ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
	
	public Server(int port) {
		System.out.println("server start in " + port);
		try {
			// 开启多路复用器
			this.selector = Selector.open();
			// 开启服务通道
			ServerSocketChannel channel = ServerSocketChannel.open();
			// 设置非阻塞
			channel.configureBlocking(false);
			// 绑定端口
			channel.bind(new InetSocketAddress(port));
			// 注册服务通道状态
			/**
			 * OP_ACCEPT: 可连接标记位
			 * OP_READ: 可读取标记位
			 * OP_WRITE: 可写入标记位
			 * OP_CONNECT: 连接后标记位
			 */
			channel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("Server waiting...");
		} catch (Exception e) {
			e.printStackTrace();
		}
		run();
	}
	
	public void run() {
		while (true) {
			try {
				// 阻塞方法,等待通道被选中
				this.selector.select();
				// 获取选中通道标记集合
				Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
				while(keys.hasNext()) {
					SelectionKey key = keys.next();
					// 移除当前标志位,
					keys.remove();
					// 检查通道是否有效
					if(key.isValid()) {
						try {
							if (key.isAcceptable()) {
								accept(key);
							}
							if (key.isReadable()) {
								read(key);
							}
							if (key.isWritable()) {
								write(key);
							}
						} catch (Exception e) {
							// 出现异常,关闭通道
							key.cancel();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void write(SelectionKey key) {
		try {
			// 清空读缓存
			writeBuffer.clear();
			// 获取通道
			SocketChannel channel = (SocketChannel) key.channel();
			System.out.println("Server response");
			writeBuffer.put("server recved.".getBytes("UTF-8"));
			writeBuffer.flip();
			// 从可写缓存写入数据到通道
			channel.write(writeBuffer);
			// 注册通道可读标志位
			channel.register(selector, SelectionKey.OP_READ);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void read(SelectionKey key) {
		try {
			// 清空读缓存
			readBuffer.clear();
			// 获取通道
			SocketChannel channel = (SocketChannel) key.channel();
			// 读取通道数据到读缓存
			int read = channel.read(readBuffer);
			// 判断是否获取数据
			if (read == -1) {
				// 关闭通道
				channel.close();
				// 关闭连接
				key.cancel();
				return;
			}
			// 重置缓存游标, 标记有效位
			// lim = pos; pos = 0;
			readBuffer.flip();
			// 构建存储实际数据的字节数组
			byte data[] = new byte[readBuffer.remaining()];
			// 从缓存中读取数据
			readBuffer.get(data);
			// 打印
			System.out.println("from " + channel.getRemoteAddress() + " recv: " + new String(data, "UTF-8"));
			// 注册通道可写标志位
			channel.register(selector, SelectionKey.OP_WRITE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void accept(SelectionKey key) {
		try {
			// 根据标记位获取服务通道
			ServerSocketChannel serverChannel = (ServerSocketChannel)key.channel();
			// 阻塞方法,等待客户端连接
			SocketChannel socketChannel = serverChannel.accept();
			// 设置通道非阻塞
			socketChannel.configureBlocking(false);
			// 注册通道,可读
			socketChannel.register(this.selector, SelectionKey.OP_READ);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		int port = 9999;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		}
		new Server(port);
//		new Thread(new Server(port)).start();
	}
}

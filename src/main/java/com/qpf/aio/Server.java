package com.qpf.aio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.TimeUnit;

public class Server {
	private AsynchronousServerSocketChannel socketChannel;
	public Server(int port) {
		try {
			socketChannel = AsynchronousServerSocketChannel.open();
			socketChannel.bind(new InetSocketAddress(port));
			/**
			 * accept(Server attachment, CompletionHandler<AsynchronousSocketChannel, ? super Server> handler
			 * attachment: 赋值给第二个范型
			 * handler: 第一个范型是与客户端的通道
			 */
			socketChannel.accept(this, new CompletionHandler<AsynchronousSocketChannel, Server>() {
				/**
				 * result: 与客户端的通道
				 * attachment: 资源类,获取服务通道
				 */
				@Override
				public void completed(AsynchronousSocketChannel result, Server attachment) {
					attachment.getSocketChannel().accept(attachment, this);
					doRead(result);
				}

				private void doRead(AsynchronousSocketChannel channel) {
					ByteBuffer buf = ByteBuffer.allocate(1024);
					
					/**
					 * destination: 处理客户端传递数据的中转缓存, 可以不使用
					 * attachment: 处理客户端传递数据对象,处理完成的结果赋值给处理逻辑的第二个范型对象
					 * handler - 处理逻辑, 第一个范型是数据的长度,第二个范型是处理的数据对象
					 */
					channel.read(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {
						@Override
						public void completed(Integer result, ByteBuffer attachment) {
							try {
								// 游标复位
								attachment.flip();
								System.out.println("from Client: " + new String(attachment.array(), "UTF-8"));
								doWrite(channel);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						@Override
						public void failed(Throwable exc, ByteBuffer attachment) {
							exc.printStackTrace();
						}
					});
				}
				
				@Override
				public void failed(Throwable exc, Server attachment) {
					exc.printStackTrace();
				}

				public void doWrite(AsynchronousSocketChannel channel) {
					ByteBuffer buf = ByteBuffer.allocate(1024);
					try {
						buf.put("Server response".getBytes("UTF-8"));
						// 重置游标
						buf.flip();
						channel.write(buf);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			System.out.println("server start in " + port);
			TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AsynchronousServerSocketChannel getSocketChannel() {
		return socketChannel;
	}

	public static void main(String[] args) {
		int port = 9999;
		if (args.length > 0) {
			port = Integer.parseInt(args[0]);
		}
		new Server(port);
	}
}

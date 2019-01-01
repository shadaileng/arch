package com.qpf.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 创建一个容器,提供添加元素和查看容大小的方法
 * 启动两个线程,
 * 第一个向容器中添加10个元素,
 * 第二个线程监听容器大小等于5时输出信息然后结束.<br>
 * wait() --> notifyAll()
 * @author shadaileng
 *
 */
public class TestConcurrent02 {
	public static void main(String[] args) {
		System.out.println(111);
		final Contatiner_02 contatiner = new Contatiner_02();
		final Object lock = new Object();
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (lock) {
					for(int i = 0; i < 10; i++) {
						System.out.println("m1 put: " + i);
						contatiner.put(i + "");
						if (contatiner.size() == 5) {
							lock.notifyAll();
							try {
								lock.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						try {
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				synchronized (lock) {
					if(contatiner.size() < 5) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					System.out.println("size == 5");
					lock.notifyAll();
				}
			}
		}).start();
	}

}
class Contatiner_02 {
	List<Object> list = new ArrayList<>();
	public int size( ) {
		return list.size();
	}
	public void put(String el) {
		list.add(el);
	}
}

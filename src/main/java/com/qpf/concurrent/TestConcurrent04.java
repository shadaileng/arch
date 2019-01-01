package com.qpf.concurrent;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;


/**
 * 生产者|消费者<br>
 * 自定义同步容器,容量上限为10.
 * 可在多线程中使用,保证线程安全.<br>
 * wait() --> notifyAll()
 * @author shadaileng
 *
 */
public class TestConcurrent04<E> {
	private final LinkedList<E> list = new LinkedList<>();
	private final int MAX = 10;
	private int count = 0;
	public synchronized int getCount() {
		return count;
	}
	public synchronized void put(E e) {
		while(list.size() == MAX) {
			try {
				this.wait();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		list.add(e);
		count++;
		this.notifyAll();
		System.out.println(Thread.currentThread().getName() + " put Value: " + e);
	}
	
	public synchronized E get() {
		E e = null;
		while(list.size() == 0) {
			try {
				this.wait();
				System.out.println("wait get");
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		e = list.removeFirst();
		count--;
		this.notifyAll();
		return e;
	}
	public static void main(String[] args) {
		TestConcurrent04<Object> concurrent04 = new TestConcurrent04<>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i = 0; i < 15; i++) {
					Object object = concurrent04.get();
					System.out.println(Thread.currentThread().getName() + " get Value: " + object);
				}
				System.out.println(Thread.currentThread().getName() + " get end: " + concurrent04.getCount());
			}
		}, "comsumer_0").start();
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(int j = 0; j < 3; j++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for(int i = 0; i < 5; i++) {
						concurrent04.put("current value: " + i);
					}
					System.out.println(Thread.currentThread().getName() + " put end: " + concurrent04.getCount());
				}
			}, "produce_" + j).start();
		}
	}
}

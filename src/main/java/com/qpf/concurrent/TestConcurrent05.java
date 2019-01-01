package com.qpf.concurrent;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 生产者|消费者<br>
 * 自定义同步容器,容量上限为10.
 * 可在多线程中使用,保证线程安全.<br>
 * ReentrantLock
 * @author shadaileng
 *
 */
public class TestConcurrent05<E> {
	private final LinkedList<E> list = new LinkedList<>();
	private final int MAX = 10;
	private int count = 0;
	private Lock lock = new ReentrantLock();
	private Condition produces = lock.newCondition();
	private Condition consumer = lock.newCondition();
	public synchronized int getCount() {
		return count;
	}
	public void put(E e) {
		try {
			lock.lock();
			while(list.size() == MAX) {
				try {
					System.out.println(Thread.currentThread().getName() + " 等待...");
					produces.await();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			list.add(e);
			count++;
			consumer.signalAll();
			System.out.println(Thread.currentThread().getName() + " 唤醒...");
			System.out.println(Thread.currentThread().getName() + " put Value: " + e);
		}catch (Exception e1) {
		}finally {
			lock.unlock();
		}
	}
	
	public synchronized E get() {
		E e = null;
		try {
			lock.lock();
			while(list.size() == 0) {
				try {
					System.out.println(Thread.currentThread().getName() + " 等待...");
					consumer.await();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
			e = list.removeFirst();
			count--;
			produces.signalAll();
			System.out.println(Thread.currentThread().getName() + " 唤醒...");
		}catch (Exception e1) {
		}finally {
			lock.unlock();
		}
		return e;
	}
	public static void main(String[] args) {
		TestConcurrent05<Object> concurrent04 = new TestConcurrent05<>();
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

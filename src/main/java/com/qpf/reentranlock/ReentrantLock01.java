package com.qpf.reentranlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLock01 {
	Lock lock = new ReentrantLock();
	void m1() {
		try {
			lock.lock();
			for (int i = 0; i < 10; i++) {
				System.out.println("m1 method: " + i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			lock.unlock();
		}
	}
	void m2() {
		lock.lock();
		System.out.println("m2 method");
		lock.unlock();
	}
	public static void main(String[] args) {
		ReentrantLock01 t = new ReentrantLock01();
		new Thread(new Runnable() {
			@Override
			public void run() {
				t.m2();
			}
		}).start();
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				t.m1();
			}
		}).start();
	}
}

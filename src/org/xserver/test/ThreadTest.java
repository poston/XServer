package org.xserver.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class ThreadTest {
	private static int i = 0;
	private int j = 0;

	public static void main(String[] args) throws IOException {
		final ThreadTest tt = new ThreadTest();
		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					ThreadTest.i++;
					tt.j++;
					System.out.println("thread 1's j is " + ThreadTest.i);
					System.out.println("thread 1's i is " + ThreadTest.i);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("thread 1's i is " + ThreadTest.i);
					System.out.println("thread 1's j is " + ThreadTest.i);
				}
			}
		});

		Thread t2 = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					ThreadTest.i++;
					tt.j++;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("thread 2's i is " + ThreadTest.i);
					System.out.println("thread 1's j is " + ThreadTest.i);
				}
			}
		});

		t1.start();
		t2.start();
	}
}

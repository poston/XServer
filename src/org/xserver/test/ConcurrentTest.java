package org.xserver.test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentTest {

	public void t() throws Exception {
		URL url = new URL("http://localhost:8080/test/comet");
		int rspcode = 0;
		String res = "";
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setConnectTimeout(1000 * 10);
		conn.setReadTimeout(1000 * 10);
		rspcode = conn.getResponseCode();

		System.out.println(rspcode);
		if (rspcode == 200) {
			BufferedInputStream read = new BufferedInputStream(
					conn.getInputStream());
			byte[] b = new byte[1024 * 100];
			int len = 0;
			while ((len = read.read(b)) > 0) {
				res += new String(b, 0, len, "UTF-8");
			}

			System.out.println(res);
		}
	}

	public static void main(String[] args) throws IOException {
		final AtomicInteger coutn = new AtomicInteger();
		final ConcurrentTest test = new ConcurrentTest();
		for (int i = 0; i < 4000; i++) {
			Thread tt = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						test.t();
						System.err.println(coutn.incrementAndGet());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			tt.start();
		}
	}

}

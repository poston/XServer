package org.xserver.test;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xserver.component.config.XServerHttpConfig;
import org.xserver.component.core.InterfaceContext;
import org.xserver.component.core.XServerHttpFactory;
import org.xserver.component.core.XServerHttpRequest;
import org.xserver.component.core.XServerHttpResponse;
import org.xserver.component.json.JsonManager;
import org.xserver.wrap.HttpInterface;

@Component
public class Test implements HttpInterface {

	public static final Logger logger = LoggerFactory.getLogger(Test.class);
	@Resource
	private XServerHttpFactory factory;
	@Resource
	private InterfaceContext interfaceContext;
	@Resource
	private XServerHttpConfig xServerHttpConfig;

	// @Resource
	// private TaskQuery ts;
	// @Resource
	// private InterfaceContext interfaceContext;
	//
	// public static void main(String[] args) {
	// ApplicationContext ctx = new ClassPathXmlApplicationContext(
	// "applicationContext.xml");
	// // Map<String, HttpInterface> map1 = ctx
	// // .getBeansOfType(HttpInterface.class);
	// // for (Entry entry : map1.entrySet()) {
	// // System.out.println(entry.getKey() + " : " + entry.getValue());
	// // }
	// // Map<String, Object> map = ctx.getBeansWithAnnotation(Cmd.class);
	// // for (Entry entry : map.entrySet()) {
	// // System.out.println(entry.getKey() + " : " + entry.getValue());
	// // }
	// // TaskQuery tq = (TaskQuery) ctx.getBean("taskQuery");
	// // tq.query(null, null);
	// // System.out.println(ctx.getApplicationName());
	// // Test t = (Test) ctx.getBean("test");
	// // t.ts.query(null, null);
	// Test t = (Test) ctx.getBean("test");
	// t.interfaceContext.loadInterfaceContext();
	// QueryStringDecoder q = new QueryStringDecoder(
	// "http://xserver.qq.com/class/method?ww=12&mkj=234");
	// System.out.println(q.getPath());
	// System.out.println(q.getParameters());
	// }

	// static int i = 0;
	//
	// public static void changeI() {
	// System.out.println(i);
	// i = 2;
	// System.out.println(i);
	// }
	//
	// public static void main(String[] args) throws NoSuchMethodException,
	// SecurityException {
	// ApplicationContext ctx = new ClassPathXmlApplicationContext(
	// "applicationContext.xml");
	// Test t = (Test) ctx.getBean("test");
	//
	// System.out.println(t.i);
	// t.changeI();
	//
	// Test t1 = (Test) ctx.getBean("test");
	//
	// System.out.println(t.i);
	// }
	/**
	 * @param args
	 * @throws UnsupportedEncodingException
	 */
	int count;

	public Object memoryMonitor(XServerHttpRequest request,
			XServerHttpResponse response) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		long totalMem = runtime.totalMemory() / 1024;
		long maxMem = runtime.maxMemory() / 1024;
		long freeMem = runtime.freeMemory() / 1024;
		long usedMem = totalMem - freeMem;

		return JsonManager.mapper(new Object[] { "totalMem", totalMem,
				"maxMem", maxMem, "freeMem", freeMem, "usedMem", usedMem });
	}

	public Object comet(XServerHttpRequest request, XServerHttpResponse response)
			throws InterruptedException {
		Random random = new Random();
		int time = random.nextInt(1000);
		Thread.sleep(time);
		return "{rtn:0}";
	}

	public Object test(XServerHttpRequest request, XServerHttpResponse response) {
		String uin = request.getParameterByPost("uid");
		return uin;
	}

}

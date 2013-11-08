package org.xserver.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.jboss.netty.handler.codec.http.multipart.FileUpload;
import org.springframework.stereotype.Component;
import org.xserver.component.annotation.Config;
import org.xserver.component.core.InterfaceContext;
import org.xserver.component.core.XServerHttpRequest;
import org.xserver.component.core.XServerHttpResponse;
import org.xserver.component.hbase.HBaseTemplate;
import org.xserver.component.jdbc.JdbcTemplate;
import org.xserver.component.json.JsonManager;
import org.xserver.component.spring.DynamicLoadBean;
import org.xserver.component.spring.SpringUtil;
import org.xserver.wrap.HttpInterface;

import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class TaskQuery implements HttpInterface {
	@Resource
	private InterfaceContext interfaceContext;
	@Resource
	private DynamicLoadBean dynamicLoadBean;
	@Config
	int testQuartz = 0;
	@Resource(name = "hbaseTemplateVoc")
	private HBaseTemplate hbaseTemplateVoc;

	Object lock = new Object();

	private static ExecutorService es = Executors.newFixedThreadPool(1);

	@Resource(name = "jdbcTemplateMonet")
	private JdbcTemplate jdbcTemplate;

	// @Comet
	public Object query(XServerHttpRequest request, XServerHttpResponse response)
			throws InterruptedException {
		int a = request.getParameterInteger("a");
		String b = request.getParameter("b");
		System.out.println("PARAM: " + "a: " + a + ", b: " + b);
		// TODO
		return "{rtn:0, data:123}";
	}

	public Object post(XServerHttpRequest request, XServerHttpResponse response)
			throws InterruptedException {
		// 1.contentType
		// 2.business works
		// 3.cvs->FileWriter
		// 4.File Out
		// 5.delete
		// response.setContentTypeHeader(XServerHttpResponse.APPLICATION_MSEXCEL);
		// response.setWriteType(WriteType.NULL);
		String uid = request.getParameterByPost("uid");
		String u = request.getParameterByPost("u", "");

		List<FileUpload> fileUploads = request.getFileUpload();
		System.out.println(fileUploads.get(0).getFilename());
		System.out.println(fileUploads.get(0).getName());
		// TODO
		return "{uid:" + uid + ", u:" + u + "}";
	}

	public Object error(XServerHttpRequest request, XServerHttpResponse response)
			throws Exception {
		throw new Exception("Test Email Error");
	}

	public Object hotDeploy(XServerHttpRequest request,
			XServerHttpResponse response) {
		interfaceContext.loadInterfaceContext();
		return null;
	}

	// public Object kkk(XServerHttpRequest request, XServerHttpResponse
	// response) {
	// XServerClassLoader xServerClassLoader = new XServerClassLoader(
	// "XServer/src");
	//
	// try {
	// Class<?> c = xServerClassLoader
	// .loadClass("org.xserver.bootstrap.BootstrapManager");
	// Method m = null;
	// try {
	// m = c.getMethod("invoke", null);
	// m.invoke(null, null);
	// } catch (Exception e) {
	// System.out.println("No invoke function.");
	// }
	// } catch (ClassNotFoundException e) {
	// e.printStackTrace();
	// }
	//
	// return "";
	// }

	public Object dynamicLoad(XServerHttpRequest request,
			XServerHttpResponse response) {
		dynamicLoadBean.loadBean("dynamicApplicationContext.xml");
		Object bean = SpringUtil.getBean("dynamicTest");
		System.out.println(bean.getClass().getName());
		return "";
	}

	public Object monetdb(XServerHttpRequest request,
			XServerHttpResponse response) {
		return jdbcTemplate.queryForInt("SELECT id FROM test_monetdb LIMIT 1");
	}

	// @Comet
	public Object threadInner(XServerHttpRequest request,
			XServerHttpResponse response) throws JsonProcessingException {

		try {
			System.out.println("in");
			Thread.sleep(5000);
			System.out.println("out");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return JsonManager.mapper(new Object[] { "cmd", "123", "msg", "ok" });
	}

	public Object memoryMonitor(XServerHttpRequest request,
			XServerHttpResponse response) throws Exception {
		Runtime runtime = Runtime.getRuntime();
		long totalMem = runtime.totalMemory() / 1024 / 1024;
		long maxMem = runtime.maxMemory() / 1024 / 1024;
		long freeMem = runtime.freeMemory() / 1024 / 1024;
		long usedMem = totalMem - freeMem;

		return JsonManager.mapper(new Object[] { "totalMem", totalMem + "M",
				"maxMem", maxMem + "M", "freeMem", freeMem + "M", "usedMem",
				usedMem + "M" });
	}

	public void quartzTest() throws InterruptedException {
		System.out.println(Thread.currentThread().getName() + ": testQuartz: "
				+ testQuartz);
	}

	public Object testHBase(XServerHttpRequest request,
			XServerHttpResponse response) throws JsonProcessingException {
		// TestHBase t = hbaseTemplateVoc.getBean("row-1", TestHBase.class);
		System.out.println(hbaseTemplateVoc.getScanCaching());
		TestHBase t1 = new TestHBase();
		t1.setQ1("value-0902");
		hbaseTemplateVoc.put("row-0902", t1);
		return JsonManager.mapper(new Object[] { "rtn", 0, "obj", "1" });
	}

	public Object testImpl(XServerHttpRequest request,
			XServerHttpResponse response) throws ClassNotFoundException {

		Connection con = null;
		try {
			Class.forName("org.apache.hive.jdbc.HiveDriver");
			String strLinkUrl = "jdbc:monetdb://10.198.144.104:50000/voc";
			con = DriverManager.getConnection(strLinkUrl, "poston", "poston");

			String sql = "select A.* from t_imc_detail_1000w A order by A.OP_DATE limit 10";
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				System.out.print(rs.getString(1));
				System.out.print(",");
				System.out.println(rs.getString(2));
			}

			rs.close();
			st.close();
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
}

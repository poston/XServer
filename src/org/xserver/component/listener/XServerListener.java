package org.xserver.component.listener;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xserver.common.util.StringUtil;
import org.xserver.component.concurrent.XServerThreadFactory;
import org.xserver.component.config.XServerHttpConfig;
import org.xserver.component.jdbc.JdbcTemplate;
import org.xserver.component.mail.MailTemplate;
import org.xserver.component.spring.SpringUtil;

@Component
public class XServerListener {
	private static final Logger logger = LoggerFactory
			.getLogger(XServerListener.class);
	private static final String SEND_ALARM_MSG = XServerListener.class
			.getClassLoader().getResource("").getPath()
			+ "../sh/sendAlarmMsg";

	@Resource
	private XServerHttpConfig xServerHttpConfig;

	// invokeListener record every interface invoke times
	private final Map<String, AtomicInteger> invokeListener = new ConcurrentHashMap<String, AtomicInteger>();
	// errorListener record interface occur error times
	private final Map<String, AtomicInteger> errorListener = new ConcurrentHashMap<String, AtomicInteger>();

	private static final OperatingSystemMXBean OS_MX_BEAN = ManagementFactory
			.getOperatingSystemMXBean();
	private static final Collection<GarbageCollectorMXBean> GC_MX_BEAN = ManagementFactory
			.getGarbageCollectorMXBeans();
	private static final MemoryMXBean MEMORY_MX_BEAN = ManagementFactory
			.getMemoryMXBean();
	private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors
			.newSingleThreadScheduledExecutor(new XServerThreadFactory(
					"Monitor", true));

	static {
		// SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(new Runnable() {
		//
		// @Override
		// public void run() {
		// getXServerListener().recordXServerMemory();
		// }
		// }, 5, 10, TimeUnit.SECONDS);
	}

	public void sendMessage(String message) {
		Runtime runtime = Runtime.getRuntime();
		String[] users = smsUsers(xServerHttpConfig.getSmsUsers());
		try {
			if (StringUtil.isEmpty(users)) {
				logger.warn("No setting sms users.");
				return;
			}
			int length = users.length + 2;
			String[] cmdArray = new String[length];
			System.arraycopy(users, 0, cmdArray, 1, users.length);
			cmdArray[0] = SEND_ALARM_MSG;
			cmdArray[length - 1] = message;

			runtime.exec(cmdArray);
			logger.info("send sms message [{}] to [{}]", cmdArray[length - 1],
					cmdArray[1] + "...");
		} catch (IOException e) {
			logger.error("Send sms message [" + message + "] to [" + users[0]
					+ "..." + "] error", e);
		}
	}

	/**
	 * Returns the operating system architecture. This method is equivalent to
	 * System.getProperty("os.arch").
	 * 
	 * @return the operating system architecture.
	 */
	public String getArch() {
		return OS_MX_BEAN.getArch();
	}

	/**
	 * Returns the number of processors available to the Java virtual machine.
	 * This method is equivalent to the Runtime.availableProcessors() method.
	 * 
	 * This value may change during a particular invocation of the virtual
	 * machine.
	 * 
	 * @return the number of processors available to the virtual machine; never
	 *         smaller than one.
	 */
	public int getAvailableProcessors() {
		return OS_MX_BEAN.getAvailableProcessors();
	}

	/**
	 * Returns the operating system version. This method is equivalent to
	 * System.getProperty("os.version").
	 * 
	 * @return the operating system version.
	 */
	public String getVersion() {
		return OS_MX_BEAN.getVersion();
	}

	/**
	 * Returns the operating system name
	 * 
	 * @return the operating system name.
	 */
	public String getOSName() {
		return System.getProperty("os.name");
	}

	/**
	 * Returns the approximate accumulated collection elapsed time in
	 * milliseconds. This method returns -1 if the collection elapsed time is
	 * undefined for this collector.
	 * 
	 * The Java virtual machine implementation may use a high resolution timer
	 * to measure the elapsed time. This method may return the same value even
	 * if the collection count has been incremented if the collection elapsed
	 * time is very short.
	 * 
	 * 
	 * @return the approximate accumulated collection elapsed time in
	 *         milliseconds.
	 */
	public long totalGCTime() {
		long total = 0L;
		for (GarbageCollectorMXBean bean : GC_MX_BEAN) {
			total += bean.getCollectionTime();
		}
		return total;
	}

	/**
	 * Returns the total number of collections that have occurred. This method
	 * returns -1 if the collection count is undefined for this collector.
	 * 
	 * @return the total number of collections that have occurred.
	 */
	public long totalGCCount() {
		long total = 0L;
		for (GarbageCollectorMXBean bean : GC_MX_BEAN) {
			total += bean.getCollectionCount();
		}
		return total;
	}

	public long getUsedHeapMemory() {
		MemoryUsage memoryUsage = MEMORY_MX_BEAN.getHeapMemoryUsage();
		return memoryUsage.getUsed();
	}

	public long getUsedNonHeapMemory() {
		MemoryUsage memoryUsage = MEMORY_MX_BEAN.getNonHeapMemoryUsage();
		return memoryUsage.getUsed();
	}

	private String[] smsUsers(String smsUsers) {
		String[] users = smsUsers.split(",");
		return users;
	}

	private static JdbcTemplate getJdbcTemplateXServer() {
		return (JdbcTemplate) SpringUtil.getBean("jdbcTemplateXServer");
	}

	private static XServerListener getXServerListener() {
		return (XServerListener) SpringUtil.getBean(XServerListener.class);
	}

	public Map<String, AtomicInteger> getInvokeListener() {
		return invokeListener;
	}

	public Map<String, AtomicInteger> getErrorListener() {
		return errorListener;
	}

	public void putInterface(String key) {
		invokeListener.put(key, new AtomicInteger());
		errorListener.put(key, new AtomicInteger());
	}

	public void incInvoke(String key) {
		invokeListener.get(key).incrementAndGet();
	}

	public void incError(String key) {
		errorListener.get(key).incrementAndGet();
	}

	public void resetListener() {
		for (AtomicInteger value : invokeListener.values()) {
			value.set(0);
		}
		for (AtomicInteger value : errorListener.values()) {
			value.set(0);
		}
	}

	private void recordXServerMemory() {
		Runtime runtime = Runtime.getRuntime();
		long totalMem = runtime.totalMemory() / 1024 / 1024;
		long maxMem = runtime.maxMemory() / 1024 / 1024;
		long freeMem = runtime.freeMemory() / 1024 / 1024;
		long usedMem = totalMem - freeMem;

		System.out.println(totalMem + "\t" + maxMem + "\t" + freeMem + "\t"
				+ usedMem + "\t" + getArch() + "\t" + totalGCTime() + "\t"
				+ totalGCCount() + "\t" + getUsedHeapMemory() + "\t"
				+ getUsedNonHeapMemory());
		String insertSQL =

		"INSERT INTO xserver(time, totalmem, maxmem, freemem, usedmem) VALUES(NOW(),?,?,?,?)";
		getJdbcTemplateXServer().execute(insertSQL,
				new Object[] { totalMem, maxMem, freeMem, usedMem });
	}

	public void mailInterfaceInvoke() {
		MailTemplate mailTemplate = MailTemplate.getMailTemplate();

		String host = "no config";
		try {
			host = InetAddress.getLocalHost().toString();
		} catch (Exception e) {
			host = xServerHttpConfig.getHostname();
		}

		StringBuffer sb = new StringBuffer("<h3>Host:[" + host + "] "
				+ "接口调用情况</h3>\n");
		sb.append("<table border='1' cellspacing='0' cellpadding='6'>").append(
				"<tr><td>接口</td><td>调用次数</td><td>失败次数</td><td>失败率</td></tr>");
		for (Entry<String, AtomicInteger> entry : invokeListener.entrySet()) {
			String key = entry.getKey();
			int invoke = entry.getValue().get();
			int error = errorListener.get(key).get();
			String percent = StringUtil.percent(error, invoke, true);

			if (error > 0) {
				String fontPrefix = "<font color='red'>";
				String fontSuffix = "</font>";
				sb.append("<tr><td>" + fontPrefix + key + fontSuffix
						+ "</td><td>" + fontPrefix + invoke + fontSuffix
						+ "</td><td>" + fontPrefix + error + fontSuffix
						+ "</td><td>" + fontPrefix + percent + fontSuffix
						+ "</td></tr>");
			} else {
				sb.append("<tr><td>" + key + "</td><td>" + invoke + "</td><td>"
						+ error + "</td><td>" + percent + "</td></tr>");
			}

		}
		sb.append("</table>");

		try {
			mailTemplate.sendHtml(xServerHttpConfig.getRecipients(),
					"XServer接口调用情况", sb.toString());
		} finally {
			resetListener();
		}
	}
}

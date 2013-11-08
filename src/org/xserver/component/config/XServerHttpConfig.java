package org.xserver.component.config;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.handler.execution.MemoryAwareThreadPoolExecutor;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.springframework.stereotype.Component;
import org.xserver.component.annotation.Config;

/**
 * Configure HTTP XServer
 * 
 * @author postonzhang
 * @since 2013/3/4
 */
@Component
public class XServerHttpConfig {

	private static final int PROCESSOR_NUM = Runtime.getRuntime()
			.availableProcessors();
	/** XServer HTTP listen port, default:8080. */
	@Config
	private int port = 8080;
	/**
	 * Debug model, if open, when throwable occur, detail information will
	 * append response, else not.
	 */
	@Config
	private boolean debug = true;
	/** the maximum number of active threads */
	@Config
	private int corePoolSize = PROCESSOR_NUM * 50;
	/** the maximum total size of the queued events per channel. */
	@Config
	private long maxChannelMemorySize = 1024 * 1024 * 100;
	/** the maximum total size of the queued events for this pool */
	@Config
	private long maxTotalMemorySize = 1024 * 1024 * 1024;
	/** the amount of time for an inactive thread to shut itself down */
	@Config
	private long keepAliveTime = 60L;
	/**
	 * when XServer business interface occur exception or error, will send sms
	 * to users
	 */
	@Config
	private String smsUsers = "postonzhang";
	/**
	 * thread threshold percentage
	 */
	@Config
	private float threadThreshold = 0.1f;
	@Config
	private String hostname;
	@Config(split = ",")
	private String[] recipients;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public long getMaxChannelMemorySize() {
		return maxChannelMemorySize;
	}

	public void setMaxChannelMemorySize(long maxChannelMemorySize) {
		this.maxChannelMemorySize = maxChannelMemorySize;
	}

	public long getMaxTotalMemorySize() {
		return maxTotalMemorySize;
	}

	public void setMaxTotalMemorySize(long maxTotalMemorySize) {
		this.maxTotalMemorySize = maxTotalMemorySize;
	}

	public long getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	public String getSmsUsers() {
		return smsUsers;
	}

	public void setSmsUsers(String smsUsers) {
		this.smsUsers = smsUsers;
	}

	public float getThreadThreshold() {
		return threadThreshold;
	}

	public void setThreadThreshold(float threadThreshold) {
		this.threadThreshold = threadThreshold;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String[] getRecipients() {
		return recipients;
	}

	public void setRecipients(String[] recipients) {
		this.recipients = recipients;
	}

	/**
	 * {@link http
	 * ://docs.jboss.org/netty/3.2/api/org/jboss/netty/handler/execution/}
	 * 
	 * OrderedMemoryAwareThreadPoolExecutor.html
	 * 
	 * <pre>
	 * Event execution order
	 * 
	 * For example, let's say there are two executor threads that handle the events from the two channels:
	 *            -------------------------------------> Timeline ------------------------------------>
	 * 
	 *  Thread X: --- Channel A (Event A1) --.   .-- Channel B (Event B2) --- Channel B (Event B3) --->
	 *                                        \ /
	 *                                         X
	 *                                        / \
	 *  Thread Y: --- Channel B (Event B1) --'   '-- Channel A (Event A2) --- Channel A (Event A3) --->
	 *  
	 * As you see, the events from different channels are independent from each other. That is, an event of Channel B will not be blocked by an event of Channel A and vice versa, unless the thread pool is exhausted.
	 * Also, it is guaranteed that the invocation will be made sequentially for the events from the same channel. For example, the event A2 is never executed before the event A1 is finished. (Although not recommended, if you want the events from the same channel to be executed simultaneously, please use MemoryAwareThreadPoolExecutor instead.)
	 * 
	 * However, it is not guaranteed that the invocation will be made by the same thread for the same channel. The events from the same channel can be executed by different threads. For example, the Event A2 is executed by the thread Y while the event A1 was executed by the thread X.
	 * </pre>
	 * 
	 * @return
	 */
	public ThreadPoolExecutor initOrderExecutor() {
		OrderedMemoryAwareThreadPoolExecutor orderedMemoryAwareThreadPoolExecutor = new OrderedMemoryAwareThreadPoolExecutor(
				corePoolSize, maxChannelMemorySize, maxTotalMemorySize,
				keepAliveTime, TimeUnit.SECONDS);

		return orderedMemoryAwareThreadPoolExecutor;
	}

	public ThreadPoolExecutor initMemoryExecutor() {
		MemoryAwareThreadPoolExecutor memoryAwareThreadPoolExecutor = new MemoryAwareThreadPoolExecutor(
				corePoolSize, maxChannelMemorySize, maxTotalMemorySize,
				keepAliveTime, TimeUnit.SECONDS);

		return memoryAwareThreadPoolExecutor;
	}
}

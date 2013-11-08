package org.xserver.component.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * XServer thread factory used by the monitor executors.
 * <P>
 * This factory creates all new threads used by an Executor in the same
 * ThreadGroup. If there is a SecurityManager, it uses the group of
 * System.getSecurityManager(), else the group of the thread instantiating this
 * DaemonThreadFactory. Each new thread is created as a daemon thread with
 * priority Thread.NORM_PRIORITY. New threads have names accessible via
 * Thread.getName() of "XServer-<poolName>-pool-#M", where M is the sequence
 * number of the thread created by this factory.
 */
public class XServerThreadFactory implements ThreadFactory {

	private final ThreadGroup threadGroup;

	private boolean daemon;

	private final AtomicInteger threadNumber = new AtomicInteger();

	private final String namePrefix;

	private static final String nameSuffix = "";

	public XServerThreadFactory(String poolName) {
		this(poolName, false);
	}

	public XServerThreadFactory(String poolName, boolean daemon) {
		SecurityManager s = System.getSecurityManager();
		this.threadGroup = (s != null) ? s.getThreadGroup() : Thread
				.currentThread().getThreadGroup();
		this.daemon = daemon;
		this.namePrefix = "XServer-" + poolName + "-pool-#";
	}

	public XServerThreadFactory(String poolName, ThreadGroup group) {
		this(poolName, group, false);
	}

	public XServerThreadFactory(String poolName, ThreadGroup group,
			boolean daemon) {
		this.threadGroup = group;
		this.daemon = daemon;
		this.namePrefix = "XServer-" + poolName + " -pool-#";
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(threadGroup, r, namePrefix
				+ threadNumber.getAndIncrement() + nameSuffix, 0);
		t.setDaemon(daemon);
		if (t.getPriority() != Thread.NORM_PRIORITY) {
			t.setPriority(Thread.NORM_PRIORITY);
		}
		return t;
	}

}

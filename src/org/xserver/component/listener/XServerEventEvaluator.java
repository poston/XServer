package org.xserver.component.listener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.xserver.component.concurrent.XServerThreadFactory;
import org.xserver.component.log.LogUtil;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.spi.ContextAwareBase;

/**
 * <h3>The trigger strategy</h3>
 * 
 * <ul>
 * <li>when current time after last send mail time equal or greater than
 * evalutorTimeOffset(default 5 minutes)</li>
 * <li>when the error count equal or greater than errorCountLimit(default 50)</li>
 * <li>or set directSend true, will trigger. But this is deprecated</li>
 * </ul>
 * 
 * @author postonzhang
 * 
 */
public class XServerEventEvaluator extends ContextAwareBase implements
		EventEvaluator<ILoggingEvent> {

	/** send error mail time offset (millisecond), default value 5 minutes */
	private final long evalutorTimeOffset = 5 * 60 * 1000;
	/** whether direct send mail when the exception occur */
	private volatile boolean directSend;
	/** error count */
	private volatile AtomicInteger errorCount = new AtomicInteger();
	/** error count upper limit */
	private final int errorCountLimit = 50;
	/** last into evaluator method(1.time task trigger,2.logger error occur) */
	private volatile long lastEvalutorTime;
	/** recent error logger name */
	private volatile String recentLoggerName;

	private String name;

	private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors
			.newSingleThreadScheduledExecutor(new XServerThreadFactory(
					"Monitor", true));

	private static XServerEventEvaluator eventEvaluator;

	static {
		SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				eventEvaluator.check();
			}
		}, 10, 10, TimeUnit.SECONDS);
	}

	@Override
	public boolean isStarted() {
		return true;
	}

	@Override
	public void start() {
		lastEvalutorTime = System.currentTimeMillis();
		eventEvaluator = this;
	}

	@Override
	public void stop() {
	}

	/**
	 * Not Thread safe
	 */
	@Override
	public boolean evaluate(ILoggingEvent event) throws NullPointerException,
			EvaluationException {
		long now = event.getTimeStamp();
		if (lastEvalutorTime == 0) {
			lastEvalutorTime = now;
		}

		recentLoggerName = event.getLoggerName();

		if (directSend || errorCount.incrementAndGet() >= errorCountLimit
				|| now - lastEvalutorTime >= evalutorTimeOffset) {
			directSend = false;
			errorCount.set(0);
			lastEvalutorTime = now;
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * if the offset time between current time and lastSendTime equal or greater
	 * than eventTimeOffset, will trigger email event.
	 */
	private void check() {
		if (System.currentTimeMillis() - lastEvalutorTime >= evalutorTimeOffset) {
			trigger();
		}
	}

	/**
	 * When the errorCount greater than 0, mean there is one error email at
	 * least. We will trigger sendMail flag.
	 */
	private void trigger() {
		if (errorCount.get() > 0) {
			directSend = true;
			LogUtil.getLogger(recentLoggerName).error(
					"XServerEventEvalutor trigger");
		}
	}
}

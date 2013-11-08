package org.xserver.bootstrap;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xserver.common.util.ReflectionUtil;
import org.xserver.component.config.XServerHttpConfig;
import org.xserver.component.core.InterfaceContext;
import org.xserver.component.core.XServerHttpFactory;
import org.xserver.component.listener.XServerListener;
import org.xserver.component.log.StdOutLog;
import org.xserver.component.spring.SpringUtil;

/**
 * <h3>XServer bootstrap</h3>
 * 
 * When the XServer start, it should do two parts works. Aiming at reducing
 * coupling between classes, we introduce spring to system. So that initialize
 * relationship of class is one work. Remain is driving Network.
 * <code>XServerBootstrap</code> is used to start http service and accept client
 * request.
 * 
 * <h3>XServer HTTP Service Start</h3>
 * XServerBootstrap.bind()->AbstractChannel.bind()->Channels.bind()->
 * NioServerSocketPipelineSink
 * .eventSunk()->NioServerSocketPipelineSink.bind()->DeadLockProofWorker
 * .start()->NioServerBoss.process().<br/>
 * 
 * <p>
 * When XServerBootstrap call bind method will fire BOUND event, that enter
 * NioServerSocketPipeline and execute inner class Boss's thread method. The
 * Thread Boss will listen at specialized HTTP port, when accept socket, Boss
 * delegate event to a Work Thread and the Boss Thread will return again to
 * accept coming socket. The Work Thread will register OP_READ on its channel.
 * At NioWork Thread, it will reach processRegisterTaskQueue, processEventQueue,
 * processWriteTaskQueue and processSelectedKeys methods. The NioWork read
 * method will read datum and fire MessageReceive event. The event will flow
 * into pipeline.
 * </p>
 * 
 * <h3>Data Type</h3> XServer support common data type, like
 * <strong>json</strong>, <strong>jsonp</strong>, <strong>xml</strong>, etc.
 * Please note when the <strong>jsonp</strong> data use, the url key is
 * <strong>callback</strong>.
 * 
 * <h3>Stress Testing</h3>
 * 
 * <pre>
 * $>ab -n 10000 -c 1000 -k http://localhost:8080/taskQuery/query?a=1
 * This is ApacheBench, Version 2.3 <$Revision: 655654 $>
 * Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
 * Licensed to The Apache Software Foundation, http://www.apache.org/
 * 
 * Benchmarking localhost (be patient)
 * Completed 1000 requests
 * Completed 2000 requests
 * Completed 3000 requests
 * Completed 4000 requests
 * Completed 5000 requests
 * Completed 6000 requests
 * Completed 7000 requests
 * Completed 8000 requests
 * Completed 9000 requests
 * Completed 10000 requests
 * Finished 10000 requests
 * 
 * Server Software:        
 * Server Hostname:        localhost
 * Server Port:            8080
 * 
 * Document Path:          /taskQuery/query?a=1
 * Document Length:        17 bytes
 * 
 * Concurrency Level:      1000
 * Time taken for tests:   1.230 seconds
 * Complete requests:      10000
 * Failed requests:        6633
 *    (Connect: 0, Receive: 0, Length: 6633, Exceptions: 0)
 * Write errors:           0
 * Non-2xx responses:      6633
 * Keep-Alive requests:    0
 * Total transferred:      790909 bytes
 * HTML transferred:       349091 bytes
 * Requests per second:    8131.42 [#/sec] (mean)
 * Time per request:       122.980 [ms] (mean)
 * Time per request:       0.123 [ms] (mean, across all concurrent requests)
 * Transfer rate:          628.05 [Kbytes/sec] received
 * 
 * Connection Times (ms)<br>
 *               min  mean[+/-sd] median   max
 * Connect:        0    1   1.3      1      13
 * Processing:     0    6   7.4      4     231
 * Waiting:        0    6   7.3      4     231
 * Total:          0    7   7.6      5     231
 * 
 * Percentage of the requests served within a certain time (ms)
 *   50%      5
 *   66%      7
 *   75%      8
 *   80%      9
 *   90%     13
 *   95%     18
 *   98%     26
 *   99%     30
 *  100%    231 (longest request)
 * </pre>
 * 
 * @author postonzhang
 * @since 2013/2/26
 */
@Component
public class XServerBootstrap extends AbstractBootstrap {

	public static final Logger logger = LoggerFactory
			.getLogger(XServerBootstrap.class);
	public static final Logger stdOutLog = StdOutLog.getLogger();

	@Resource
	private InterfaceContext interfaceContext;
	@Resource
	private XServerHttpFactory xServerHttpFactory;
	@Resource
	private XServerHttpConfig xServerHttpConfig;
	@Resource
	private XServerListener xServerListener;

	// @Resource
	// private BootstrapManager bootstrapManager;

	public XServerHttpFactory getXServerHttpFactory() {
		return xServerHttpFactory;
	}

	public void setXServerHttpFactory(XServerHttpFactory xServerHttpFactory) {
		this.xServerHttpFactory = xServerHttpFactory;
	}

	public InterfaceContext getInterfaceContext() {
		return interfaceContext;
	}

	public void setInterfaceContext(InterfaceContext interfaceContext) {
		this.interfaceContext = interfaceContext;
	}

	public void initSystem() {
		XServerBootstrap xServerBootstrap = (XServerBootstrap) SpringUtil
				.getBean(XServerBootstrap.class);

		ServerBootstrap serverBootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		serverBootstrap.setOption("tcpNoDelay", true);

		serverBootstrap.setPipelineFactory(xServerBootstrap
				.getXServerHttpFactory());

		serverBootstrap
				.bind(new InetSocketAddress(xServerHttpConfig.getPort()));
		ReflectionUtil.classFound(new String[] {
				"nl.cwi.monetdb.jdbc.MonetDriver",
				"oracle.jdbc.driver.OracleDriver" });
		logger.info("XServer bind port {}.", xServerHttpConfig.getPort());
	}

	private static void initSpring() {
		logger.info("---------->Spring Dependency Injection");
		stdOutLog
				.info("----------------------------------------------------Spring Dependency Injection----------------------------------------------------");
		SpringUtil.loadApplicationContext();
	}

	public void initInterfaceMeta() {
		XServerBootstrap bootstrap = (XServerBootstrap) SpringUtil
				.getBean(XServerBootstrap.class);
		bootstrap.getInterfaceContext().loadInterfaceContext();
	}

	public static void main(String[] args) {
		long beginTime = System.currentTimeMillis();
		initSpring();

		XServerBootstrap xServerBootstrap = (XServerBootstrap) SpringUtil
				.getBean(XServerBootstrap.class);

		xServerBootstrap.init();

		try {
			long usedTime = System.currentTimeMillis() - beginTime;
			stdOutLog.info(logo);
			stdOutLog
					.info("XServer Http Service start OK, bind port [{}], USED TIME [{}], USED MEMORY [{}], TOTOAL MEMORY [{}]  at {}/{}",
							new Object[] {
									xServerBootstrap.xServerHttpConfig
											.getPort(),
									usedTime + "ms",
									(Runtime.getRuntime().totalMemory() - Runtime
											.getRuntime().freeMemory())
											/ 1024
											/ 1024 + "M",
									Runtime.getRuntime().maxMemory() / 1024
											/ 1024 + "M",
									xServerBootstrap.xServerListener
											.getOSName(),
									xServerBootstrap.xServerListener
											.getVersion() });
			logger.info(
					"XServer Http Service start OK, bind port [{}], USED TIME [{}], USED MEMORY [{}], TOTOAL MEMORY [{}]",
					new Object[] {
							xServerBootstrap.xServerHttpConfig.getPort(),
							usedTime + "ms",
							(Runtime.getRuntime().totalMemory() - Runtime
									.getRuntime().freeMemory())
									/ 1024
									/ 1024
									+ "M",
							Runtime.getRuntime().maxMemory() / 1024 / 1024
									+ "M" });
		} catch (Exception e) {
			stdOutLog.error("XServer Http Service start fail. port [{}]",
					xServerBootstrap.xServerHttpConfig.getPort());
			logger.error("XServer Start Fail.", e);
			System.exit(1);
		}
	}

	@Override
	public void init() {
		logger.info("---------->Load InterfaceMeta by Spring");
		stdOutLog
				.info("----------------------------------------------------Load InterfaceMeta by Spring----------------------------------------------------");
		initInterfaceMeta();

		logger.info("---------->Initialize XServer framework");
		stdOutLog
				.info("----------------------------------------------------Initialize XServer framework----------------------------------------------------");
		initSystem();
	}

	public static final String logo = "..........................................................................\n"
			+ "'##::::'##::'######::'########:'########::'##::::'##:'########:'########::\r\n"
			+ ". ##::'##::'##... ##: ##.....:: ##.... ##: ##:::: ##: ##.....:: ##.... ##:\r\n"
			+ ":. ##'##::: ##:::..:: ##::::::: ##:::: ##: ##:::: ##: ##::::::: ##:::: ##:\r\n"
			+ "::. ###::::. ######:: ######::: ########:: ##:::: ##: ######::: ########::\r\n"
			+ ":: ## ##::::..... ##: ##...:::: ##.. ##:::. ##:: ##:: ##...:::: ##.. ##:::\r\n"
			+ ": ##:. ##::'##::: ##: ##::::::: ##::. ##:::. ## ##::: ##::::::: ##::. ##::\r\n"
			+ " ##:::. ##:. ######:: ########: ##:::. ##:::. ###:::: ########: ##:::. ##:\r\n"
			+ "..:::::..:::......:::........::..:::::..:::::...:::::........::..:::::..::";
}

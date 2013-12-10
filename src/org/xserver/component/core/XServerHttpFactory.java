package org.xserver.component.core;

import javax.annotation.Resource;
import javax.annotation.PostConstruct;

import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioWorker;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.springframework.stereotype.Component;
import org.xserver.component.config.XServerHttpConfig;
import org.xserver.component.handler.RequestDispatchHandler;
import org.xserver.component.handler.XServerHttpInterceptor;
import org.xserver.component.handler.XServerHttpRequestDecoder;
import org.xserver.component.handler.XServerHttpResponseEncoder;

/**
 * <h3>The event handlers as follow:</h3> Timer -> XServerHttpDecoder ->
 * Executor -> XServerHttpInterceptor -> XServerRequestDispatcher ->
 * XServerHttpEncoder
 * 
 * <p>
 * <table border="1" cellspacing="0" cellpadding="6">
 * <tr>
 * <th>pipeline name</th>
 * <th>stream event type</th>
 * </tr>
 * <tr>
 * <td>XServerHttpDecoder</td>
 * <td>Upstream</td>
 * </tr>
 * <tr>
 * <td>XServerHttpInterceptor</td>
 * <td>Upstream</td>
 * </tr>
 * <tr>
 * <td>ExecutionHandler</td>
 * <td>Upstream, Downstream</td>
 * </tr>
 * <tr>
 * <td>XServerRequestDispatcher</td>
 * <td>Upstream</td>
 * </tr>
 * <tr>
 * <td>XServerHttpEncoder</td>
 * <td>Downstream</td>
 * </tr>
 * </table>
 * <h3>Upstream Handler order:</h3>
 * XServerHttpDecoder -> XServerHttpInterceptor -> ExecutionHandler ->
 * XServerRequestDispatcher
 * <h3>Downstream Handler order:</h3>
 * ExecutionHandler -> XServerHttpEncoder
 * </p>
 * 
 * <h3>Note</h3> The executionHandler should at the first, use executionHandler
 * deal with following operations is asynchronous, detail see
 * {@link ExecutionHandler#handleUpstream(ChannelHandlerContext, ChannelEvent)}
 * and
 * {@link ExecutionHandler#handleDownstream(ChannelHandlerContext, ChannelEvent)}
 * <br/>
 * 
 * <h3>Except</h3> The XServerHttpRequestDecoder should be at first of pipeline,
 * because use ExecutionHandler to handle decode will lead to thread safety
 * problem. For example: use ExecutionHandler to decode HTTP package may be
 * raised <strong> {@code java.lang.IllegalArgumentException: empty text}
 * </strong> Exception. When the channel register OP_READ event on selector, the
 * NioWork thread will process READ event if necessary, so if many threads read
 * a socket input stream parts, those threads may be regard the same socket
 * input stream as different socket input. To see {@link NioWorker#read()}.<br/>
 * <br/>
 * 
 * When Boss accept client request, many events will be fired. We should keep
 * one principle in mind, that is the each event will call a thread to deal
 * with, but not mean all event will handle at the same thread.
 * 
 * @author postonzhang
 * @since 2013/01/10
 * 
 */
@Component
public class XServerHttpFactory implements ChannelPipelineFactory {
	@Resource
	private RequestDispatchHandler requestDispatchHandler;
	@Resource
	private XServerHttpInterceptor xServerHttpInterceptor;
	@Resource
	private XServerHttpConfig xServerHttpConfig;

	private ExecutionHandler executionHandler;

	private static HashedWheelTimer timer = new HashedWheelTimer();

	@PostConstruct
	public void initExecutor() {
		executionHandler = new ExecutionHandler(
				xServerHttpConfig.initMemoryExecutor());
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		ChannelPipeline pipeline = Channels.pipeline();

		pipeline.addLast("XServerHttpDecoder", new XServerHttpRequestDecoder());
		pipeline.addLast("HttpChunker", new HttpChunkAggregator(1048576));
		pipeline.addLast("Executor", executionHandler);
		pipeline.addLast("XServerHttpInterceptor", xServerHttpInterceptor);
		pipeline.addLast("XServerRequestDispatcher", requestDispatchHandler);
		pipeline.addLast("XServerHttpEncoder", new XServerHttpResponseEncoder());
		// pipeline.addFirst("Timer", new ReadTimeoutHandler(timer, 5));

		return pipeline;
	}

	public RequestDispatchHandler getRequestDispatchHandler() {
		return requestDispatchHandler;
	}

	public void setRequestDispatchHandler(
			RequestDispatchHandler requestDispatchHandler) {
		this.requestDispatchHandler = requestDispatchHandler;
	}

	public XServerHttpConfig getXServerHttpConfig() {
		return xServerHttpConfig;
	}

	public void setXServerHttpConfig(XServerHttpConfig xServerHttpConfig) {
		this.xServerHttpConfig = xServerHttpConfig;
	}

	public ExecutionHandler getExecutionHandler() {
		return executionHandler;
	}

	public void setExecutionHandler(ExecutionHandler executionHandler) {
		this.executionHandler = executionHandler;
	}

}

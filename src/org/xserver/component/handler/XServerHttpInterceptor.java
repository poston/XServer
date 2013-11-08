package org.xserver.component.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xserver.component.annotation.ThreadPercent;
import org.xserver.component.config.XServerHttpConfig;
import org.xserver.component.core.InterfaceContext;
import org.xserver.component.core.InterfaceMeta;
import org.xserver.component.core.XServerHttpRequest;
import org.xserver.component.core.XServerHttpResponse;
import org.xserver.component.listener.XServerListener;

/**
 * <pre>
 * Use this interceptor resolve businesses influence. For example, business A always blocking at period of time, 
 * at this time, coming many request about the business A, that will lead to many Thread deal with business A,
 * but cannot release resources. So business B have no Thread to be dealt. Register this Handler will intercept 
 * special request cost many threads. 
 * If the request <strong>reach</strong> or <strong>beyond</strong> threshold, will response HTTP <strong>403</strong> for forbidden request, else just <strong>send event
 * to next handler</strong>.
 * </pre>
 * 
 * @author postonzhang
 * @since 2013/06/20
 * 
 */
@Component
public class XServerHttpInterceptor extends SimpleChannelUpstreamHandler {
	@Resource
	private XServerHttpConfig xServerHttpConfig;
	@Resource
	private InterfaceContext interfaceContext;
	@Resource
	private XServerListener xServerListener;

	private final Map<String, AtomicInteger> interceptor = new ConcurrentHashMap<String, AtomicInteger>();

	private static final Logger log = LoggerFactory
			.getLogger(XServerHttpInterceptor.class);

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		String path = ((XServerHttpRequest) e.getMessage()).getPath();
		InterfaceMeta interfaceMeta = interfaceContext.getInterfaceMeta(path);

		if (interfaceMeta != null) {
			xServerListener.incInvoke(path);

			AtomicInteger count = interceptor.get(path);
			int currentSize = count.get();

			ThreadPercent threadPercent = interfaceMeta.getMethod()
					.getAnnotation(ThreadPercent.class);
			float availableRate = (float) (threadPercent == null ? 1.0f / interceptor
					.size() < .1f ? .1f : 1.0f / interceptor.size()
					: threadPercent.value());

			int maxSize = (int) (availableRate * xServerHttpConfig
					.getCorePoolSize());

			if (currentSize >= maxSize) {
				XServerHttpResponse response = new XServerHttpResponse(
						HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);
				response.setContentString("service forbidden, because reach its maximum");
				ChannelFuture future = e.getChannel().write(response);
				future.addListener(ChannelFutureListener.CLOSE);
			} else {
				count.incrementAndGet();
				ctx.sendUpstream(e);
			}
		} else {
			XServerHttpResponse response = new XServerHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
			response.setContentString("bad request, there are no interface for PATH["
					+ path + "]");
			ChannelFuture future = e.getChannel().write(response);
			future.addListener(ChannelFutureListener.CLOSE);
			log.info("Response for Request [{}] is {}", path,
					"bad request, there are no interface for PATH[" + path
							+ "]");
		}
	}

	public void dec(String path) {
		AtomicInteger count = interceptor.get(path);
		if (count != null) {
			count.decrementAndGet();
		}
	}

	public void putInterface(String key) {
		interceptor.put(key, new AtomicInteger());
	}
}

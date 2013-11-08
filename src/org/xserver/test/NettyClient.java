package org.xserver.test;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpClientCodec;

public class NettyClient {
	public static void main(String[] args) throws InterruptedException {
		ClientBootstrap bootstrap = new ClientBootstrap(
				new NioClientSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {

			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("decode", new HttpClientCodec());
				return Channels.pipeline(new SendMsgHandler());
			}
		});

		// for (int i = 0; i < 100000; i++) {
		bootstrap.connect(new InetSocketAddress("127.0.0.1", 8080));
		// Thread.sleep(500);
		// }
	}

	private static class SendMsgHandler extends SimpleChannelHandler {
		@Override
		public void channelConnected(ChannelHandlerContext ctx,
				ChannelStateEvent e) throws Exception {
			String msg = "Hiiiiii";
			ChannelBuffer buffer = ChannelBuffers.buffer(1024);
			buffer.writeBytes(msg.toString().getBytes());
			e.getChannel().write(buffer);
		}

		@Override
		public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
				throws Exception {
			Object m = e.getMessage();
			System.out.println(m);
		}
	}
}

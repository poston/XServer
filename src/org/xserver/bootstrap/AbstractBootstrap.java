package org.xserver.bootstrap;

/**
 * As always, all system when start should do many thing. As TCP/IP or HTTP
 * Server, the system must should do two parts at least. One work is initialize
 * the system, like load system configuration, another should build the network
 * listen. So class <code>AbstractBootstrap</code> build in the method
 * <code>initSystem</code> to initialize the part one. The part two given by
 * low-level framework.
 * 
 * @author postonzhang
 * @since 2013/01/28
 * 
 */
public abstract class AbstractBootstrap {
	public abstract void init();
}

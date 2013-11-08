package org.xserver.bootstrap;

import org.springframework.stereotype.Component;

@Component
public class BootstrapManager {
	private long _initSpring;
	private long initSpring;

	private long _initInterfaceMeta;
	private long initInterfaceMeta;

	private long _initSystem;
	private long initSystem;

	public long get_initSpring() {
		return _initSpring;
	}

	public void set_initSpring(long _initSpring) {
		this._initSpring = _initSpring;
	}

	public long getInitSpring() {
		return initSpring;
	}

	public void setInitSpring(long initSpring) {
		this.initSpring = initSpring;
	}

	public long get_initInterfaceMeta() {
		return _initInterfaceMeta;
	}

	public void set_initInterfaceMeta(long _initInterfaceMeta) {
		this._initInterfaceMeta = _initInterfaceMeta;
	}

	public long getInitInterfaceMeta() {
		return initInterfaceMeta;
	}

	public void setInitInterfaceMeta(long initInterfaceMeta) {
		this.initInterfaceMeta = initInterfaceMeta;
	}

	public long get_initSystem() {
		return _initSystem;
	}

	public void set_initSystem(long _initSystem) {
		this._initSystem = _initSystem;
	}

	public long getInitSystem() {
		return initSystem;
	}

	public void setInitSystem(long initSystem) {
		this.initSystem = initSystem;
	}

	public static void invoke() {
		System.out.println(BootstrapManager.class.getClassLoader());
	}
}

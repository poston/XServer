package org.xserver.interfaces.inner;

import org.xserver.component.annotation.Chart;
import org.xserver.component.annotation.Description;

public class XServerMem {
	@Chart(false)
	@Description("记录时间")
	private String time;
	@Chart(name = "java内存")
	@Description("java内存")
	private int totalmem;
	@Chart(name = "JVM最大可用内存")
	@Description("JVM最大可用内存")
	private int maxmem;
	@Chart(name = "剩余内存")
	@Description("剩余内存")
	private int freemem;
	@Chart(name = "已使用内存")
	@Description("已使用内存")
	private int usedmem;

	public XServerMem() {

	}

	public XServerMem(String time, int totalmem, int maxmem, int freemem,
			int usedmem) {
		this.time = time;
		this.totalmem = totalmem;
		this.maxmem = maxmem;
		this.freemem = freemem;
		this.usedmem = usedmem;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getTotalmem() {
		return totalmem;
	}

	public void setTotalmem(int totalmem) {
		this.totalmem = totalmem;
	}

	public int getmaxmem() {
		return maxmem;
	}

	public void setmaxmem(int maxmem) {
		this.maxmem = maxmem;
	}

	public int getFreemem() {
		return freemem;
	}

	public void setFreemem(int freemem) {
		this.freemem = freemem;
	}

	public int getUsedmem() {
		return usedmem;
	}

	public void setUsedmem(int usedmem) {
		this.usedmem = usedmem;
	}

	@Override
	public String toString() {
		return "XServerMem [time=" + time + ", totalmem=" + totalmem
				+ ", maxmem=" + maxmem + ", freemem=" + freemem + ", usedmem="
				+ usedmem + "]";
	}
}

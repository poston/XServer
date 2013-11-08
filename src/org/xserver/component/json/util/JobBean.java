package org.xserver.component.json.util;

import org.xserver.component.annotation.Description;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class JobBean {
	@JsonIgnore
	@Description("任务标识")
	private String jobID;
	@Description("任务名称")
	private String jobName;
	@Description("任务开始时间")
	private String startTime;
	@Description("任务结束时间")
	private String endTime;
	@Description("任务运行状态")
	private String status;

	public String getJobID() {
		return jobID;
	}

	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "JobBean [jobID=" + jobID + ", jobName=" + jobName
				+ ", startTime=" + startTime + ", endTime=" + endTime
				+ ", status=" + status + "]";
	}

}

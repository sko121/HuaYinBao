package com.thtfit.pos.model;

import java.io.Serializable;

public class PushInfo implements Serializable{

	
	/**
	 * 记录信息类的实体化
	 */
	private static final long serialVersionUID = 882585490691950302L;
	
	private String type;//记录类别类别
	private String time;//记录时间
	private String content;//记录内容
	
	
	public PushInfo() {
		super();
	}
	

	public PushInfo(String type, String time, String content) {
		super();
		this.type = type;
		this.time = time;
		this.content = content;
	}

	@Override
	public String toString() {
		return "PushInfo [type=" + type + ", time=" + time + ", content="
				+ content + "]";
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public String getTime() {
		return time;
	}



	public void setTime(String time) {
		this.time = time;
	}



	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}

package com.thtfit.pos.model;

import java.io.Serializable;

public class ItemType implements Serializable {

	/**
	 * 类别
	 */
	private static final long serialVersionUID = -8888957051875753337L;

	private Integer id;
	private String name;

	public ItemType() {
	}

	public ItemType(Integer id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

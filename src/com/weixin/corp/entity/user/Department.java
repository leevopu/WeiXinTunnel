package com.weixin.corp.entity.user;

import java.io.Serializable;

/**
 * н╒пе╡©це
 */
public class Department implements Serializable {

	private static final long serialVersionUID = -3886293804629153404L;
	private int id;
	private String name;
	private int parentid;
	private int order;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getParentid() {
		return parentid;
	}

	public void setParentid(int parentid) {
		this.parentid = parentid;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

}

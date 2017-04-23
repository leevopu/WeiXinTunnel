package test;

import java.util.List;

public class CopyOfUser {
	private String id;
	private String xxx;
	private String name;
	private String Address;
	
	public CopyOfUser(){
	}
	
	public CopyOfUser(String id, String name, String address) {
		super();
		this.id = id;
		this.xxx = name;
	}
	public String getXXId() {
		return id;
	}
	public void setDDId(String id) {
		this.id = id;
	}
}

package test;

import java.util.List;

public class User {
	private String id;
	private String xxx;
	
	public User(){
	}
	
	public User(String id, String name, String address) {
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

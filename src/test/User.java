package test;

import java.util.List;

public class User {
	private String id;
	private String name;
	
	public User(){
	}
	
	public User(String id, String name, String address) {
		super();
		this.id = id;
		this.name = name;
	}
	public String getXXId() {
		return id;
	}
	public void setDDId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}

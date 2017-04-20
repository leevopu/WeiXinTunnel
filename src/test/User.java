package test;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("person")
public class User {
	@XStreamAlias("personId")
	private String id;
	private String name;
	private String url;
	private List<String> image;
	
	
	
	public User(){
	}
	
	public User(String id, String name, String url) {
		super();
		this.id = id;
		this.name = name;
		this.url = url;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}

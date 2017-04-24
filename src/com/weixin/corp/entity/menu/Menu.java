package com.weixin.corp.entity.menu;


/**
 * ²Ëµ¥
 * @author caspar.chen
 * @version 1.0
 * 
 */
public class Menu {
	
	/**
	 * ²Ëµ¥°´Å¥
	 */
	private Button[] button;

	public Button[] getButton() {
		return button;
	}


	public void setButton(Button[] button) {
		this.button = button;
	}


	public Menu(Button[] button) {
		super();
		this.button = button;
	}


	public Menu() {
		super();
	}  
}

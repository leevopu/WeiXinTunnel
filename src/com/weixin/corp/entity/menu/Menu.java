package com.weixin.corp.entity.menu;


/**
 * �˵�
 * @author caspar.chen
 * @version 1.0
 * 
 */
public class Menu {
	
	/**
	 * �˵���ť
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

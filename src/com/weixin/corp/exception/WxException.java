package com.weixin.corp.exception;


public class WxException extends Exception {

	private static final long serialVersionUID = -4814271321318836924L;
	
	private String error;

	  public WxException(String error) {
	    super(error);
	    this.error = error;
	  }

	  public WxException(String error, Throwable cause) {
	    super(error, cause);
	    this.error = error;
	  }

	  public String getError() {
	    return this.error;
	  }

}

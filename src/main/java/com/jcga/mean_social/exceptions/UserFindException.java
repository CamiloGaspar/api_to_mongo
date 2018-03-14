package com.jcga.mean_social.exceptions;

public class UserFindException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	private Exception exception;
	
	public UserFindException(String message){
	
		super(message);
		this.message = message;
	}
	public UserFindException(String message, Exception exception){
		
		super(message, exception);
		this.message = message;
		this.exception = exception;
	}
	
	public String getMessage() {
		return message;
	}

	public Exception getException() {
		return exception;
	}
}

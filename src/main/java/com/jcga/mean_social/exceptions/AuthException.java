package com.jcga.mean_social.exceptions;

public class AuthException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	private Exception exception;
	
	public AuthException(String message){
	
		super(message);
		this.message = message;
	}
	public AuthException(String message, Exception exception){
		
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

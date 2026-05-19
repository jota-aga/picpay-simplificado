package com.jh.picpay_simplificado.exceptions;

public class NotAuthorizedException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotAuthorizedException(String message) {
		super(message);
	}
	
}

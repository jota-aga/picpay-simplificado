package com.jh.picpay_simplificado.exceptions;

public class NotFoundException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotFoundException(String object) {
		super(object + "não encontrado(a)");
	}
	
	
}

package com.jh.picpay_simplificado.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalHandlerException {
	
	@ExceptionHandler(NotFoundException.class)
	public String handlerNotFoundException(NotFoundException ex) {
		return ex.getMessage();
	}
	
	@ExceptionHandler(NotAuthorizedException.class)
	public String handlerNotAuthorizedException(NotAuthorizedException ex) {
		return ex.getMessage();
	}
}

package com.jh.picpay_simplificado.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.jh.picpay_simplificado.dto.auth.LoginRequest;
import com.jh.picpay_simplificado.dto.auth.LoginResponse;
import com.jh.picpay_simplificado.dto.auth.UserRequest;
import com.jh.picpay_simplificado.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	private AuthService authService;
	
	@PostMapping("/register")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void registrarUsuario(@Valid @RequestBody UserRequest userRequest) {
		authService.createUser(userRequest);
	}
	
	@PostMapping("/login")
	@ResponseStatus(code = HttpStatus.ACCEPTED)
	public LoginResponse login(@RequestBody LoginRequest loginRequest) {
		String token = authService.doLogin(loginRequest);
		
		return new LoginResponse(token);		
	}
}

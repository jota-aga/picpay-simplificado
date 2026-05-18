package com.jh.picpay_simplificado;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jh.picpay_simplificado.auth.dto.LoginRequest;
import com.jh.picpay_simplificado.auth.dto.LoginResponse;
import com.jh.picpay_simplificado.dto.user.UserRequest;
import com.jh.picpay_simplificado.service.AuthService;
import com.jh.picpay_simplificado.service.TokenService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private AuthService authService;
	
	@PostMapping("/regiter")
	public void registrarUsuario(@Valid @RequestBody UserRequest userRequest) {
		authService.registerUser(userRequest);
	}
	
	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
		return authService.doLogin(loginRequest);
	}
}

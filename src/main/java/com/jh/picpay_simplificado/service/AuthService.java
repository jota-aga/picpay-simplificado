package com.jh.picpay_simplificado.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jh.picpay_simplificado.auth.dto.LoginRequest;
import com.jh.picpay_simplificado.auth.dto.LoginResponse;
import com.jh.picpay_simplificado.dto.user.UserRequest;
import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.repository.RoleRepository;
import com.jh.picpay_simplificado.repository.UserRepository;

@Service
public class AuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	public void registerUser(UserRequest userRequest) {
		User user = createUser(userRequest);
		
		userRepository.save(user);
	}
	
	public LoginResponse doLogin(LoginRequest login) {
		User user = procurarUsuarioPorEmail(login);
		
		isLoginCorrect(user, login.senha());
		String token = tokenService.generateToken(user);
		return new LoginResponse(token);
	}
	
	private void isLoginCorrect(User user, String senha) {
		if(!encoder.matches(senha, user.getSenha())) throw new RuntimeException();
	}
	
	private User procurarUsuarioPorEmail(LoginRequest login) {
		return userRepository.findByEmail(login.email()).orElseThrow(() -> new RuntimeException());
	}
	
	private Role findRoleByNome(String nome) {
		return roleRepository.findByName(nome)
				.orElseThrow(() -> new RuntimeException());
	}
	
	private User createUser(UserRequest userRequest) {
		String encryptedPassword = encoder.encode(userRequest.senha());
		
		Role role = findRoleByNome(userRequest.role());
		
		return User.builder()
				.nome(userRequest.nome())
				.email(userRequest.email())
				.senha(encryptedPassword)
				.role(role)
				.build();
	}
}

package com.jh.picpay_simplificado.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jh.picpay_simplificado.dto.auth.LoginRequest;
import com.jh.picpay_simplificado.dto.auth.UserRequest;
import com.jh.picpay_simplificado.entity.Carteira;
import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.enums.Roles;
import com.jh.picpay_simplificado.exceptions.ConflictException;
import com.jh.picpay_simplificado.exceptions.NotAuthorizedException;
import com.jh.picpay_simplificado.exceptions.NotFoundException;
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
	
	@Transactional
	public void createUser(UserRequest userRequest) {
		Carteira carteira = Carteira.builder()
				.balanco(BigDecimal.ZERO)
				.build();
		
		String encryptedPassword = encoder.encode(userRequest.senha());
		
		Role role = findRoleByNome(userRequest.role());
		
		User user = User.builder()
				.nome(userRequest.nome())
				.email(userRequest.email())
				.senha(encryptedPassword)
				.role(role)
				.carteira(carteira)
				.build();
		
		validateUserEmail(userRequest.email());
		
		if(userRequest.role().equals(Roles.COMPRADOR.name())) {
			validateDocumento(userRequest.cpf());
			user.setDocumento(userRequest.cpf());
		}
		else if(userRequest.role().equals(Roles.LOJISTA.name())) {
			validateDocumento(userRequest.cnpj());
			user.setDocumento(userRequest.cnpj());
		}
		
		userRepository.save(user);
	}
	
	public String doLogin(LoginRequest login) {
		User user = userRepository.findByEmail(login.email())
				.orElseThrow(() -> new NotAuthorizedException("Login ou senha não estão corretos"));
		
		isLoginCorrect(user, login.senha());
		String token = tokenService.generateToken(user);
		return token;
	}
	
	private void isLoginCorrect(User user, String senha) {
		if(!encoder.matches(senha, user.getSenha())) throw new NotAuthorizedException("Login ou senha não estão corretos");
	}
	
	private Role findRoleByNome(String nome) {
		return roleRepository.findByNome(nome)
				.orElseThrow(() -> new NotFoundException("Nome da Role"));
	}
	
	private void validateUserEmail(String email) {
		Optional<User> optionalUser = userRepository.findByEmail(email);
		
		if(optionalUser.isPresent()) throw new ConflictException("Email já cadastrado");
	}
	
	private void validateDocumento(String documento) {
		if(documento == null || documento.isBlank()) throw new ConflictException("Documento é obrigatório");
		
		Optional<User> optionalUser = userRepository.findByDocumento(documento);
		
		if(optionalUser.isPresent()) throw new ConflictException("Documento já cadastrado");
	}
}

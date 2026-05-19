package com.jh.picpay_simplificado.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jh.picpay_simplificado.dto.auth.LoginRequest;
import com.jh.picpay_simplificado.dto.auth.LoginResponse;
import com.jh.picpay_simplificado.dto.auth.UserRequest;
import com.jh.picpay_simplificado.entity.Comprador;
import com.jh.picpay_simplificado.entity.Lojista;
import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.exceptions.NotAuthorizedException;
import com.jh.picpay_simplificado.exceptions.NotFoundException;
import com.jh.picpay_simplificado.repository.CompradorRepository;
import com.jh.picpay_simplificado.repository.LojistaRepository;
import com.jh.picpay_simplificado.repository.RoleRepository;
import com.jh.picpay_simplificado.repository.UserRepository;


@Service
public class AuthService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private CompradorRepository compradorRepository;
	
	@Autowired
	private LojistaRepository lojistaRepository;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Transactional
	public void saveUser(UserRequest userRequest) {
		User user = createUser(userRequest);
		
		if(user.getRole().getNome().equals(Role.Value.COMPRADOR.name())) {
			Comprador comprador = Comprador.builder()
					.balanco(new BigDecimal(0))
					.CPF(userRequest.cpf())
					.user(user)
					.build();
			
			compradorRepository.save(comprador);
		}
		else if(user.getRole().getNome().equals(Role.Value.LOJISTA.name())) {
			Lojista lojista = Lojista.builder()
					.balanco(new BigDecimal(0))
					.CNPJ(userRequest.cnpj())
					.user(user)
					.build();
			
			lojistaRepository.save(lojista);
		}		
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
	
	public LoginResponse doLogin(LoginRequest login) {
		User user = findUserByEmail(login);
		
		isLoginCorrect(user, login.senha());
		String token = tokenService.generateToken(user);
		return new LoginResponse(token);
	}
	
	private void isLoginCorrect(User user, String senha) {
		if(!encoder.matches(senha, user.getSenha())) throw new NotAuthorizedException("Login ou senha não estão corretos");
	}
	
	private User findUserByEmail(LoginRequest login) {
		return userRepository.findByEmail(login.email()).orElseThrow(() -> new NotAuthorizedException("Login ou senha não estão corretos"));
	}
	
	private Role findRoleByNome(String nome) {
		return roleRepository.findByNome(nome)
				.orElseThrow(() -> new NotFoundException("nome de role"));
	}
}

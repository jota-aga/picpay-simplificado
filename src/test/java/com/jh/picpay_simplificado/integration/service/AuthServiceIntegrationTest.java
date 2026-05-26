package com.jh.picpay_simplificado.integration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jh.picpay_simplificado.dto.auth.LoginRequest;
import com.jh.picpay_simplificado.dto.auth.UserRequest;
import com.jh.picpay_simplificado.entity.Carteira;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.enums.Roles;
import com.jh.picpay_simplificado.exceptions.ConflictException;
import com.jh.picpay_simplificado.exceptions.NotAuthorizedException;
import com.jh.picpay_simplificado.exceptions.NotFoundException;
import com.jh.picpay_simplificado.repository.CarteiraRepository;
import com.jh.picpay_simplificado.repository.UserRepository;
import com.jh.picpay_simplificado.service.AuthService;

@SpringBootTest
public class AuthServiceIntegrationTest {
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CarteiraRepository carteiraRepository;
	
	private UserRequest userComprador;
	
	private UserRequest userLojista;
	
	@BeforeEach
	public void setUp() {
		userComprador = new UserRequest("João Henrique", "11237419484", "12345678000195", "joao@email.com", "senha123",
				Roles.COMPRADOR.name());
		userLojista = new UserRequest("João Henrique", "11237419484", "62420095000150", "joao@email.com", "senha123",
				Roles.LOJISTA.name());
		userRepository.deleteAll();
	}
	
	@Test
	public void createUserCompradorSucess() {
		authService.createUser(userComprador);
		
		List<User> users = userRepository.findAll();
		List<Carteira> carteiras = carteiraRepository.findAll();
		
		assertEquals(1, users.size());
		assertEquals(1, carteiras.size());

	}
	
	@Test
	public void createUserComprador_WhenCPFIsRepeated() {
		UserRequest userCPFRepetido = new UserRequest("João Henrique", "11237419484", "12345678000195", "joao123@email.com", "senha123",
				Roles.COMPRADOR.name());
		
		authService.createUser(userComprador);
		assertThrows(ConflictException.class, ()->authService.createUser(userCPFRepetido));
		
		List<User> users = userRepository.findAll();
		
		assertEquals(1, users.size());
	}
	
	@Test
	public void createUserComprador_WhenRoleNotFound() {
		userComprador = new UserRequest("João Henrique", "12345678978", "12345678000195", "joao@email.com", "senha123",
				"testes");
		assertThrows(NotFoundException.class, ()->authService.createUser(userComprador));
		
		List<User> users = userRepository.findAll();
		
		assertEquals(0, users.size());
	}
	
	@Test
	public void createUserLojistaSucess() {
		authService.createUser(userLojista);
		
		List<User> users = userRepository.findAll();
		
		assertEquals(1, users.size());
	}
	
	@Test
	public void createUserLojista_WhenRoleNotFound() {
		userLojista = new UserRequest("João Henrique", "12345678978", "12345678000195", "joao@email.com", "senha123",
				"testes");
		assertThrows(NotFoundException.class, ()->authService.createUser(userLojista));
		
		List<User> users = userRepository.findAll();
		
		assertEquals(0, users.size());
	}
	
	@Test
	public void createUserLojista_WhenCNPJIsRepeated() {
		UserRequest userCNPJRepetido = new UserRequest("João Henrique", "12345678978", userLojista.cnpj(), "joao123@email.com", "senha123",
				Roles.LOJISTA.name());
		authService.createUser(userLojista);
		
		assertThrows(ConflictException.class, () -> authService.createUser(userCNPJRepetido));
		
		List<User> users = userRepository.findAll();
		
		assertEquals(1, users.size());
	}
	
	@Test
	public void doLoginUserLojista() {
		authService.createUser(userLojista);
		
		LoginRequest login = new LoginRequest(userLojista.email(), userLojista.senha());
		
		String token = authService.doLogin(login);
		
		assertFalse(token.isBlank());
	}
	
	@Test
	public void doLoginLojista_whenEmailIsIncorrect() {
		authService.createUser(userLojista);
		
		LoginRequest login = new LoginRequest("email incorreto", userLojista.senha());
		
		assertThrows(NotAuthorizedException.class, () -> authService.doLogin(login));
	}
	
	@Test
	public void doLoginUserComprador() {
		authService.createUser(userComprador);
		
		LoginRequest login = new LoginRequest(userComprador.email(), userComprador.senha());
		
		String token = authService.doLogin(login);
		
		assertFalse(token.isBlank());
	}
	
	@Test
	public void doLoginComprador_whenEmailIsIncorrect() {
		authService.createUser(userComprador);
		
		LoginRequest login = new LoginRequest("email incorreto", userComprador.senha());
		
		assertThrows(NotAuthorizedException.class, () -> authService.doLogin(login));
	}
}

package com.jh.picpay_simplificado.integration;

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
import com.jh.picpay_simplificado.entity.Comprador;
import com.jh.picpay_simplificado.entity.Lojista;
import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.exceptions.ConflictException;
import com.jh.picpay_simplificado.exceptions.NotAuthorizedException;
import com.jh.picpay_simplificado.exceptions.NotFoundException;
import com.jh.picpay_simplificado.repository.CompradorRepository;
import com.jh.picpay_simplificado.repository.LojistaRepository;
import com.jh.picpay_simplificado.repository.UserRepository;
import com.jh.picpay_simplificado.service.AuthService;

import jakarta.validation.ConstraintViolationException;

@SpringBootTest
public class AuthServiceIntegrationTest {
	
	@Autowired
	private AuthService authService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LojistaRepository lojistaRepository;
	
	@Autowired
	private CompradorRepository compradorRepository;
	
	private UserRequest userComprador;
	
	private UserRequest userLojista;
	
	@BeforeEach
	public void setUp() {
		userComprador = new UserRequest("João Henrique", "11237419484", "12345678000195", "joao@email.com", "senha123",
				Role.Value.COMPRADOR.name());
		userLojista = new UserRequest("João Henrique", "11237419484", "62420095000150", "joao@email.com", "senha123",
				Role.Value.LOJISTA.name());
		lojistaRepository.deleteAll();
		compradorRepository.deleteAll();
	}
	
	@Test
	public void saveUserCompradorSucess() {
		authService.saveUser(userComprador);
		
		List<Comprador> compradores = compradorRepository.findAll();
		List<User> users = userRepository.findAll();
		
		assertEquals(1, compradores.size());
		assertEquals(1, users.size());
	}
	
	@Test
	public void saveUserComprador_WhenCPFIsInvalid() {
		userComprador = new UserRequest("João Henrique", "12345678978", "12345678000195", "joao@email.com", "senha123",
				Role.Value.COMPRADOR.name());
		assertThrows(ConstraintViolationException.class, ()->authService.saveUser(userComprador));
		
		List<Comprador> compradores = compradorRepository.findAll();
		List<User> users = userRepository.findAll();
		
		assertEquals(0, compradores.size());
		assertEquals(0, users.size());
	}
	
	@Test
	public void saveUserComprador_WhenCPFIsRepeated() {
		UserRequest userCPFRepetido = new UserRequest("João Henrique", "11237419484", "12345678000195", "joao123@email.com", "senha123",
				Role.Value.COMPRADOR.name());
		
		authService.saveUser(userComprador);
		assertThrows(ConflictException.class, ()->authService.saveUser(userCPFRepetido));
		
		List<Comprador> compradores = compradorRepository.findAll();
		List<User> users = userRepository.findAll();
		
		assertEquals(1, compradores.size());
		assertEquals(1, users.size());
	}
	
	@Test
	public void saveUserComprador_WhenRoleNotFound() {
		userComprador = new UserRequest("João Henrique", "12345678978", "12345678000195", "joao@email.com", "senha123",
				"testes");
		assertThrows(NotFoundException.class, ()->authService.saveUser(userComprador));
		
		List<Comprador> compradores = compradorRepository.findAll();
		List<User> users = userRepository.findAll();
		
		assertEquals(0, compradores.size());
		assertEquals(0, users.size());
	}
	
	@Test
	public void saveUserLojistaSucess() {
		authService.saveUser(userLojista);
		
		List<Lojista> lojistas = lojistaRepository.findAll();
		List<User> users = userRepository.findAll();
		
		assertEquals(1, lojistas.size());
		assertEquals(1, users.size());
	}
	
	@Test
	public void saveUserLojista_WhenCPFIsInvalid() {
		userLojista = new UserRequest("João Henrique", "12345678978", "3213546", "joao@email.com", "senha123",
				Role.Value.LOJISTA.name());
		assertThrows(ConstraintViolationException.class, ()->authService.saveUser(userLojista));
		
		List<Lojista> lojistas = lojistaRepository.findAll();
		List<User> users = userRepository.findAll();
		
		assertEquals(0, lojistas.size());
		assertEquals(0, users.size());
	}
	
	@Test
	public void saveUserLojista_WhenRoleNotFound() {
		userLojista = new UserRequest("João Henrique", "12345678978", "12345678000195", "joao@email.com", "senha123",
				"testes");
		assertThrows(NotFoundException.class, ()->authService.saveUser(userLojista));
		
		List<Lojista> lojistas = lojistaRepository.findAll();
		List<User> users = userRepository.findAll();
		
		assertEquals(0, lojistas.size());
		assertEquals(0, users.size());
	}
	
	@Test
	public void saveUserLojista_WhenCNPJIsRepeated() {
		UserRequest userCNPJRepetido = new UserRequest("João Henrique", "12345678978", userLojista.cnpj(), "joao123@email.com", "senha123",
				Role.Value.LOJISTA.name());
		authService.saveUser(userLojista);
		
		assertThrows(ConflictException.class, () -> authService.saveUser(userCNPJRepetido));
		
		List<Lojista> lojistas = lojistaRepository.findAll();
		List<User> users = userRepository.findAll();
		
		assertEquals(1, lojistas.size());
		assertEquals(1, users.size());
	}
	
	@Test
	public void doLoginUserLojista() {
		authService.saveUser(userLojista);
		
		LoginRequest login = new LoginRequest(userLojista.email(), userLojista.senha());
		
		String token = authService.doLogin(login);
		
		assertFalse(token.isBlank());
	}
	
	@Test
	public void doLoginLojista_whenEmailIsIncorrect() {
		authService.saveUser(userLojista);
		
		LoginRequest login = new LoginRequest("email incorreto", userLojista.senha());
		
		assertThrows(NotAuthorizedException.class, () -> authService.doLogin(login));
	}
	
	@Test
	public void doLoginUserComprador() {
		authService.saveUser(userComprador);
		
		LoginRequest login = new LoginRequest(userComprador.email(), userComprador.senha());
		
		String token = authService.doLogin(login);
		
		assertFalse(token.isBlank());
	}
	
	@Test
	public void doLoginComprador_whenEmailIsIncorrect() {
		authService.saveUser(userComprador);
		
		LoginRequest login = new LoginRequest("email incorreto", userComprador.senha());
		
		assertThrows(NotAuthorizedException.class, () -> authService.doLogin(login));
	}
}

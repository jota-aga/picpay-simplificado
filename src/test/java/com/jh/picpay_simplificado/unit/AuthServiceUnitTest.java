package com.jh.picpay_simplificado.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.jh.picpay_simplificado.dto.auth.LoginRequest;
import com.jh.picpay_simplificado.dto.auth.UserRequest;
import com.jh.picpay_simplificado.entity.Comprador;
import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.exceptions.NotAuthorizedException;
import com.jh.picpay_simplificado.exceptions.NotFoundException;
import com.jh.picpay_simplificado.repository.CompradorRepository;
import com.jh.picpay_simplificado.repository.LojistaRepository;
import com.jh.picpay_simplificado.repository.RoleRepository;
import com.jh.picpay_simplificado.repository.UserRepository;
import com.jh.picpay_simplificado.service.AuthService;
import com.jh.picpay_simplificado.service.TokenService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class AuthServiceUnitTest {
	
	@InjectMocks
	private AuthService authService;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private RoleRepository roleRepository;
	
	@Mock
	private CompradorRepository compradorRepository;
	
	@Mock
	private LojistaRepository lojistaRepository;
	
	@Mock
	private TokenService tokenService;
	
	@Mock
	private BCryptPasswordEncoder passwordEncoder;
	
	private LoginRequest loginRequest;
	
	private UserRequest userRequest;
	
	private User user;
	
	private Role role;
	
	@BeforeEach
	public void setUp() {
		role = new Role(1L, "role");
		loginRequest = new LoginRequest("email", "senha");
		user = new User(1L, "nome", "email", "senha", role);
	}
	
	@Test
	public void doLoginSucess() {
		when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(loginRequest.senha(), user.getSenha())).thenReturn(true);
		when(tokenService.generateToken(user)).thenReturn("token");
		
		authService.doLogin(loginRequest);
	}
	
	@Test
	public void doLogin_WhenUserNotFound() {
		when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.empty());
		
		assertThrows(NotAuthorizedException.class, () -> authService.doLogin(loginRequest));
	}
	
	@Test
	public void doLogin_WhenPasswordNotMatches() {
		when(userRepository.findByEmail(loginRequest.email())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(loginRequest.senha(), user.getSenha())).thenReturn(false);
		
		assertThrows(NotAuthorizedException.class, () -> authService.doLogin(loginRequest));
	}
	
	@Test
	public void saveUserCompradorSucess() {
		userRequest = new UserRequest("João Henrique", "11237419484", "12345678000195", "joao@email.com", "senha123",
				Role.Value.COMPRADOR.name());
		role.setNome(Role.Value.COMPRADOR.name());
		
		when(passwordEncoder.encode(userRequest.senha())).thenReturn("senha");
		when(roleRepository.findByNome(userRequest.role())).thenReturn(Optional.of(role));
		
		authService.saveUser(userRequest);
		
		verify(compradorRepository, atLeastOnce()).save(any());
	}
	
	@Test
	public void saveUserComprador_WhenRoleNotFound() {
		userRequest = new UserRequest("João Henrique", "11237419484", "12345678000195", "joao@email.com", "senha123",
				Role.Value.COMPRADOR.name());
		role.setNome(Role.Value.COMPRADOR.name());
		
		when(passwordEncoder.encode(userRequest.senha())).thenReturn("senha");
		when(roleRepository.findByNome(userRequest.role())).thenReturn(Optional.empty());
		
		assertThrows(NotFoundException.class, () -> authService.saveUser(userRequest));
		
		verify(compradorRepository, never()).save(any());
	}
	
	@Test
	public void saveUserLojistaSucess() {
		userRequest = new UserRequest("João Henrique", "11237419484", "12345678000195", "joao@email.com", "senha123",
				Role.Value.LOJISTA.name());
		role.setNome(Role.Value.LOJISTA.name());
		
		when(passwordEncoder.encode(userRequest.senha())).thenReturn("senha");
		when(roleRepository.findByNome(userRequest.role())).thenReturn(Optional.of(role));
		
		authService.saveUser(userRequest);
		
		verify(lojistaRepository, atLeastOnce()).save(any());
	}
	
	@Test
	public void saveUserLojista_WhenRoleNotFound() {
		userRequest = new UserRequest("João Henrique", "11237419484", "12345678000195", "joao@email.com", "senha123",
				Role.Value.COMPRADOR.name());
		role.setNome(Role.Value.COMPRADOR.name());
		
		when(passwordEncoder.encode(userRequest.senha())).thenReturn("senha");
		when(roleRepository.findByNome(userRequest.role())).thenReturn(Optional.empty());
		
		assertThrows(NotFoundException.class, () -> authService.saveUser(userRequest));
		
		verify(lojistaRepository, never()).save(any());
	}
}

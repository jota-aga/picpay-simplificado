package com.jh.picpay_simplificado.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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
import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.exceptions.NotAuthorizedException;
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
	private TokenService tokenService;
	
	@Mock
	private BCryptPasswordEncoder passwordEncoder;
	
	private LoginRequest loginRequest;
	
	private User user;
	
	@BeforeEach
	public void setUp() {
		Role role = new Role(1L, "role");
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
}

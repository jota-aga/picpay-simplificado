package com.jh.picpay_simplificado.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
import com.jh.picpay_simplificado.dto.auth.UserRequest;
import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.enums.Roles;
import com.jh.picpay_simplificado.exceptions.ConflictException;
import com.jh.picpay_simplificado.exceptions.NotAuthorizedException;
import com.jh.picpay_simplificado.exceptions.NotFoundException;
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
	
	private UserRequest userRequest;
	
	private User user;
	
	private Role role;
	
	@BeforeEach
	public void setUp() {
		role = new Role(1L, "role");
		loginRequest = new LoginRequest("email", "senha");
		user = User.builder()
				.nome("nome")
				.email("email")
				.documento("documento")
				.senha("password")
				.role(role)
				.build();
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
	public void createUserCompradorSucess() {
		userRequest = new UserRequest("João Henrique", "11237419484", "12345678000195", "joao@email.com", "senha123",
				Roles.COMPRADOR.name());
		role.setNome(Roles.COMPRADOR.name());
		
		when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.empty());
		when(passwordEncoder.encode(userRequest.senha())).thenReturn("senha");
		when(roleRepository.findByNome(userRequest.role())).thenReturn(Optional.of(role));
		when(userRepository.findByDocumento(userRequest.cpf())).thenReturn(Optional.empty());
		
		authService.createUser(userRequest);
		
		verify(userRepository, atLeastOnce()).save(any());
	}
	
	@Test
	public void createUserComprador_WhenRoleNotFound() {
		userRequest = new UserRequest("João Henrique", "11237419484", "12345678000195", "joao@email.com", "senha123",
				Roles.COMPRADOR.name());
		role.setNome(Roles.COMPRADOR.name());
		
		when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.empty());

		when(passwordEncoder.encode(userRequest.senha())).thenReturn("senha");
		when(roleRepository.findByNome(userRequest.role())).thenReturn(Optional.empty());
		
		assertThrows(NotFoundException.class, () -> authService.createUser(userRequest));
		
		verify(userRepository, never()).save(any());
	}
	
	@Test
	public void createUserComprador_WhenEmailIsRepeated() {
		userRequest = new UserRequest("João Henrique", "11237419484", "12345678000195", "joao@email.com", "senha123",
				Roles.COMPRADOR.name());
		role.setNome(Roles.COMPRADOR.name());
		
		when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.of(new User()));
		
		assertThrows(ConflictException.class, () -> authService.createUser(userRequest));
		
		verify(userRepository, never()).save(any());
	}
	
	@Test
	public void createUserLojistaSucess() {
		userRequest = new UserRequest("João Henrique", "11237419484", "12345678000195", "joao@email.com", "senha123",
				Roles.LOJISTA.name());
		role.setNome(Roles.LOJISTA.name());
		
		when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.empty());
		when(passwordEncoder.encode(userRequest.senha())).thenReturn("senha");
		when(roleRepository.findByNome(userRequest.role())).thenReturn(Optional.of(role));
		when(userRepository.findByDocumento(userRequest.cnpj())).thenReturn(Optional.empty());
		
		authService.createUser(userRequest);
		
		verify(userRepository, atLeastOnce()).save(any());
	}
	
	@Test
	public void createUserLojista_WhenRoleNotFound() {
		userRequest = new UserRequest("João Henrique", "11237419484", "12345678000195", "joao@email.com", "senha123",
				Roles.LOJISTA.name());
		role.setNome(Roles.LOJISTA.name());
		
		when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.empty());
		when(passwordEncoder.encode(userRequest.senha())).thenReturn("senha");
		when(roleRepository.findByNome(userRequest.role())).thenReturn(Optional.empty());
		
		assertThrows(NotFoundException.class, () -> authService.createUser(userRequest));
		
		verify(userRepository, never()).save(any());
	}
	
	@Test
	public void createUserLojista_WhenEmailIsRepeated() {
		userRequest = new UserRequest("João Henrique", "11237419484", "12345678000195", "joao@email.com", "senha123",
				Roles.LOJISTA.name());
		role.setNome(Roles.LOJISTA.name());
		
		when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.of(new User()));
		
		assertThrows(ConflictException.class, () -> authService.createUser(userRequest));
		
		verify(userRepository, never()).save(any());
	}
	
	@Test
	public void createUserLojista_WhenCNPJIsRepeated() {
		userRequest = new UserRequest("João Henrique", "11237419484", "12345678000195", "joao@email.com", "senha123",
				Roles.LOJISTA.name());
		role.setNome(Roles.LOJISTA.name());
		
		when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.empty());
		when(userRepository.findByDocumento(userRequest.cnpj())).thenReturn(Optional.of(new User()));
	
		assertThrows(ConflictException.class, () -> authService.createUser(userRequest));
		
		verify(userRepository, never()).save(any());
	}
}

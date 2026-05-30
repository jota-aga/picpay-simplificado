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

import com.jh.picpay_simplificado.creator.RoleCreator;
import com.jh.picpay_simplificado.creator.UserCreator;
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
	
	private Role roleComprador;
	
	private Role roleLojista;
	
	private UserRequest requestForComprador;
	
	private UserRequest requestForLojista; 
	
	@BeforeEach
	public void setUp() {
		roleComprador = RoleCreator.comprador();
		roleLojista = RoleCreator.lojista();
		user = UserCreator.userWithNoId(new Carteira(), roleComprador);
		
		loginRequest = new LoginRequest("email", "senha");
		
		requestForComprador = new UserRequest("João Henrique", "11237419484", null, "joao@email.com", "senha123",
				roleComprador.getNome());
		
		requestForLojista = new UserRequest("João Henrique", null, "12345678000195", "joao@email.com",
				"senha123", roleLojista.getNome());
		
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
		when(userRepository.findByEmail(requestForComprador.email())).thenReturn(Optional.empty());
		when(passwordEncoder.encode(requestForComprador.senha())).thenReturn("senha");
		when(roleRepository.findByNome(requestForComprador.role())).thenReturn(Optional.of(roleComprador));
		when(userRepository.findByDocumento(requestForComprador.cpf())).thenReturn(Optional.empty());
		
		authService.createUser(requestForComprador);
		
		verify(userRepository, atLeastOnce()).save(any());
	}
	
	@Test
	public void createUserComprador_WhenRoleNotFound() {
		
		when(passwordEncoder.encode(requestForComprador.senha())).thenReturn("senha");
		when(roleRepository.findByNome(requestForComprador.role())).thenReturn(Optional.empty());
		
		assertThrows(NotFoundException.class, () -> authService.createUser(requestForComprador));
		
		verify(userRepository, never()).save(any());
	}
	
	@Test
	public void createUserComprador_WhenEmailIsRepeated() {
		when(userRepository.findByEmail(requestForComprador.email())).thenReturn(Optional.of(new User()));
		when(roleRepository.findByNome(requestForComprador.role())).thenReturn(Optional.of(roleComprador));
		
		assertThrows(ConflictException.class, () -> authService.createUser(requestForComprador));
		
		verify(userRepository, never()).save(any());
	}
	
	@Test
	public void createUserLojistaSucess() {
		when(userRepository.findByEmail(requestForLojista.email())).thenReturn(Optional.empty());
		when(passwordEncoder.encode(requestForLojista.senha())).thenReturn("senha");
		when(roleRepository.findByNome(requestForLojista.role())).thenReturn(Optional.of(roleLojista));
		when(userRepository.findByDocumento(requestForLojista.cnpj())).thenReturn(Optional.empty());
		
		authService.createUser(requestForLojista);
		
		verify(userRepository, atLeastOnce()).save(any());
	}
	
	@Test
	public void createUserLojista_WhenRoleNotFound() {
		
		when(passwordEncoder.encode(requestForLojista.senha())).thenReturn("senha");
		when(roleRepository.findByNome(requestForLojista.role())).thenReturn(Optional.empty());
		
		assertThrows(NotFoundException.class, () -> authService.createUser(requestForLojista));
		
		verify(userRepository, never()).save(any());
	}
	
	@Test
	public void createUserLojista_WhenEmailIsRepeated() {
		
		when(userRepository.findByEmail(requestForLojista.email())).thenReturn(Optional.of(new User()));
		when(roleRepository.findByNome(requestForLojista.role())).thenReturn(Optional.of(roleLojista));
		
		assertThrows(ConflictException.class, () -> authService.createUser(requestForLojista));
		
		verify(userRepository, never()).save(any());
	}
	
	@Test
	public void createUserLojista_WhenCNPJIsRepeated() {
		
		when(userRepository.findByEmail(requestForLojista.email())).thenReturn(Optional.empty());
		when(userRepository.findByDocumento(requestForLojista.cnpj())).thenReturn(Optional.of(new User()));
		when(roleRepository.findByNome(requestForLojista.role())).thenReturn(Optional.of(roleLojista));

		assertThrows(ConflictException.class, () -> authService.createUser(requestForLojista));
		
		verify(userRepository, never()).save(any());
	}
}

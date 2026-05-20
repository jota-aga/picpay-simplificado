package com.jh.picpay_simplificado.integration;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.repository.RoleRepository;
import com.jh.picpay_simplificado.repository.UserRepository;
import com.jh.picpay_simplificado.service.SecurityService;
import com.jh.picpay_simplificado.service.TransferenciaService;

@SpringBootTest
@Transactional
public class TransactionServiceIntegrationTest {
	@Autowired
	private TransferenciaService transferenciaService;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@MockitoBean
	private SecurityService securityService;
	
	private User userComprador;
	
	private User userLojista;
	
	@BeforeEach
	public void setUp() {
		Role roleLojista = roleRepository.findByNome(Role.Value.LOJISTA.name()).get();
		Role roleComprador = roleRepository.findByNome(Role.Value.COMPRADOR.name()).get();

		userLojista = User.builder()
				.nome("lojista")
				.email("lojista@email.com")
				.senha("senha")
				.role(roleLojista)
				.build();
		userComprador = User.builder()
				.nome("comprador")
				.email("comprador@email.com")
				.senha("senha")
				.role(roleComprador)
				.build();
				
		userRepository.saveAll(List.of(userComprador, userLojista));
	}
	
	@Test
	public void depositoCompradorSucess() {
		when(securityService.getCurrentUser()).thenReturn(userComprador);
		
	}
	
	@Test
	public void depositoLojistaSucess() {
		when(securityService.getCurrentUser()).thenReturn(userLojista);
		
	}
}

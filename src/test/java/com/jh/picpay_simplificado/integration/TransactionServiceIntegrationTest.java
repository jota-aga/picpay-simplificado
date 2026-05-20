package com.jh.picpay_simplificado.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.jh.picpay_simplificado.entity.Comprador;
import com.jh.picpay_simplificado.entity.Lojista;
import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.repository.CompradorRepository;
import com.jh.picpay_simplificado.repository.LojistaRepository;
import com.jh.picpay_simplificado.repository.RoleRepository;
import com.jh.picpay_simplificado.service.SecurityService;
import com.jh.picpay_simplificado.service.TransactionService;

@SpringBootTest
@Transactional
public class TransactionServiceIntegrationTest {
	@Autowired
	private TransactionService transactionService;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private CompradorRepository compradorRepository;
	
	@Autowired
	private LojistaRepository lojistaRepository;
	
	@MockitoBean
	private SecurityService securityService;
	
	private Comprador comprador;
	
	private Lojista lojista;
	
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
				
		lojista = Lojista.builder()
				.CNPJ("61834148000116")
				.balanco(BigDecimal.ZERO)
				.user(userLojista)
				.build();
		
		comprador = Comprador.builder()
				.CPF("11237419484")
				.balanco(BigDecimal.ZERO)
				.user(userComprador)
				.build();
		
		lojista = lojistaRepository.save(lojista);
		comprador = compradorRepository.save(comprador);
	}
	
	@Test
	public void depositoCompradorSucess() {
		when(securityService.getCurrentUser()).thenReturn(userComprador);
		transactionService.realizarDeposito(BigDecimal.valueOf(200L));
		
		comprador = compradorRepository.findById(comprador.getId()).get();
		
		assertEquals(BigDecimal.valueOf(200L), comprador.getBalanco());
	}
	
	@Test
	public void depositoLojistaSucess() {
		when(securityService.getCurrentUser()).thenReturn(userLojista);
		transactionService.realizarDeposito(BigDecimal.valueOf(200L));
		
		lojista = lojistaRepository.findById(lojista.getId()).get();
		
		assertEquals(BigDecimal.valueOf(200L), lojista.getBalanco());
	}
}

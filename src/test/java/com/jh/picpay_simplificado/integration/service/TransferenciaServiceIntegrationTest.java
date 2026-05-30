package com.jh.picpay_simplificado.integration.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.jh.picpay_simplificado.client.AuthorizationClient;
import com.jh.picpay_simplificado.creator.CarteiraCreator;
import com.jh.picpay_simplificado.creator.UserCreator;
import com.jh.picpay_simplificado.dto.transferencia.TransferenciaRequest;
import com.jh.picpay_simplificado.entity.Carteira;
import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.Transferencia;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.enums.Roles;
import com.jh.picpay_simplificado.exceptions.ConflictException;
import com.jh.picpay_simplificado.exceptions.NotAuthorizedException;
import com.jh.picpay_simplificado.repository.RoleRepository;
import com.jh.picpay_simplificado.repository.TransferenciaRepository;
import com.jh.picpay_simplificado.repository.UserRepository;
import com.jh.picpay_simplificado.service.SecurityService;
import com.jh.picpay_simplificado.service.TransferenciaService;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class TransferenciaServiceIntegrationTest {
	@Autowired
	private TransferenciaService transferenciaService;
	
	@Autowired
	private TransferenciaRepository transferenciaRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@MockitoBean
	private SecurityService securityService;
	
	@MockitoBean
	private AuthorizationClient authorizationClient;
	
	private Carteira carteiraComprador;
	
	private Carteira carteiraLojista;
	
	private User comprador;
	
	private User lojista;
	
	private TransferenciaRequest request;
	
	@BeforeEach
	public void setUp() {
		carteiraComprador = CarteiraCreator.carteiraWith100Balanco();
		
		carteiraLojista = CarteiraCreator.carteiraWith0Balanco();
		
		Role roleComprador = roleRepository.findByNome(Roles.COMPRADOR.name()).get();
		Role roleLojista = roleRepository.findByNome(Roles.LOJISTA.name()).get();
		
		comprador = UserCreator.userWithNoId(carteiraComprador, roleComprador);
		lojista = UserCreator.userWithNoId(carteiraLojista, roleLojista);
		
		comprador = userRepository.save(comprador);
		lojista = userRepository.save(lojista);
		
		request = new TransferenciaRequest(BigDecimal.valueOf(100), lojista.getId());
	}
	
	@Test
	public void transferirSucesso() {
		when(securityService.getCurrentUser()).thenReturn(comprador);
		when(authorizationClient.autorizarTransferencia()).thenReturn(true);

		transferenciaService.realizarTransferencia(request);
		
		carteiraComprador = comprador.getCarteira();
		carteiraLojista = lojista.getCarteira();
		
		List<Transferencia> transferencias = transferenciaRepository.findAll();
		
		assertEquals(BigDecimal.ZERO, carteiraComprador.getBalanco());
		assertEquals(BigDecimal.valueOf(100), carteiraLojista.getBalanco());
		assertFalse(transferencias.isEmpty());
	}
	
	@Test
	public void transferir_WhenUserIsLojista() {
		when(securityService.getCurrentUser()).thenReturn(lojista);
		request = new TransferenciaRequest(BigDecimal.TEN, comprador.getId());
		assertThrows(NotAuthorizedException.class, () -> transferenciaService.realizarTransferencia(request));
		
		carteiraComprador = comprador.getCarteira();
		carteiraLojista = lojista.getCarteira();
		List<Transferencia> transferencias = transferenciaRepository.findAll();
		
		assertEquals(BigDecimal.valueOf(100), carteiraComprador.getBalanco());
		assertEquals(BigDecimal.ZERO, carteiraLojista.getBalanco());
		assertTrue(transferencias.isEmpty());
	}
	
	@Test
	public void transferir_WhenBalancoIsNotEnough() {
		when(securityService.getCurrentUser()).thenReturn(comprador);
		request = new TransferenciaRequest(BigDecimal.valueOf(200), lojista.getId());

		assertThrows(ConflictException.class, () -> transferenciaService.realizarTransferencia(request));
		
		carteiraComprador = comprador.getCarteira();
		carteiraLojista = lojista.getCarteira();
		List<Transferencia> transferencias = transferenciaRepository.findAll();
		
		assertEquals(BigDecimal.valueOf(100), carteiraComprador.getBalanco());
		assertEquals(BigDecimal.ZERO, carteiraLojista.getBalanco());
	}
	
	@Test
	public void transferir_WhenNotAuthorizedByServicoExterno() {
		when(securityService.getCurrentUser()).thenReturn(comprador);
		when(authorizationClient.autorizarTransferencia()).thenReturn(false);

		transferenciaService.realizarTransferencia(request);
		
		carteiraComprador = comprador.getCarteira();
		carteiraLojista = lojista.getCarteira();
		List<Transferencia> transferencias = transferenciaRepository.findAll();
		
		assertEquals(BigDecimal.valueOf(100), carteiraComprador.getBalanco());
		assertEquals(BigDecimal.ZERO, carteiraLojista.getBalanco());
		assertTrue(transferencias.isEmpty());
	}
}

package com.jh.picpay_simplificado.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.jh.picpay_simplificado.client.AuthorizationClient;
import com.jh.picpay_simplificado.dto.transferencia.TransferenciaRequest;
import com.jh.picpay_simplificado.entity.Carteira;
import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.repository.CarteiraRepository;
import com.jh.picpay_simplificado.repository.RoleRepository;
import com.jh.picpay_simplificado.repository.TransferenciaRepository;
import com.jh.picpay_simplificado.repository.UserRepository;
import com.jh.picpay_simplificado.service.SecurityService;
import com.jh.picpay_simplificado.service.TransferenciaService;

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
	private CarteiraRepository carteiraRepository;
	
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
		carteiraComprador = Carteira.builder()
				.balanco(BigDecimal.valueOf(100))
				.build();
		
		carteiraLojista = Carteira.builder()
				.balanco(BigDecimal.ZERO)
				.build();
		
		Role roleComprador = roleRepository.findByNome(Role.Value.COMPRADOR.name()).get();
		Role roleLojista = roleRepository.findByNome(Role.Value.LOJISTA.name()).get();
		
		comprador = User.builder()
				.nome("comprador")
				.email("email")
				.documento("documento")
				.role(roleComprador)
				.carteira(carteiraComprador)
				.build();
		
		lojista = User.builder()
				.nome("comprador")
				.email("email")
				.documento("documento")
				.role(roleLojista)
				.carteira(carteiraLojista)
				.build();
		
		comprador = userRepository.save(comprador);
		lojista = userRepository.save(lojista);
		
		request = new TransferenciaRequest(BigDecimal.valueOf(100), lojista.getId());
	}
	
	@Test
	public void transferirSucesso() {
		when(securityService.getCurrentUser()).thenReturn(comprador);
		doNothing().when(authorizationClient).autorizarTransferencia();

		transferenciaService.transferencia(request);
		
		carteiraComprador = comprador.getCarteira();
		carteiraLojista = lojista.getCarteira();
		
		assertEquals(BigDecimal.ZERO, carteiraComprador.getBalanco());
		assertEquals(BigDecimal.valueOf(100), carteiraLojista.getBalanco());
	}
}

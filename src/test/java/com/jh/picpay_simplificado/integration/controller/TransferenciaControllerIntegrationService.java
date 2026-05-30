package com.jh.picpay_simplificado.integration.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.jh.picpay_simplificado.client.AuthorizationClient;
import com.jh.picpay_simplificado.creator.CarteiraCreator;
import com.jh.picpay_simplificado.creator.UserCreator;
import com.jh.picpay_simplificado.dto.transferencia.TransferenciaRequest;
import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.enums.Roles;
import com.jh.picpay_simplificado.repository.RoleRepository;
import com.jh.picpay_simplificado.repository.UserRepository;
import com.jh.picpay_simplificado.service.SecurityService;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class TransferenciaControllerIntegrationService {
	
	private static final String url = "/api/transferencia";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@MockitoBean
	private SecurityService securityService;
	
	@MockitoBean
	private AuthorizationClient authorizationClient;
	
	private User userComprador;
	
	private User userLojista;
	
	@BeforeEach
	public void setUp() {
		Role roleComprador = roleRepository.findByNome(Roles.COMPRADOR.name()).get();
		Role roleLojista = roleRepository.findByNome(Roles.LOJISTA.name()).get();

		userComprador = UserCreator.userWithNoId(CarteiraCreator.carteiraWith100Balanco(), roleComprador);
		
		userLojista = UserCreator.userWithNoId(CarteiraCreator.carteiraWith0Balanco(), roleLojista);
		
		userComprador = userRepository.save(userComprador);
		userLojista = userRepository.save(userLojista);
	}
	
	@Test
	public void realizarTransferencia_WhenAllIsCorrect_ShouldReturn200() throws Exception {
		when(securityService.getCurrentUser()).thenReturn(userComprador);
		when(authorizationClient.autorizarTransferencia()).thenReturn(true);
		TransferenciaRequest request = new TransferenciaRequest(BigDecimal.valueOf(50), userLojista.getId());
		transferirPost("SCOPE_COMPRADOR", request).andExpect(MockMvcResultMatchers.status().isOk());
		
	}
	
	@Test
	public void realizarTransferencia_WhenScopeIsLojista_ShouldReturn401() throws Exception {
		when(securityService.getCurrentUser()).thenReturn(userComprador);
		when(authorizationClient.autorizarTransferencia()).thenReturn(true);
		TransferenciaRequest request = new TransferenciaRequest(BigDecimal.valueOf(50), userLojista.getId());
		
		transferirPost("SCOPE_LOJISTA", request).andExpect(MockMvcResultMatchers.status().isForbidden());
	}
	
	@Test
	public void realizarTransferencia_WhenUserIsLojista_ShouldReturn401() throws Exception {
		when(securityService.getCurrentUser()).thenReturn(userLojista);
		when(authorizationClient.autorizarTransferencia()).thenReturn(true);
		TransferenciaRequest request = new TransferenciaRequest(BigDecimal.valueOf(50), userComprador.getId());
		
		transferirPost("SCOPE_COMPRADOR", request).andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}
	
	@Test
	public void realizarTransferencia_WhenBalancoIsNotEnough_ShouldReturn409() throws Exception {
		when(securityService.getCurrentUser()).thenReturn(userComprador);
		when(authorizationClient.autorizarTransferencia()).thenReturn(true);
		TransferenciaRequest request = new TransferenciaRequest(BigDecimal.valueOf(101), userLojista.getId());
		
		transferirPost("SCOPE_COMPRADOR", request).andExpect(MockMvcResultMatchers.status().isConflict());
	}
	
	private ResultActions transferirPost(String scope, TransferenciaRequest request) throws JacksonException, Exception {
		return mockMvc.perform(MockMvcRequestBuilders.post(url)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
				.with(jwt().authorities(new SimpleGrantedAuthority(scope))));
	}
}

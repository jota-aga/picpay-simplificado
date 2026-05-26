package com.jh.picpay_simplificado.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.jh.picpay_simplificado.dto.auth.UserRequest;
import com.jh.picpay_simplificado.entity.Carteira;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.enums.Roles;
import com.jh.picpay_simplificado.repository.CarteiraRepository;
import com.jh.picpay_simplificado.repository.UserRepository;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AuthControllerIntegrationTest {
	
	private static String url = "http://localhost:8080/api/auth";
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CarteiraRepository carteiraRepository;
	
	private UserRequest requestComprador;
	
	private UserRequest requestLojista;
	
	@BeforeEach
	public void setUp() {
		requestComprador = new UserRequest("comprador", "11237419484", null, "comprador@email.com", "senhaComprador", Roles.COMPRADOR.name());
		requestLojista = new UserRequest("lojista", null, "17871266000102", "lojista@email.com", "senhaLojista", Roles.LOJISTA.name());
	}
	
	@Test
	public void shouldRegisterUserComprador() throws JacksonException, Exception {
		mockMvc.perform(MockMvcRequestBuilders.post(url+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestComprador)))
		.andExpect(MockMvcResultMatchers.status().isCreated());
		
		List<User> users = userRepository.findAll();
		List<Carteira> carteiras = carteiraRepository.findAll();
		
		
		assertEquals(1, users.size());
		assertEquals(1, carteiras.size());
	}
	
	@Test
	public void registerUserComprador_WhenCpfIsRepeated() throws JacksonException, Exception {
		
		UserRequest requestComCPFRepetido = new UserRequest("comprador", "11237419484", null, "emaildiferente@email.com", "senhaComprador", Roles.COMPRADOR.name());
		
		mockMvc.perform(MockMvcRequestBuilders.post(url+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestComCPFRepetido)))
		.andExpect(MockMvcResultMatchers.status().isCreated());
		
		mockMvc.perform(MockMvcRequestBuilders.post(url+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestComprador)))
		.andExpect(MockMvcResultMatchers.status().isConflict());
		
		List<User> users = userRepository.findAll();
		List<Carteira> carteiras = carteiraRepository.findAll();
		
		
		assertEquals(1, users.size());
		assertEquals(1, carteiras.size());
	}
	
	@Test
	public void registerUserComprador_WhenEmailIsRepeated() throws JacksonException, Exception {
		
		mockMvc.perform(MockMvcRequestBuilders.post(url+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestComprador)))
		.andExpect(MockMvcResultMatchers.status().isCreated());
		
		mockMvc.perform(MockMvcRequestBuilders.post(url+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestComprador)))
		.andExpect(MockMvcResultMatchers.status().isConflict());
		
		List<User> users = userRepository.findAll();
		List<Carteira> carteiras = carteiraRepository.findAll();
		
		
		assertEquals(1, users.size());
		assertEquals(1, carteiras.size());
	}
	
	@Test
	public void shouldRegisterUserLojista() throws JacksonException, Exception {
		mockMvc.perform(MockMvcRequestBuilders.post(url+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestLojista)))
		.andExpect(MockMvcResultMatchers.status().isCreated());
		
		List<User> users = userRepository.findAll();
		List<Carteira> carteiras = carteiraRepository.findAll();
		
		
		assertEquals(1, users.size());
		assertEquals(1, carteiras.size());
	}
	
	@Test
	public void registerUserLojista_WhenCnpjIsRepeated() throws JacksonException, Exception {
		UserRequest cnpjRepetido = new UserRequest("lojista", null, "17871266000102", "emailDiferente@email.com", "senhaLojista", Roles.LOJISTA.name());

		
		mockMvc.perform(MockMvcRequestBuilders.post(url+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(cnpjRepetido)))
		.andExpect(MockMvcResultMatchers.status().isCreated());
		
		mockMvc.perform(MockMvcRequestBuilders.post(url+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestLojista)))
		.andExpect(MockMvcResultMatchers.status().isConflict());
		
		List<User> users = userRepository.findAll();
		List<Carteira> carteiras = carteiraRepository.findAll();
		
		
		assertEquals(1, users.size());
		assertEquals(1, carteiras.size());
	}
	
	@Test
	public void registerUserLojista_WhenEmailIsRepeated() throws JacksonException, Exception {
		
		mockMvc.perform(MockMvcRequestBuilders.post(url+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestLojista)))
		.andExpect(MockMvcResultMatchers.status().isCreated());
		
		mockMvc.perform(MockMvcRequestBuilders.post(url+"/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(requestLojista)))
		.andExpect(MockMvcResultMatchers.status().isConflict());
		
		List<User> users = userRepository.findAll();
		List<Carteira> carteiras = carteiraRepository.findAll();
		
		
		assertEquals(1, users.size());
		assertEquals(1, carteiras.size());
	}
}

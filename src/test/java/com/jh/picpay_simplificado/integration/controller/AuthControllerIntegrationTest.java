package com.jh.picpay_simplificado.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.jh.picpay_simplificado.dto.auth.LoginRequest;
import com.jh.picpay_simplificado.dto.auth.LoginResponse;
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

	private static String url = "/api/auth";

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
		requestComprador = new UserRequest("comprador", "11237419484", null, "comprador@email.com", "senhaComprador",
				Roles.COMPRADOR.name());
		requestLojista = new UserRequest("lojista", null, "17871266000102", "lojista@email.com", "senhaLojista",
				Roles.LOJISTA.name());
	}

	@Test
	public void registerUserComprador_WhenDataIsCorrect_ShouldCreate() throws JacksonException, Exception {
		register(requestComprador).andExpect(MockMvcResultMatchers.status().isCreated());

		List<User> users = userRepository.findAll();
		List<Carteira> carteiras = carteiraRepository.findAll();

		assertEquals(1, users.size());
		assertEquals(1, carteiras.size());
	}

	@Test
	public void registerUserComprador_WhenCpfIsRepeated_ShouldReturn409() throws JacksonException, Exception {

		UserRequest requestComCPFRepetido = new UserRequest("comprador", "11237419484", null,
				"emaildiferente@email.com", "senhaComprador", Roles.COMPRADOR.name());

		register(requestComprador).andExpect(MockMvcResultMatchers.status().isCreated());

		register(requestComCPFRepetido).andExpect(MockMvcResultMatchers.status().isConflict());

		List<User> users = userRepository.findAll();
		List<Carteira> carteiras = carteiraRepository.findAll();

		assertEquals(1, users.size());
		assertEquals(1, carteiras.size());
	}

	@Test
	public void registerUserComprador_WhenEmailIsRepeated_ShouldReturn409() throws JacksonException, Exception {

		register(requestComprador).andExpect(MockMvcResultMatchers.status().isCreated());

		register(requestComprador)

				.andExpect(MockMvcResultMatchers.status().isConflict());

		List<User> users = userRepository.findAll();
		List<Carteira> carteiras = carteiraRepository.findAll();

		assertEquals(1, users.size());
		assertEquals(1, carteiras.size());
	}

	@Test
	public void registerUserLojista_WhenDataIsCorrect_ShouldCreate() throws JacksonException, Exception {
		register(requestLojista).andExpect(MockMvcResultMatchers.status().isCreated());

		List<User> users = userRepository.findAll();
		List<Carteira> carteiras = carteiraRepository.findAll();

		assertEquals(1, users.size());
		assertEquals(1, carteiras.size());
	}

	@Test
	public void registerUserLojista_WhenCnpjIsRepeated_ShouldReturn409() throws JacksonException, Exception {
		UserRequest cnpjRepetido = new UserRequest("lojista", null, "17871266000102", "emailDiferente@email.com",
				"senhaLojista", Roles.LOJISTA.name());

		register(cnpjRepetido).andExpect(MockMvcResultMatchers.status().isCreated());

		register(requestLojista).andExpect(MockMvcResultMatchers.status().isConflict());

		List<User> users = userRepository.findAll();
		List<Carteira> carteiras = carteiraRepository.findAll();

		assertEquals(1, users.size());
		assertEquals(1, carteiras.size());
	}

	@Test
	public void registerUserLojista_WhenEmailIsRepeated_ShouldReturn409() throws JacksonException, Exception {

		register(requestLojista).andExpect(MockMvcResultMatchers.status().isCreated());

		register(requestLojista).andExpect(MockMvcResultMatchers.status().isConflict());

		List<User> users = userRepository.findAll();
		List<Carteira> carteiras = carteiraRepository.findAll();

		assertEquals(1, users.size());
		assertEquals(1, carteiras.size());
	}

	@Test
	public void doLogin_WhenDataIsCorrect_ShouldReturnToken() throws JacksonException, Exception {
		LoginRequest login = new LoginRequest(requestComprador.email(), requestComprador.senha());

		register(requestComprador).andExpect(MockMvcResultMatchers.status().isCreated());

		MvcResult result = login(login).andExpect(MockMvcResultMatchers.status().isAccepted()).andReturn();

		String response = result.getResponse().getContentAsString();

		LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);

		assertNotNull(loginResponse.token());
		assertFalse(loginResponse.token().isBlank());
	}

	@Test
	public void doLogin_WhenEmailIsNotFound_ShouldReturn401() throws JacksonException, Exception {
		LoginRequest login = new LoginRequest(requestComprador.email(), requestComprador.senha());

		login(login).andExpect(MockMvcResultMatchers.status().isUnauthorized());

	}

	@Test
	public void doLogin_WhenPasswordIsIncorrect_ShouldReturn401() throws JacksonException, Exception {
		LoginRequest login = new LoginRequest(requestComprador.email(), "senhaAleatoria");

		register(requestComprador).andExpect(MockMvcResultMatchers.status().isCreated());

		login(login).andExpect(MockMvcResultMatchers.status().isUnauthorized());
	}

	private ResultActions register(UserRequest userRequest) throws JacksonException, Exception {
		return mockMvc.perform(MockMvcRequestBuilders.post(url + "/register").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userRequest)));
	}

	private ResultActions login(LoginRequest loginRequest) throws JacksonException, Exception {
		return mockMvc.perform(MockMvcRequestBuilders.post(url + "/login").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequest)));
	}
}

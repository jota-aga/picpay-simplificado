package com.jh.picpay_simplificado.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.User;
import com.jh.picpay_simplificado.enums.Roles;
import com.jh.picpay_simplificado.service.TokenService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class TokenServiceUnitTest {
	
	@InjectMocks
	private TokenService tokenService;
	
	@Mock
	private JwtEncoder jwtEncoder;
	
	@Mock
	private Jwt jwt;
	
	@Captor
	private ArgumentCaptor<JwtEncoderParameters> captor;
	
	private User user;
		
	@BeforeEach
	public void setUp() {
		Role role = Role.builder()
				.nome(Roles.COMPRADOR.name())
				.build();
		
		user = User.builder()
				.id(1L)
				.nome("nome")
				.email("email")
				.senha("senha")
				.documento("documento")
				.role(role)
				.build();
		
	}
	
	@Test
	public void generateToken_ShouldReturnToken() {
        when(jwt.getTokenValue()).thenReturn("token-fake");

		when(jwtEncoder.encode(any())).thenReturn(jwt);
		
		String token = tokenService.generateToken(user);
		
		assertEquals("token-fake", token);

        verify(jwtEncoder).encode(captor.capture());

        JwtClaimsSet claims = captor.getValue().getClaims();

        assertEquals(user.getId().toString(), claims.getSubject());
        assertEquals(Roles.COMPRADOR.name(), claims.getClaim("scope"));
	}
}

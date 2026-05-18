package com.jh.picpay_simplificado.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.jh.picpay_simplificado.entity.User;

@Service
public class TokenService {
	@Autowired 
	private JwtEncoder jwtEncoder;
	
	
	public String generateToken(User user) {
		var scope = user.getRole();
		var claims = JwtClaimsSet.builder()
				 .issuer("mybackend")
				 .subject(user.getId().toString())
				 .issuedAt(Instant.now())
				 .expiresAt(this.tokenExpiration())
				 .claim("scope", scope)
				 .build();
		
		var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
		
		return jwtValue;
	}
	
	private Instant tokenExpiration() {
		return LocalDateTime.now()
				.plusHours(2)
				.toInstant(ZoneOffset.of("-03:00"));
	}
}

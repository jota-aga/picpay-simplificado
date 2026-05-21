package com.jh.picpay_simplificado.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.jh.picpay_simplificado.dto.authorization.AuthorizationResponse;

@Component
public class AuthorizationClient {
	
	@Autowired
	private WebClient webClient;
	
	public boolean autorizarTransferencia() {
		AuthorizationResponse authorizationResponse = webClient.get()
				.uri("https://util.devi.tools/api/v2/authorize")
				.retrieve()
				.bodyToMono(AuthorizationResponse.class)
				.block();
				
				return authorizationResponse.data()
						.authorization();
	}
}

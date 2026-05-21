package com.jh.picpay_simplificado.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.jh.picpay_simplificado.dto.authorization.AuthorizationResponse;
import com.jh.picpay_simplificado.exceptions.NotAuthorizedException;

@Component
public class AuthorizationClient {
	
	@Autowired
	private WebClient webClient;
	
	public void autorizarTransferencia() {
		AuthorizationResponse authorizationResponse = webClient.get()
				.uri("https://util.devi.tools/api/v2/authorize")
				.retrieve()
				.bodyToMono(AuthorizationResponse.class)
				.block();
				
		if(!authorizationResponse.data().authorization()) 
			throw new NotAuthorizedException("Serviço autorizador externo não autorizou a transferência");
	}
}

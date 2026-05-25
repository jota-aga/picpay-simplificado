package com.jh.picpay_simplificado.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.jh.picpay_simplificado.dto.authorization.AuthorizationResponse;
import com.jh.picpay_simplificado.exceptions.NotAuthorizedException;

import reactor.core.publisher.Mono;

@Component
public class AuthorizationClient {

	@Autowired
	private WebClient webClient;

	public boolean autorizarTransferencia() {
		AuthorizationResponse authorizationResponse = webClient.get()
				.uri("https://util.devi.tools/api/v2/authorize")
				.retrieve()
				.onStatus(
						status -> status.equals(HttpStatus.FORBIDDEN),
						response -> Mono.error(new NotAuthorizedException("Serviço externo não autorizou a transferência"))
						)
				.bodyToMono(AuthorizationResponse.class).block();
		
		return authorizationResponse.data()
				.authorization();
	}
}

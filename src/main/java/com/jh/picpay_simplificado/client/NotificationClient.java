package com.jh.picpay_simplificado.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class NotificationClient {
	
	private static final Logger logger =
            LoggerFactory.getLogger(NotificationClient.class);
	
	@Autowired
	private WebClient webClient;
	
	public void notificar() {
		try {
			webClient.post()
			.uri("https://util.devi.tools/api/v1/notify")
			.retrieve()
			.toBodilessEntity()
			.block();
		}catch(Exception e) {
			logger.error("Não foi possível enviar notificação: {}", e.getMessage());
		}
	}
}

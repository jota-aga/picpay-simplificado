package com.jh.picpay_simplificado.dto.transferencia;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TransferenciaRequest(
		@NotNull(message = "Valor é obrigatório")
		@Positive(message = "Valor deve ser positivo")
		BigDecimal valor,
		
		@NotNull(message = "Recebedor é obrigatório")
		Long recebedor
		) {}

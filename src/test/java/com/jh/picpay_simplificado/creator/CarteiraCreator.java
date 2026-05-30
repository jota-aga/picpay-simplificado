package com.jh.picpay_simplificado.creator;

import java.math.BigDecimal;

import com.jh.picpay_simplificado.entity.Carteira;

public class CarteiraCreator {
	public static Carteira noIdWith100BalancoAndNoUser() {
		return Carteira.builder()
				.balanco(BigDecimal.valueOf(100))
				.build();
	}
	
	public static Carteira noIdWith0BalancoAndNoUser() {
		return Carteira.builder()
				.balanco(BigDecimal.valueOf(100))
				.build();
	}
}

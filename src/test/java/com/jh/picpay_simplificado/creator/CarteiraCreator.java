package com.jh.picpay_simplificado.creator;

import java.math.BigDecimal;

import com.jh.picpay_simplificado.entity.Carteira;

public class CarteiraCreator {
	public static Carteira carteiraWith100Balanco() {
		return Carteira.builder()
				.balanco(BigDecimal.valueOf(100))
				.build();
	}
	
	public static Carteira carteiraWith0Balanco() {
		return Carteira.builder()
				.balanco(BigDecimal.valueOf(0))
				.build();
	}
}

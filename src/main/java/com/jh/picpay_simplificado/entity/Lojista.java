package com.jh.picpay_simplificado.entity;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.br.CNPJ;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Lojista {
	
	@CNPJ
	private String CNPJ;
	
	private BigDecimal balanco;
	
	@OneToOne(cascade = CascadeType.ALL)
	private User user;
}

package com.jh.picpay_simplificado.entity;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.br.CPF;

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
public class Comprador {
	
	@CPF
	private String CPF;
	
	private BigDecimal balanco;
	
	@OneToOne(cascade = CascadeType.ALL)
	private User user;
}

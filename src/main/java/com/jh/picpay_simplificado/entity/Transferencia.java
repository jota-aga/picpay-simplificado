package com.jh.picpay_simplificado.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.jh.picpay_simplificado.enums.StatusDaTransferencia;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Transferencia {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	private User pagador;
	
	@ManyToOne
	private User recebedor;
	
	private BigDecimal valor;
	
	private LocalDateTime createdAt;
	
	private StatusDaTransferencia status ;
}

package com.jh.picpay_simplificado.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Role {
	@Id
	@GeneratedValue
	private Long id;
	
	private String name;
	
	public enum Value{
		USUARIO,
		LOJISTA;
	}
}

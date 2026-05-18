package com.jh.picpay_simplificado.dto.user;

import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
		@NotBlank(message = "Nome é obrigatório") 
		String nome,
		
		@CPF(message = "CPF inválido")
		String cpf,
		
		@CNPJ(message = "CNPJ inválido")
		String cnpj,
		
		@NotBlank(message = "Email é obrigatório")
		@Email(message = "Email inválido")
		String email,
		
		@Size(min = 8, max = 32, message = "Tamanho dá senha deve ser entre 8 a 32 caracteres")
		@NotBlank(message = "Senha é obrigatório")
		String senha,
		
		@NotBlank(message = "Role é obrigatória")
		String role
		) {

}

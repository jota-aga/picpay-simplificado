package com.jh.picpay_simplificado.creator;

import com.jh.picpay_simplificado.entity.Carteira;
import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.entity.User;

public class UserCreator {
	public static User userWithNoId(Carteira carteira, Role role) {
		return User.builder()
				.nome("nome")
				.email("email")
				.documento("documento")
				.senha("password")
				.carteira(carteira)
				.role(role)
				.build();
	}
	
	public static User userWithId(Long id, Carteira carteira, Role role) {
		return User.builder()
				.id(id)
				.nome("nome")
				.email("email")
				.documento("documento")
				.senha("password")
				.carteira(carteira)
				.role(role)
				.build();
	}
}

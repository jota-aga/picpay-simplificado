package com.jh.picpay_simplificado.creator;

import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.enums.Roles;

public class RoleCreator {
	
	public static Role comprador() {
		return Role.builder()
				.nome(Roles.COMPRADOR.name())
				.build();
	}
	
	public static Role lojista() {
		return Role.builder()
				.nome(Roles.LOJISTA.name())
				.build();
	}
}

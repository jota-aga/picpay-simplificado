package com.jh.picpay_simplificado.configuration;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.enums.Roles;
import com.jh.picpay_simplificado.repository.RoleRepository;

@Component
public class InicializarRoles implements CommandLineRunner{
	
	@Autowired
	private RoleRepository roleRepository;

	@Override
	public void run(String... args) throws Exception {
		Optional<Role> optionalRole = roleRepository.findByNome(Roles.LOJISTA.name());
		
		if(optionalRole.isEmpty()) {
			Role roleUsuario = Role.builder()
					.nome(Roles.COMPRADOR.name())
					.build();
			
			Role roleLojista = Role.builder()
					.nome(Roles.LOJISTA.name())
					.build();
			
			roleRepository.saveAll(List.of(roleUsuario, roleLojista));
		}
		
	}

}

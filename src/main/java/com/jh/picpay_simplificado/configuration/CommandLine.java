package com.jh.picpay_simplificado.configuration;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.jh.picpay_simplificado.entity.Role;
import com.jh.picpay_simplificado.repository.RoleRepository;

@Component
public class CommandLine implements CommandLineRunner{
	
	@Autowired
	private RoleRepository roleRepository;

	@Override
	public void run(String... args) throws Exception {
		Optional<Role> optionalRole = roleRepository.findByName(Role.Value.LOJISTA.name());
		
		if(optionalRole.isEmpty()) {
			Role roleUsuario = Role.builder()
					.name(Role.Value.USUARIO.name())
					.build();
			
			Role roleLojista = Role.builder()
					.name(Role.Value.LOJISTA.name())
					.build();
			
			roleRepository.saveAll(List.of(roleUsuario, roleLojista));
		}
		
	}

}

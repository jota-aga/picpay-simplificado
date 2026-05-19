package com.jh.picpay_simplificado.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jh.picpay_simplificado.entity.Comprador;
import com.jh.picpay_simplificado.entity.User;

public interface CompradorRepository extends JpaRepository<Comprador, Long>{
	Optional<Comprador> findByCPF(String cpf);

	Optional<Comprador> findByUser(User user);
}	

package com.jh.picpay_simplificado.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jh.picpay_simplificado.entity.Carteira;

public interface CarteiraRepository extends JpaRepository<Carteira, Long>{
	
	Optional<Carteira> findByUserId(Long id);
}

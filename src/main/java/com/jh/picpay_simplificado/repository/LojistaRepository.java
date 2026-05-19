package com.jh.picpay_simplificado.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jh.picpay_simplificado.entity.Lojista;
import com.jh.picpay_simplificado.entity.User;

public interface LojistaRepository extends JpaRepository<Lojista, Long>{

	Optional<Lojista> findByCNPJ(String CNPJ);

	Optional<Lojista> findByUser(User user);

}

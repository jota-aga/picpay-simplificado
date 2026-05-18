package com.jh.picpay_simplificado.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jh.picpay_simplificado.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

}

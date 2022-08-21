package dev.procheck.capweb.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.procheck.capweb.entities.Client;

public interface ClientRepository extends JpaRepository<Client, String>{
	
}

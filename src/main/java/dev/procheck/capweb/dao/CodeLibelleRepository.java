package dev.procheck.capweb.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.procheck.capweb.entities.CodeLibelle;

public interface CodeLibelleRepository extends JpaRepository<CodeLibelle, Integer>{

	@Query("SELECT l from CodeLibelle l WHERE l.code = :x")
	public CodeLibelle getLibelleByCode(@Param("x") String code);
	
	
}

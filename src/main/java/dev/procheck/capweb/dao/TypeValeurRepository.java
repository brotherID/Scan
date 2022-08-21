package dev.procheck.capweb.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.procheck.capweb.entities.TypeValeur;

public interface TypeValeurRepository extends JpaRepository<TypeValeur, String>{
	@Query("SELECT typeValeur.idTypeValeur from TypeValeur typeValeur where typeValeur.type = :typeValeur")
	public String findByTypeValeur(@Param("typeValeur")String typeValeur);
	@Query("SELECT typeValeur.idTypeValeur from TypeValeur typeValeur")
	List<String>  findByIdTypeValeur();
}

package dev.procheck.capweb.dao;


import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.procheck.capweb.entities.Valeur;

public interface ValeurRepository extends JpaRepository<Valeur, String>{

	
	@Query("SELECT d from Valeur d WHERE d.idValeur = :x")
	public List<Valeur> getValeurById(@Param("x") String id);
	
	@Query("SELECT d from Valeur d WHERE d.cmc7 = :x")
	public List<Valeur> getValeurByCmc7(@Param("x") String cmc7);
	
	@Query("SELECT d from Valeur d WHERE d.cmc7 = :x and d.remise.capturePoint=:y and convert(date,d.remise.dateRemise) = :z")
	public List<Valeur> findAllByCmc7AndCapturePointAndDateRemise(@Param("x")String cmc7,@Param("y")String capturePoint,@Param("z")Date dateRemise);
	
}

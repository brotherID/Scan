package dev.procheck.capweb.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.procheck.capweb.entities.ParamRefRemise;

public interface ParamRefRemiseRepository extends JpaRepository<ParamRefRemise, String>{
	@Query("SELECT paramRefRemise.formule from ParamRefRemise paramRefRemise where paramRefRemise.idParamRefRemise = :idParamRefRemise")
	public String findFormuleByidParamRefRemise(@Param("idParamRefRemise")String idParamRefRemise);
	
	@Query("SELECT paramRefRemise from ParamRefRemise paramRefRemise where paramRefRemise.formule = :formule and paramRefRemise.label = :formule")
	public ParamRefRemise findByFormuleAndLabel(@Param("formule") String formule);
}

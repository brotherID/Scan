package dev.procheck.capweb.dao;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.procheck.capweb.entities.CompteGr;

public interface CompteGrRepository extends JpaRepository<CompteGr, String>{
	
	public CompteGr findByRibRemettant(String rib);
	
	public CompteGr findByRibRemettantAndUser_IdUser(String rib,String idUser);
	
	public List<CompteGr> findAllByIsActiveAndUser_IdUser(boolean isActive,String idUser);
	
	@Query(value ="select DISTINCT  [s_id_param_ref_remise] from [dbo].[t_compte_gr] where [s_id_user] = :idUser",nativeQuery = true)
	public String findIdParamRefRemiseByIdUser(@Param("idUser")String idUser);
	
	@Query(value ="select [s_nom_remettant] from [dbo].[t_compte_gr] where [s_id_user] = :idUser",nativeQuery = true)
	public List<String> findNomRemettantByIdUser(@Param("idUser")String idUser);
	
	@Query(value ="select * from [dbo].[t_compte_gr] where [s_id_user] = :idUser",nativeQuery = true)
	public List<CompteGr> findByIdUser(@Param("idUser")String idUser);
	
}

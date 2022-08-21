package dev.procheck.capweb.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.procheck.capweb.entities.TypeRemise;

public interface TypeRemiseRepository extends JpaRepository<TypeRemise, String>{
	@Query("SELECT typeRemise from TypeRemise typeRemise WHERE typeRemise.type not like %:type")
	List<TypeRemise> findAllByType(@Param("type")String type);
	@Query("SELECT typeRemise.idTypeRemise from TypeRemise typeRemise")
	List<String>  findByIdTypeRemise();
    @Query(value = "select * from [dbo].[t_type_remise] where [s_type_valeur] = :idTypeValeur", nativeQuery = true) 
    List<TypeRemise> findAllByIdTypeValeur(@Param("idTypeValeur")String idTypeValeur);
}

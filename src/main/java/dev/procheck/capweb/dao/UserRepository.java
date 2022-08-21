package dev.procheck.capweb.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.procheck.capweb.entities.User;

public interface UserRepository extends JpaRepository<User, String>{

	@Query("SELECT u from User u WHERE LOWER(u.userLogin) = :x AND u.userPassword = :y")
	public List<User> getUserByIdentifiants(@Param("x") String userlogin, @Param("y") String password);
	
	@Query("SELECT u from User u WHERE u.userLogin = :x")
	public User getUserByUserLogin(@Param("x") String user_login);
	
	@Query("SELECT u from User u WHERE u.capturePoint = :x")
	public User getUserByCapturePoint(@Param("x") String capture_point);
	
	public Page<User> findAllByRoleIsNotAndCapturePointLikeAndIsGrTrue(String role,String pc,Pageable pageable);
	
	public Page<User> findAllByRoleIsNotAndIsGrFalseOrIsGrIsNull(String role,Pageable pageable);
	
	
	
	@Query(value ="select [s_id_param_ref_remise]  from [dbo].[t_users] where [s_id_user] = :idUser", nativeQuery = true)
	public String findIdParamRefRemiseByIdUser(@Param("idUser") String idUser);
	
	
	
	
}

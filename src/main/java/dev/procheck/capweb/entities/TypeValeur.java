package dev.procheck.capweb.entities;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "T_TYPE_VALEUR")
@Data @NoArgsConstructor @AllArgsConstructor
public class TypeValeur implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(length = 24, name ="s_id_type_valeur")
	private String idTypeValeur;
	
	@Column(length = 3, name ="s_type")
	private String type;
	
	@Column(length = 10, name ="s_type_long")
	private String typeLong;
	
	@ManyToMany(mappedBy = "typeValeurs")
	private Collection<User> users;
	
	
	

	
}

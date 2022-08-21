package dev.procheck.capweb.entities;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "T_CLIENT")
@Data @NoArgsConstructor @AllArgsConstructor
public class Client implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 24, name ="s_id_client")
	private String idClient;
	
	@Column(length = 3, name ="s_code")
	private String code;
	
	@Column(length = 24, name ="s_short_name")
	private String shortName;
	
	@Column(length = 128, name ="s_long_name")
	private String longName;
	
	@OneToMany(mappedBy = "client")
	private Collection<User> users;
	
	
}

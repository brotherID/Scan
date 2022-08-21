package dev.procheck.capweb.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "T_CODE_LIBELLE")
@Data @NoArgsConstructor @AllArgsConstructor
public class CodeLibelle {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(length = 24, name ="n_id_codelibelle")
	public int idCodeLibelle;
	
	@Column(length = 24, name ="s_code")
	public String code;
	
	@Column(length = 124, name ="s_libelle")
	public String libelle;
	
}

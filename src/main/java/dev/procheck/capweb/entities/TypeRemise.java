package dev.procheck.capweb.entities;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "T_TYPE_REMISE")
@Data @NoArgsConstructor @AllArgsConstructor
public class TypeRemise implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@Column(length = 24, name ="s_id_type_remise")
	private String idTypeRemise;
	
	@Column(length = 2, name ="s_code")
	private String code;
	
	@Column(length = 24, name ="s_type")
	private String type;
	
	@Column(length = 64, name ="s_type_long")
	private String typeLong;
	
	@Column(length = 3, name ="s_bank_code")
	private String sBankCode;
	
	@OneToOne
	@JoinColumn(name = "s_type_valeur")
	private TypeValeur typeValeur;
}

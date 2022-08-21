package dev.procheck.capweb.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "T_COMPTE_GR")
@Data @NoArgsConstructor @AllArgsConstructor
public class CompteGr implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(length = 24, name ="s_id_compte_gr")
	private String idCompteGr;
	
	@Column(length = 24, name = "s_rib_remettant")
	private String ribRemettant;

	@Column(length = 64, name = "s_nom_remettant")
	private String nomRemettant;
	
	@Column(name = "n_is_active")
	private Boolean isActive;
	
	@ManyToOne
	@JoinColumn(name = "s_id_user")
	@JsonProperty(access = Access.WRITE_ONLY)
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "s_id_param_ref_remise")
	@JsonProperty(access = Access.WRITE_ONLY)
	private ParamRefRemise paramRefRemise;
	
//	@Column(name = "n_sequenceDay",columnDefinition = "integer default 0")
//	private Integer sequenceDay;
//	
//	@Column(name = "n_sequence",columnDefinition = "integer default 0")
//	private Integer sequence;
//	
//	@Column(name = "s_sys_Date", length = 14)
//	private String sysDate;
}
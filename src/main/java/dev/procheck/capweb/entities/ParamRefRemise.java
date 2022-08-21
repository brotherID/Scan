package dev.procheck.capweb.entities;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "T_PARAM_REF_REMISE")
@Data @NoArgsConstructor @AllArgsConstructor
public class ParamRefRemise implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(length = 24, name ="s_id_param_ref_remise")
	private String idParamRefRemise;
	
	@Column(name = "n_is_auto")
	private Boolean isAuto;
	
	@Column(name = "n_is_numerique")
	private Boolean isNumerique;
	
	@Column(name = "n_min_length")
	private int minLength;
	
	@Column(name = "n_max_length")
	private int maxLength;
	
	@Column(name = "s_label")
	private String label;
	
	@Column(name = "s_formule")
	private String formule;
	
	@OneToMany(mappedBy = "paramRefRemise")
	private Collection<CompteGr> compteGrs ;
	
	@Column(name = "s_sequence_day",columnDefinition = "integer default 0")
	private Integer sequenceDay;
	@Column(name = "s_sequence",columnDefinition = "integer default 0")
	private Integer sequence;
	@Column(name = "s_date_traitement", length = 14)
	private String  dateTraitement;
}

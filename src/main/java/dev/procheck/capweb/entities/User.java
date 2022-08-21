package dev.procheck.capweb.entities;

import java.io.Serializable;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="T_USERS")
@Data @NoArgsConstructor @AllArgsConstructor
public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(length = 24, name ="s_id_user")
	private String idUser;

	@Column(length = 3, name ="s_bank_code")
	private String bankCode;
	
	@Column(length = 24, name ="s_id_login")
	private String userLogin;
	
	@Column(length = 64, name ="s_user_name")
	private String userName;
	
	@Column(length = 64, name ="s_user_password")
	private String userPassword;
	
	@Column(length = 5, name ="s_user_capture_point")
	private String capturePoint;
	
	@Column(name = "n_is_actif")
	private int isActif;
	
	@Column(name = "n_nb_fail_password")
	private int nbFailPassword;
	
	@Column(name = "s_roles")
	private String role;
	
	@Column(name = "n_cree_remise")
	private Boolean creeRemise;
	
	@Column(name = "n_is_gr")
	private Boolean isGr;
	
	@Column(name = "n_is_password_changed")
	private Boolean isPasswordChanged;
	
	@Column(name = "n_seq")
	private Integer sequence;
	
	
	@OneToMany(mappedBy = "user")
	private Collection<UserHisto> histo;
	
	@ManyToOne
	@JoinColumn(name = "s_id_client")
	private Client  client;
	
	@OneToMany(mappedBy = "user")
	private Collection<CompteGr> compteGrs ;
	
	@OneToMany(mappedBy = "user")
	private Collection<Remise> remises ;
	
	@ManyToOne
	@JoinColumn(name = "s_id_param_ref_remise")
	private ParamRefRemise  paramRefRemise;
	
	
	
	@ManyToMany
	@JoinTable(
			  name = "R_PARAM_TYPE_VALEUR", 
			  joinColumns = @JoinColumn(name = "s_id_user"), 
			  inverseJoinColumns = @JoinColumn(name = "s_id_type_valeur"))
	private Collection<TypeValeur> typeValeurs;
	
	
	@ManyToMany
	@JoinTable(
			  name = "R_PARAM_TYPE_REMISE", 
			  joinColumns = @JoinColumn(name = "s_id_user"), 
			  inverseJoinColumns = @JoinColumn(name = "s_id_type_remise"))
	private Collection<TypeRemise> typeRemises;


	public User(String idUser, String bankCode, String userLogin, String userName, String userPassword,
			String capturePoint, String role) {
		this.idUser = idUser;
		this.bankCode = bankCode;
		this.userLogin = userLogin;
		this.userName = userName;
		this.userPassword = userPassword;
		this.capturePoint = capturePoint;
		this.role = role;
	}
}

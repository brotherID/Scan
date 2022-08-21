package dev.procheck.capweb.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "T_REMISE")
@Data @NoArgsConstructor @AllArgsConstructor
public class Remise implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 24, name ="s_id_remise")
	private String idRemise;
	
	@Column(length = 24, name = "s_intern_id")
	private String internId;

	@Column(length = 3, name = "s_bank_code")
	private String bankCode;

	@Column(length = 5, name = "s_capture_point")
	private String capturePoint;

	@Column(length = 3, name = "s_scanner_code")
	private String scanner_code;
	
	@Column(name = "d_date_remise")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date dateRemise;

	@Column(length = 50, name = "s_user_name")
	private String userName;

	@Column(length = 24, name = "s_rib_remettant")
	private String ribRemettant;

	@Column(length = 64, name = "s_nom_remettant")
	private String nomRemettant;

	@Column(length = 32, name = "s_type_remise")
	private String typeRemise;

	@Column(length = 10, name = "s_type_valeur")
	private String typeValeur;

	@Column(length = 16, name = "s_reference_remise")
	private String referenceRemise;

	@Column(length = 24, name = "s_reference_client")
	private String referenceClient;
	
	@Column(precision = 19, scale = 2, name = "n_montant_remise")
	@Type(type = "big_decimal")
	private BigDecimal montantRemise;

	@Column(name = "n_nombre_valeur")
	private int nombreValeur;
	
	@Column(name = "n_status")
	private int status;
	
	@Column(name = "d_scan_date")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date scanDate;
	
	@Column(name = "n_scan_nb_valeur")
	private int scanNbValeur;

	@Column(length = 128, name = "s_scan_pdf_url")
	private String scanPdfUrl;
	
	@Column(name = "d_export_date")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date exportDate;

	@Column(length = 64, name = "s_package_name")
	private String packageName;
	
	@Column(length = 24, name = "s_adresse_ip")
	private String adresseIp;
	
	@Column(name = "d_cancel_date")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date cancelDate;
	
	
	@Column(precision = 19, scale = 2, name = "n_taux_escompte")
	@Type(type = "big_decimal")
	private BigDecimal tauxEscompte;
	
	@Column(name = "s_sys_DateTime", length = 14)
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "yyyyMMddHHmmss")
	private Date sysDateTime;

	@Column(length = 64, name = "s_token")
	private String token;
	
	@Column(length = 64, name = "s_nom_remise")
	private String nomRemise;
	
	@Column(length = 64, name = "s_nom_compte")
	private String nomCompte;
	
	@Column(name = "s_version_app")
	private String versionApp;
	
	
	@OneToMany(mappedBy="remise")
	private Collection<Valeur> valeurs;
	
	@ManyToOne
	@JoinColumn(name = "s_id_user")
	private User user;
	

}

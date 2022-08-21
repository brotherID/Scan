package dev.procheck.capweb.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "T_VALEUR")
@Data @NoArgsConstructor @AllArgsConstructor
public class Valeur implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 24, name ="s_id_valeur")
	private String idValeur;
	
	@Column(length = 64, name = "s_cmc7")
	private String cmc7;
	
	@Column(length = 64, name = "s_endos")
	private String endos;

	@Column(precision = 19, scale = 2, name = "n_montant")
	@Type(type = "big_decimal")
	private BigDecimal montant;
	
	@Column(length = 128, name = "s_url_img_f")
	private String urlVerso;
	
	@Column(length = 128, name = "s_url_img_r")
	private String urlRecto;
	
	@Column(name = "d_scan_date")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date scanDate;
	
	@ManyToOne
	@JoinColumn(name = "s_id_remise")
	private Remise remise;
	
}

package dev.procheck.capweb.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="T_USERS_HISTO")
@Data @NoArgsConstructor @AllArgsConstructor
public class UserHisto implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(length = 24, name ="s_id_user_histo")
	private String idHistoUser;
	
	@Column(name = "s_date_action")
	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
	private Date dateAction;
	
	@Column(length = 24, name = "s_action")
	private String action;
	
	@Column(length = 128, name = "s_memo")
	private String memo;
	
	@ManyToOne
	@JoinColumn(name="s_id_user")
	private User user;

}

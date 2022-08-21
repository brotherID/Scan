package dev.procheck.capweb.controllers;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import dev.procheck.capweb.common.Methodes;
import dev.procheck.capweb.common.PageWrapper;
import dev.procheck.capweb.common.reference.remise.generator.ReferenceRemiseGenerated;
import dev.procheck.capweb.common.reference.remise.generator.ReferenceRemiseGeneratorEngine;
import dev.procheck.capweb.dao.CodeLibelleRepository;
import dev.procheck.capweb.dao.CompteGrRepository;
import dev.procheck.capweb.dao.RemiseRepository;
import dev.procheck.capweb.dao.UserHistoRepository;
import dev.procheck.capweb.dao.UserRepository;
import dev.procheck.capweb.dao.ValeurRepository;
import dev.procheck.capweb.entities.CodeLibelle;
import dev.procheck.capweb.entities.Remise;
import dev.procheck.capweb.entities.TypeRemise;
import dev.procheck.capweb.entities.User;
import dev.procheck.capweb.entities.UserHisto;
import dev.procheck.capweb.entities.Valeur;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;
import procheck.dev.pkcore.bean.PKConnect;
import procheck.dev.pkcore.common.CommonData;
import procheck.dev.pkcore.security.PKSecurity;

@RestController
@Slf4j
public class RemiseController {

	@Autowired
	private RemiseRepository remiseRepository;

	@Autowired
	private ValeurRepository valeurRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserHistoRepository userHistoRepository;

	@Autowired
	private CodeLibelleRepository codeLibelleRepository;

	@Autowired
	private CompteGrRepository compteGrRepository;

	@Value("${pathToServer}")
	private String pathToServer;

	@Value("${key}")
	private String key;

	@Value("${nombreJour}")
	private int nombreJour;
	
	@Autowired
	private ReferenceRemiseGeneratorEngine referenceRemiseGeneratorEngine;


	@GetMapping(value = "/remises")
	public ModelAndView listeRemises(HttpServletRequest req, HttpServletResponse res, Model model,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "motCle", defaultValue = "") String motCle,
			@RequestParam(name = "dateScanRemise", defaultValue = "") String dateScanRemise,
			@RequestParam(name = "typeDate", defaultValue = "dateScan") String typeDate,
			@RequestParam(name = "typeValeur", defaultValue = "") String typeValeur,
			@RequestParam(name = "action", defaultValue = "") String action,
			@RequestParam(name = "size", defaultValue = "20") int size)
			throws ParseException, ServletException, IOException {
		Connection cnxToDb = null;
		cnxToDb = PKConnect.getCnxToDb();
		if (!PKSecurity.isUserAuth(req, res, CommonData.PKEXPLORER_APP_CODE, cnxToDb)) {
			try {
				cnxToDb.close();
			} catch (Exception e) {
				log.error("Error ", e);
				// TODO: handle exception
			}
			res.sendRedirect(res.encodeRedirectURL("/PKAuth"));
			return null;
		} else {
			try {
				cnxToDb.close();
			} catch (SQLException e) {
			}
		}

		String id_user = (String) req.getSession().getAttribute("userid");
		if (id_user != null) {

			User u = userRepository.findById(id_user).get();

			log.info("Visualisation des remises de la page: " + page + " avec le filter:[" + motCle + "," + typeDate
					+ "] par l'utilisateur: " + u.getUserName());

			int randomHisto = Methodes.GenerateRandom();
			String idHisto = Methodes.getId() + (String.valueOf(randomHisto).substring(0, 2));
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			String stringSdt = sdf.format(new Date());
			Date date_action = sdf.parse(stringSdt);
			UserHisto userHisto = new UserHisto(idHisto, date_action, "Afficher d'autres remise",
					"Visualisation des remises de la page " + page + " de la pagination avec le filter:[" + motCle + ","
							+ typeDate + "]",
					u);
			userHistoRepository.save(userHisto);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			if (dateScanRemise == null || dateScanRemise.equals(""))
				dateScanRemise = dateFormat.format(new Date());
			Date dateScanRemiseD = null;
			try {
				dateScanRemiseD = dateFormat.parse(dateScanRemise);
			} catch (ParseException e) {
				dateScanRemise = null;
			}

			Page<Remise> remises;
			PageWrapper<Remise> pageRemises;
			/*
			 * if(action.equals("bordereau")) { if(u.getRole().equals("ADMIN")) { remises =
			 * ; } else { remises = ; } pageRemises = new PageWrapper<Remise>(remises,
			 * "/remises"); }
			 */
			// else {
			if (u.getRole().equals("ADMIN")) {
				if (typeDate.equals("dateRemise"))
					remises = remiseRepository.findAllSortByIdRemiseAndMotCleAndDateRemise(typeValeur, motCle,
							dateScanRemiseD, PageRequest.of(page, size, Sort.by("idRemise").descending()));
				else
					remises = remiseRepository.findAllSortByIdRemiseAndMotCleAndDateScan(typeValeur, motCle,
							dateScanRemiseD, PageRequest.of(page, size, Sort.by("idRemise").descending()));
				pageRemises = new PageWrapper<Remise>(remises, "/remises");
			} else {
				if (typeDate.equals("dateRemise"))
					remises = remiseRepository.getRemiseByCapturePointAndMotCleAndDateRemise(u.getBankCode(),
							typeValeur, u.getCapturePoint(), motCle, dateScanRemiseD,
							PageRequest.of(page, size, Sort.by("idRemise").descending()));
				else
					remises = remiseRepository.getRemiseByCapturePointAndMotCleAndDateScan(u.getBankCode(), typeValeur,
							u.getCapturePoint(), motCle, dateScanRemiseD,
							PageRequest.of(page, size, Sort.by("idRemise").descending()));
				pageRemises = new PageWrapper<Remise>(remises, "/remises");
			}
			// }

			model.addAttribute("page", pageRemises);
			model.addAttribute("currentNumber", page);
			model.addAttribute("totalElements", remises.getTotalElements());
			model.addAttribute("remises", remises.getContent());
			model.addAttribute("motCle", motCle);
			model.addAttribute("dateScanRemise", dateScanRemise);
			model.addAttribute("typeDate", typeDate);
			model.addAttribute("typeValeur", typeValeur);
			model.addAttribute("nombreJour", nombreJour);

			model.addAttribute("currentUser", u);
			model.addAttribute("compteGrs", compteGrRepository.findAllByIsActiveAndUser_IdUser(true, u.getIdUser()));
			return new ModelAndView("numeriser");
		} else {

			return new ModelAndView("redirect:/login");
		}
	}

	@RequestMapping(value = "/scannedRemise", method = RequestMethod.GET)
	public List<String> scannedRemise(HttpServletRequest req, Model model) throws IOException, ParseException {

		try {
			String id_user = (String) req.getSession().getAttribute("userid");
			if (id_user != null) {
				String id_remise = req.getParameter("idRemise");
				Remise remise = remiseRepository.getRemiseById(id_remise).get(0);

				log.info("Consultation des valeurs scannées de la remise: " + id_remise);

				List<String> base64Doc = new ArrayList<String>();
				Collection<Valeur> valeurs = remise.getValeurs();
				for (Valeur v : valeurs) {
					File rectoFile = new File(v.getUrlRecto());
					File versoFile = new File(v.getUrlVerso());
					byte[] rectoContent = Files.readAllBytes(rectoFile.toPath());
					byte[] versoContent = Files.readAllBytes(versoFile.toPath());
					String rectoBase64 = Base64.encodeBase64String(rectoContent);
					String versoBase64 = Base64.encodeBase64String(versoContent);
					base64Doc.add(rectoBase64);
					base64Doc.add(versoBase64);
				}
				return base64Doc;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Erreur lorsque la consultation des valeurs scannées-->", e);

		}
		return null;

	}

	@RequestMapping(value = "/scannedValeur", method = RequestMethod.GET)
	public List<String> getValeurs(HttpServletRequest req) throws IOException {
		try {
			String id_user = (String) req.getSession().getAttribute("userid");
			if (id_user != null) {
				String idValeur = req.getParameter("idValeur");
				Valeur valeur = valeurRepository.getValeurById(idValeur).get(0);
				String type = req.getParameter("type");

				log.info("Visualisation du: " + type + " de la valeur: " + idValeur + " ");

				List<String> base64Doc = new ArrayList<String>();

				if (type.equals("recto")) {
					File rectoFile = new File(valeur.getUrlRecto());
					byte[] rectoContent = Files.readAllBytes(rectoFile.toPath());
					base64Doc.add(Base64.encodeBase64String(rectoContent));
				} else {
					File versoFile = new File(valeur.getUrlVerso());
					byte[] versoContent = Files.readAllBytes(versoFile.toPath());
					base64Doc.add(Base64.encodeBase64String(versoContent));
				}

				return base64Doc;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Erreur lorsque la visualisation-->", e);

		}
		return null;

	}

	@PostMapping(value = "/addRemise")
	public ModelAndView addRemise(HttpServletRequest req, HttpServletResponse res, Model model,
			RedirectAttributes redirectAttrs) throws ParseException, IOException {
		Connection cnxToDb = null;
		cnxToDb = PKConnect.getCnxToDb();
		if (!PKSecurity.isUserAuth(req, res, CommonData.PKEXPLORER_APP_CODE, cnxToDb)) {
			try {
				cnxToDb.close();
			} catch (Exception e) {
				log.error("Error ", e);
				// TODO: handle exception
			}
			res.sendRedirect(res.encodeRedirectURL("/PKAuth"));
			return null;
		} else {
			try {
				cnxToDb.close();
			} catch (SQLException e) {
			}
		}
		log.info("Demande de scan par addRemise");
		try {
			Enumeration<String> listNameParam = req.getParameterNames();
			log.info("List parametre envoyer------------------");
			while (listNameParam.hasMoreElements()) {
				String nameParam = listNameParam.nextElement();
				log.info(nameParam + ":" + req.getParameter(nameParam));
			}
			log.info("---------------------------------------");
		} catch (Exception ex) {
			log.error("ERROR GET PARAM:", ex);
		}

		InetAddress ipHost = InetAddress.getLocalHost();
		String ip = ipHost.getHostAddress();

		String id = Methodes.getId();
		int random = Methodes.GenerateRandom();
		String randomString = (String.valueOf(random)).substring(0, 2);
		String capture_point = req.getParameter("cdf");
		String id_remise = id + capture_point + randomString;

		Date dateRemise = new Date();

		String ribRemettant = req.getParameter("ribRemettant");
		String nomRemettant = req.getParameter("nomRemettant");
		String referenceRemise = req.getParameter("referenceRemise");
		Double montantRemise = Double.parseDouble(req.getParameter("montantRemise"));
		BigDecimal bdMontant = BigDecimal.valueOf(montantRemise.doubleValue());
		int nombre_valeur = Integer.parseInt(req.getParameter("nombreValeur"));
		String type_valeur = req.getParameter("typeValeur");
		String type_remise = req.getParameter("typeRemise");
		BigDecimal bdEscompte = new BigDecimal(0.00);

		int status = 0;

		String id_user = req.getSession().getAttribute("userid").toString();
		User u = userRepository.findById(id_user).get();
		String username = u.getUserName();
		String bank_code = u.getBankCode();

		Date sysDateTime = new Date();

		int randomHisto = Methodes.GenerateRandom();
		String idHisto = Methodes.getId() + (String.valueOf(randomHisto).substring(0, 2));

		Date date_action = new Date();

		try {
			UserHisto userHisto = new UserHisto(idHisto, date_action, "Ajout remise", "Ajout d'une nouvelle remise", u);
			userHistoRepository.save(userHisto);

			// Ajouter la remise à tout les users qui ont le pmoint de capture
			Remise remise = new Remise();
			remise.setIdRemise(id_remise);
			remise.setBankCode(bank_code);
			remise.setCapturePoint(capture_point);
			remise.setDateRemise(dateRemise);
			remise.setUserName(username);
			remise.setRibRemettant(ribRemettant);
			remise.setNomRemettant(nomRemettant);
			remise.setTypeRemise(type_remise);
			remise.setTypeValeur(type_valeur);
			remise.setReferenceRemise(referenceRemise);
			remise.setMontantRemise(bdMontant);
			remise.setNombreValeur(nombre_valeur);
			remise.setStatus(status);
			remise.setAdresseIp(ip);
			remise.setSysDateTime(sysDateTime);
			remise.setTauxEscompte(bdEscompte);
			remise.setScanDate(sysDateTime);
			u.setSequence(u.getSequence() == null || u.getSequence() == 0 ? 2 : (u.getSequence() + 1) % 100);
			remise.setUser(u);
			remise.setToken("");
			remiseRepository.save(remise);
			model.addAttribute("remise", remise);
			//
			String referenceRemiseUpdated = req.getParameter("referenceRemiseUpdated");
			log.info("referenceRemiseUpdated {}",referenceRemiseUpdated);
			String referenceRemiseOld = req.getParameter("referenceRemiseOld");
			log.info("referenceRemiseOld {}",referenceRemiseOld);
			model.addAttribute("referenceRemiseUpdated", referenceRemiseUpdated);
			model.addAttribute("referenceRemiseOld", referenceRemiseOld);
			log.info("referenceRemise {}",referenceRemise);
			model.addAttribute("referenceRemise", referenceRemise);

			log.info("Ajout de la remise: " + id_remise + " par l'utilisateur: " + u.getUserName()
					+ " depuis l'adresse: " + ip);

			return new ModelAndView("scan");
		} catch (DataIntegrityViolationException e) {
			log.error("erreur add remise:", e);
			redirectAttrs.addFlashAttribute("erreurmessage",
					"La référence remise " + referenceRemise + " existe déjà dans cette journée");
			return new ModelAndView("redirect:/remises");
		} catch (Exception e) {
			log.error("erreur:", e);
			return new ModelAndView("redirect:/remises");
		}
	}

	@RequestMapping(value = "/deleteRemise", method = RequestMethod.POST)
	public ModelAndView deleteRemise(HttpServletRequest req, HttpServletResponse res, Model model,
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "6") int size) throws ParseException, IOException {
		Connection cnxToDb = null;
		cnxToDb = PKConnect.getCnxToDb();
		if (!PKSecurity.isUserAuth(req, res, CommonData.PKEXPLORER_APP_CODE, cnxToDb)) {
			try {
				cnxToDb.close();
			} catch (Exception e) {
				log.error("Error ", e);
				// TODO: handle exception
			}
			res.sendRedirect(res.encodeRedirectURL("/PKAuth"));
			return null;
		} else {
			try {
				cnxToDb.close();
			} catch (SQLException e) {
			}
		}
		InetAddress ipHost = InetAddress.getLocalHost();
		String ip = ipHost.getHostAddress();

		String id_remise = req.getParameter("id_remise_delete");
		Remise remise = remiseRepository.getRemiseById(id_remise).get(0);

		String id_user = req.getSession().getAttribute("userid").toString();
		User u = userRepository.findById(id_user).get();

		int randomHisto = Methodes.GenerateRandom();
		String idHisto = Methodes.getId() + (String.valueOf(randomHisto).substring(0, 2));
		Date date_action = new Date();
		UserHisto userHisto = new UserHisto(idHisto, date_action, "Suppression remise",
				"Suppression de la remise: " + id_remise, u);
		userHistoRepository.save(userHisto);

		remiseRepository.delete(remise);

		log.info("Suppresion de la remise: " + id_remise + " par l'utilisateur: " + u.getUserName()
				+ " depuis l'adresse: " + ip);

		return new ModelAndView("redirect:/remises");
	}

	@RequestMapping(value = "/scanCheque", method = RequestMethod.POST)
	public ModelAndView scanCheque(HttpServletRequest req, HttpServletResponse res, Model model)
			throws IOException, ParseException {

		log.info("Debut scanCheque");
		Connection cnxToDb = null;
		cnxToDb = PKConnect.getCnxToDb();
		if (!PKSecurity.isUserAuth(req, res, CommonData.PKEXPLORER_APP_CODE, cnxToDb)) {
			try {
				cnxToDb.close();
			} catch (Exception e) {
				log.error("Error ", e);
				// TODO: handle exception
			}
			res.sendRedirect(res.encodeRedirectURL("/PKAuth"));
			return null;
		} else {
			try {
				cnxToDb.close();
			} catch (SQLException e) {
			}
		}
		String cmc7 = req.getParameter("cmc7");
		String endos = req.getParameter("endos");
		String recto = req.getParameter("recto");
		String verso = req.getParameter("verso");
		String id_remise = req.getParameter("idRemise");
		String version_app = req.getParameter("version_app");

		Remise remise = remiseRepository.findById(id_remise).get();

		log.info("Cmc7: " + cmc7 + "-Endos:" + endos);
		List<Valeur> valeurs = valeurRepository.findAllByCmc7AndCapturePointAndDateRemise(cmc7,
				remise.getCapturePoint(), remise.getDateRemise());
		if (valeurs.isEmpty() || cmc7.contains("?") || cmc7.trim().equals("")) {

			int randomNumber = Methodes.GenerateRandom();
			String rectoPath = pathToServer + randomNumber + "Recto.jpg";

			if (!recto.isEmpty()) {
				byte[] rectoArray = Base64.decodeBase64(recto);
				try (OutputStream out = new BufferedOutputStream(new FileOutputStream(rectoPath))) {
					out.write(rectoArray);
					out.close();
				} catch (Exception e) {
					log.error("Error: " + e + " write DB du Recto la valeur avec cmc7: " + cmc7 + " de la remise: "
							+ id_remise);
				}
			} else {
				log.warn("Recto is empty pour la remise: " + id_remise);
			}

			String versoPath = pathToServer + randomNumber + "Verso.jpg";

			if (!verso.isEmpty()) {
				byte[] versoArray = Base64.decodeBase64(verso);
				try (OutputStream out = new BufferedOutputStream(new FileOutputStream(versoPath))) {
					out.write(versoArray);
					out.close();
				} catch (Exception e) {
					log.error("Error: " + e + ", write DB du Verso la valeur avec cmc7: " + cmc7 + " de la remise: "
							+ id_remise);
				}
			} else {
				log.warn("Verso is empty pour la remise: " + id_remise);
			}

			String id = Methodes.getId();
			int random = Methodes.GenerateRandom();
			String randomString = (String.valueOf(random)).substring(0, 2);

			String idValeur = id + randomString;

			Date scan_date = new Date();

			// Changer le montant de la valeur
			Double montant = 0.0;
			BigDecimal bdMontant = BigDecimal.valueOf(montant.doubleValue());

			Valeur valeur = new Valeur(idValeur, cmc7, endos, bdMontant, versoPath, rectoPath, scan_date, remise);
			remise.setScanNbValeur(remise.getScanNbValeur() + 1);
			remise.setVersionApp(version_app);
			valeurRepository.save(valeur);
			model.addAttribute("scanned", 1);

			log.info("Scan de la valeur: " + idValeur + " de la remise: " + id_remise + " est effectué avec succès");

		} else {
			model.addAttribute("scanned", 0);
			log.info("La valeur avec le cmc7: " + cmc7 + " est déja scannée dans la journée");
		}

		model.addAttribute("remise", remise);
		model.addAttribute("valeurs", remise.getValeurs());

		return new ModelAndView("scan");
	}

	@GetMapping(value = "/addRemise")
	public ModelAndView scanFromUrl(HttpServletRequest req, HttpServletResponse res, Model model,
			RedirectAttributes redirectAttrs) throws IOException {
		Connection cnxToDb = null;
		cnxToDb = PKConnect.getCnxToDb();
		if (!PKSecurity.isUserAuth(req, res, CommonData.PKEXPLORER_APP_CODE, cnxToDb)) {
			try {
				cnxToDb.close();
			} catch (Exception e) {
				log.error("Error ", e);
				// TODO: handle exception
			}
			res.sendRedirect(res.encodeRedirectURL("/PKAuth"));
			return null;
		} else {
			try {
				cnxToDb.close();
			} catch (SQLException e) {
			}
		}

		String id_user = (String) req.getSession().getAttribute("userid");
		String typeValeur = req.getParameter("typeValeur");
		if (id_user != null) {
			if (typeValeur.equals("CHQ") || typeValeur.equals("LCN")) {
				User u = userRepository.findById(id_user).get();
				if (u.getTypeValeurs().size() == 1
						&& !u.getTypeValeurs().stream().findFirst().get().getType().equals(typeValeur)) {
					redirectAttrs.addFlashAttribute("erreurmessage", "Type valeur erroné");
					return new ModelAndView("redirect:/remises");
				}
				List<TypeRemise> lstTypeRemise=new ArrayList<TypeRemise>(); 
				Iterator<TypeRemise> iterator = u.getTypeRemises().iterator();
				while (iterator.hasNext()) {
					TypeRemise tr=iterator.next();
					if (tr.getTypeValeur().getType().equalsIgnoreCase(typeValeur)) {
						lstTypeRemise.add(tr);
					}
				}
				model.addAttribute("currentUser", u);
				model.addAttribute("lstTypeRemise", lstTypeRemise);
				model.addAttribute("compteGrs",
						compteGrRepository.findAllByIsActiveAndUser_IdUser(true, u.getIdUser()));
				if (u.getParamRefRemise() != null && u.getParamRefRemise().getIsAuto()) {
					//model.addAttribute("referenceRemise", referenceRemise);
					ReferenceRemiseGenerated  referenceRemiseGenerated  = referenceRemiseGeneratorEngine.buildReferenceRemise(u);
					model.addAttribute("referenceRemise", referenceRemiseGenerated.getReferenceRemiseFromated());
					model.addAttribute("referenceRemiseUpdated", referenceRemiseGenerated.isHasreferenceRemiseFromated());
					model.addAttribute("referenceRemiseOld", referenceRemiseGenerated.getReferenceRemiseGenerated());
				
				}
				model.addAttribute("typeValeur", typeValeur);

				return new ModelAndView("formScan");
			} else {
				redirectAttrs.addFlashAttribute("erreurmessage", "Type valeur erroné");
				return new ModelAndView("redirect:/remises");
			}

		} else {
			return new ModelAndView("redirect:/login");
		}

	}

	@GetMapping(value = "/cloturer")
	public ModelAndView Cloturer(HttpServletRequest req, HttpServletResponse res, Model model,
			@RequestParam("idRemise") String id_remise) throws IOException {
		Connection cnxToDb = null;
		cnxToDb = PKConnect.getCnxToDb();
		if (!PKSecurity.isUserAuth(req, res, CommonData.PKEXPLORER_APP_CODE, cnxToDb)) {
			try {
				cnxToDb.close();
			} catch (Exception e) {
				log.error("Error ", e);
				// TODO: handle exception
			}
			res.sendRedirect(res.encodeRedirectURL("/PKAuth"));
			return null;
		} else {
			try {
				cnxToDb.close();
			} catch (SQLException e) {
			}
		}
		String id_user = (String) req.getSession().getAttribute("userid");
		if (id_user != null) {
			log.info("Clôturer la remise: " + id_remise);

			Remise remise = remiseRepository.findById(id_remise).get();
			User u = userRepository.findById(id_user).get();
			if (u.getRole().equals("ADMIN") || remise.getCapturePoint().equals(u.getCapturePoint())) {
				remise.setStatus(1);
				remiseRepository.save(remise);

				model.addAttribute("remise", remise);
				model.addAttribute("valeurs", remise.getValeurs());

				return new ModelAndView("scan");
			} else {
				return new ModelAndView("redirect:/remises");
			}
		} else {
			return new ModelAndView("redirect:/login");
		}
	}

	@RequestMapping(value = "/bordereau", method = RequestMethod.GET, produces = MediaType.APPLICATION_PDF_VALUE)
	public byte[] bordereau(HttpServletRequest req, HttpServletResponse res) throws IOException, DocumentException {

		String id_user = (String) req.getSession().getAttribute("userid");
		if (id_user != null) {
			String id_remise = req.getParameter("idRemise");
			try {

				Remise remise = remiseRepository.findById(id_remise).get();
				User u = userRepository.findById(id_user).get();
				if (u.getRole().equals("ADMIN") || remise.getCapturePoint().equals(u.getCapturePoint())) {
					List<Valeur> valeurs = new ArrayList<Valeur>(remise.getValeurs());
					log.info("Debut generation du bordereau de la remise: " + id_remise + " par " + u.getUserLogin());
					try {

						String masterPath = "/static/pdf/Bmce.pdf";
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						PdfReader pdfReader = new PdfReader(masterPath);
						PdfStamper pdfStamper = new PdfStamper(pdfReader, baos);

						PdfContentByte cbFirstPage = pdfStamper.getOverContent(1);

						cbFirstPage.rectangle(25, 50, 550, 780);
						cbFirstPage.stroke();

						// Add text in existing PDF
						cbFirstPage.beginText();
						cbFirstPage.setFontAndSize(
								BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 11);
						cbFirstPage.setTextMatrix(375, 760);

						StringBuilder typeValeur = new StringBuilder();
						if (remise.getTypeValeur().equals("CHQ")) {
							typeValeur.append("CHEQUE");
						} else {
							typeValeur.append("LCN");
						}

						cbFirstPage.showText(typeValeur.toString());
						cbFirstPage.setTextMatrix(315, 742);
						cbFirstPage.showText(remise.getReferenceRemise());
						// Les infos de la premiere colonne du recu avant les valeurs
						cbFirstPage.setFontAndSize(
								BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 9);
						cbFirstPage.setTextMatrix(100, 704);
						String code = remise.getCapturePoint().substring(0, 3);
						CodeLibelle codeLibelle = codeLibelleRepository.getLibelleByCode(code);
						String libelle = new String();
						if (codeLibelle != null)
							libelle = codeLibelle.getLibelle();
						else
							libelle = ".............";
						cbFirstPage.showText(libelle);

						cbFirstPage.setFontAndSize(
								BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 11);
						cbFirstPage.setTextMatrix(220, 686);
						StringBuilder codeAgenceCompte = new StringBuilder();
						String firstPart = remise.getRibRemettant().substring(3, 6);
						codeAgenceCompte.append(firstPart);
						String secondPart = remise.getRibRemettant().substring(10, 12);
						codeAgenceCompte.append(secondPart);

						cbFirstPage.showText(remise.getCapturePoint());
						cbFirstPage.setTextMatrix(220, 670);
						cbFirstPage.showText(codeAgenceCompte.toString());
						cbFirstPage.setTextMatrix(160, 651);
						cbFirstPage.showText(remise.getRibRemettant());
						cbFirstPage.setTextMatrix(220, 634);
						cbFirstPage.showText(String.valueOf(remise.getNombreValeur()));
						cbFirstPage.setFontAndSize(
								BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 9);
						cbFirstPage.setTextMatrix(150, 616);
						cbFirstPage.showText(remise.getTypeRemise());

						cbFirstPage.setTextMatrix(180, 600);
						cbFirstPage.showText(String.valueOf(remise.getTauxEscompte()));

						// Les infos de la 2éme colonne du recu avant les valeurs
						cbFirstPage.setFontAndSize(
								BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 11);
						cbFirstPage.setTextMatrix(420, 705);
						SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
						String stringSdt = sdf.format(remise.getSysDateTime());
						cbFirstPage.showText(stringSdt);

						cbFirstPage.setFontAndSize(
								BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 9);
						cbFirstPage.setTextMatrix(430, 686);
						cbFirstPage.showText(remise.getNomRemise());
						cbFirstPage.setTextMatrix(430, 670);
						cbFirstPage.showText(remise.getNomCompte());

						cbFirstPage.setTextMatrix(400, 652);
						cbFirstPage.showText(remise.getNomRemettant());

						cbFirstPage.setFontAndSize(
								BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 11);
						cbFirstPage.setTextMatrix(440, 632);
						cbFirstPage.showText(remise.getMontantRemise().toString());
						cbFirstPage.setFontAndSize(
								BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 9);
						cbFirstPage.setTextMatrix(380, 617);
						cbFirstPage.showText(remise.getUserName());

						cbFirstPage.setFontAndSize(
								BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 11);

						float absoluteHeight = 120;
						float absoluteWidth = 250;

						int ligneIndex = 0;
						int renderedImage = 0;
						int renderedImageFirstPage = 0;
						float heightToRenderImage = 430;

						PdfContentByte cbImg = pdfStamper.getOverContent(pdfReader.getNumberOfPages());

						int numeroValeur = 0;
						float lastHeightImage = 0;

						for (int i = 0; i < valeurs.size(); i++) {

							if (ligneIndex == 2) {
								ligneIndex = 0;
								renderedImageFirstPage++;
								renderedImage++;
							}

							numeroValeur++;

							String recto = valeurs.get(i).getUrlRecto();
							Image img = Image.getInstance(recto);

							ligneIndex++;

							PdfContentByte cbTextCheque = pdfStamper.getOverContent(pdfReader.getNumberOfPages());
							cbTextCheque.beginText();
							cbTextCheque.setFontAndSize(
									BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 11);

							if (renderedImageFirstPage < 3) {
								if (ligneIndex == 1) {
									img.setAbsolutePosition(45,
											heightToRenderImage - renderedImageFirstPage * 150 - 10);
									cbTextCheque.setTextMatrix(140,
											120 + heightToRenderImage - renderedImageFirstPage * 150);
								} else {
									img.setAbsolutePosition(305,
											heightToRenderImage - renderedImageFirstPage * 150 - 10);
									cbTextCheque.setTextMatrix(400,
											120 + heightToRenderImage - renderedImageFirstPage * 150);
								}
								cbTextCheque.showText("Valeur N° " + numeroValeur);
								lastHeightImage = heightToRenderImage - renderedImageFirstPage * 150 - 20;
							} else {
								if (ligneIndex == 1) {
									img.setAbsolutePosition(45, heightToRenderImage - renderedImage * 150 - 10);
									cbTextCheque.setTextMatrix(140, 120 + heightToRenderImage - renderedImage * 150);
								} else {
									img.setAbsolutePosition(305, heightToRenderImage - renderedImage * 150 - 10);
									cbTextCheque.setTextMatrix(400, 120 + heightToRenderImage - renderedImage * 150);
								}
								cbTextCheque.showText("Valeur N° " + numeroValeur);
								lastHeightImage = heightToRenderImage - renderedImage * 150 - 20;
							}
							cbTextCheque.endText();

							img.scaleAbsoluteHeight(absoluteHeight);
							img.scaleAbsoluteWidth(absoluteWidth);
							cbImg.addImage(img);

							if (((renderedImageFirstPage == 2 & ligneIndex == 2)
									|| (renderedImage == 5 & ligneIndex == 2)) && i < valeurs.size() - 1) {
								renderedImage = 0;
								heightToRenderImage = 830;
								pdfStamper.insertPage(pdfReader.getNumberOfPages() + 1,
										pdfReader.getPageSizeWithRotation(1));
								cbImg = pdfStamper.getOverContent(pdfReader.getNumberOfPages());
								cbImg.rectangle(25, 50, 550, 780);
								cbImg.stroke();
							}
						}

						// Rectange signature client et agence
						PdfContentByte cbSignature = pdfStamper.getOverContent(pdfReader.getNumberOfPages());
						if (renderedImageFirstPage == 2 || renderedImage == 5) {
							pdfStamper.insertPage(pdfReader.getNumberOfPages() + 1,
									pdfReader.getPageSizeWithRotation(1));
							cbSignature = pdfStamper.getOverContent(pdfReader.getNumberOfPages());
							lastHeightImage = 800;
						}
						cbSignature.rectangle(25, 50, 550, 780);
						cbSignature.stroke();
						cbSignature.rectangle(40, lastHeightImage - 85, 520, 80);
						cbSignature.moveTo(40, lastHeightImage - 20);
						cbSignature.lineTo(560, lastHeightImage - 20);
						cbSignature.moveTo(300, lastHeightImage - 5);
						cbSignature.lineTo(300, lastHeightImage - 85);
						cbSignature.stroke();

						cbSignature.setFontAndSize(
								BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 11);
						cbSignature.setTextMatrix(110, lastHeightImage - 15);
						cbSignature.showText("SIGNATURE CLIENT");
						cbSignature.setTextMatrix(370, lastHeightImage - 15);
						cbSignature.showText("SIGNATURE AGENCE");

						String FONT = "/static/pdf/Amiri-Bold.ttf";
						String arabic = "+بنك أفريقيا - شركة مساهمة رأسمالها 600 204 998 1 درهم - مؤسسة ائتمان - قرار إعتماد رقم 94-2348 بتاريخ 23 غشت 1994 – 140, محج الحسن الثاني - 039 20 الدار البيضاء - المغرب - س.ت.:27129 الدار البيضاء - رقم التعريف الجبائي: 01085112 – الضريبة المهنية: 35502790 - ح.ش.ب. الرباط 1030 - ص.و.ض.ج. 5 2808 10 -ش.ت.م. 001512572000078 – الهاتف: 25 03 20 522 212+/20 04 20 522 212";
						String french = "BANK OF AFRICA - S.A. au capital de 1 998 204 600 Dirhams - Établissement de crédit - Arrêté d’agrément n° 2348-94 du 23 août 1994 - 140 avenue Hassan II - 20 039 Casablanca – Maroc RC : 27129 Casa - N° IF : 01085112 - Patente : 35502790 - CCP Rabat 1030 - CNSS 10 2808 5 - ICE 001512572000078 - Tél. : +212 522 20 04 20 / +212 522 20 03 25";

						Font f = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 3);
						f.setColor(1, 64, 129);
						Phrase p = new Phrase();
						p.add(new Chunk(arabic, f));

						for (int i = 0; i < pdfReader.getNumberOfPages(); i++) {
							ColumnText canvas = new ColumnText(pdfStamper.getOverContent(i + 1));
							canvas.setSimpleColumn(700, 0, 70, 75);
							canvas.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
							canvas.addElement(p);
							canvas.go();

							PdfContentByte ligne = pdfStamper.getOverContent(i + 1);
							ligne.setRGBColorStroke(3, 185, 189);
							ligne.moveTo(80, 90 - 25);
							ligne.lineTo(510, 90 - 25);
							ligne.stroke();

							PdfContentByte cbFrench = pdfStamper.getOverContent(i + 1);
							cbFrench.beginText();
							cbFrench.setFontAndSize(
									BaseFont.createFont(BaseFont.TIMES_BOLD, BaseFont.CP1257, BaseFont.EMBEDDED), 3);
							BaseColor bc = new BaseColor(1, 64, 129);
							cbFrench.setColorFill(bc);
							cbFrench.setTextMatrix(70, 79 - 25);
							cbFrench.showText(french);
						}

						pdfStamper.close();

						byte[] bytePdf = baos.toByteArray();
						// String strPdf = Base64.encodeBase64String(bytePdf);
						log.info("Fin generation du bordereau de la remise: " + id_remise + " par " + u.getUserLogin());
						int random = Methodes.GenerateRandom();
						String idHisto = Methodes.getId() + (String.valueOf(random).substring(0, 2));
						UserHisto userHisto = new UserHisto(idHisto, new Date(), "Consulter bordereau",
								"Consulter le bordereau de la remise:" + id_remise, u);
						userHistoRepository.save(userHisto);
						// return strPdf;
						return bytePdf;

					} catch (Exception e) {
						e.printStackTrace();
						log.error("Error:" + e + " de la generation du bordereau de la remise: " + id_remise);
					}
				} else {
					log.warn("Vos pouvez pas genere le bordereau de cette remise: " + id_remise + " par "
							+ u.getUserLogin());
				}
			} catch (Exception e) {
				log.error("Error generation du bordereau de la remise: " + id_remise, e);
			}
		}

		return null;

	}

	@RequestMapping(value = "/continueScan", method = RequestMethod.GET)
	public ModelAndView continueScan(HttpServletRequest req, HttpServletResponse res, Model model,
			RedirectAttributes redirectAttrs, @RequestParam("idRemise") String id_remise)
			throws ParseException, IOException {
		Connection cnxToDb = null;
		cnxToDb = PKConnect.getCnxToDb();
		if (!PKSecurity.isUserAuth(req, res, CommonData.PKEXPLORER_APP_CODE, cnxToDb)) {
			try {
				cnxToDb.close();
			} catch (Exception e) {
				log.error("Error ", e);
				// TODO: handle exception
			}
			res.sendRedirect(res.encodeRedirectURL("/PKAuth"));
			return null;
		} else {
			try {
				cnxToDb.close();
			} catch (SQLException e) {
			}
		}
		String id_user = (String) req.getSession().getAttribute("userid");
		if (id_user != null) {
			try {
				User u = userRepository.findById(id_user).get();
				Remise remise = remiseRepository.findById(id_remise).get();

				if (u.getRole().equals("ADMIN")
						|| (u.getRole().equals("USER") && remise.getCapturePoint().equals(u.getCapturePoint()))) {
					log.info("Demande de continuer le scan de la remise: " + id_remise + " par l'utilisateur: "
							+ u.getUserName());

					int randomHisto = Methodes.GenerateRandom();
					String idHisto = Methodes.getId() + (String.valueOf(randomHisto).substring(0, 2));
					SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
					String stringSdt = sdf.format(new Date());
					Date date_action = sdf.parse(stringSdt);
					UserHisto userHisto = new UserHisto(idHisto, date_action, "Reprise scan",
							"Demande de reprise scan de la remise de l'ID=" + id_remise, u);
					userHistoRepository.save(userHisto);

					model.addAttribute("remise", remise);
					model.addAttribute("valeurs", remise.getValeurs());

					UserAgent userAgent = UserAgent.parseUserAgentString(req.getHeader("User-Agent"));
					String navigName = userAgent.getBrowser().getName();
					log.info("Navigateur:" + navigName);
					if (navigName.contains("Internet Explorer")) {
						try {
							Runtime.getRuntime().exec("cmd.exe /C start microsoft-edge:"
									+ req.getRequestURL().toString() + "?" + req.getQueryString());
						} catch (IOException e) {
							log.error("Erreur Navigateur:", e);
						}
						redirectAttrs.addFlashAttribute("erreurmessage",
								"Ce navigateur " + navigName + " n'est pas compatible merci d'utiliser EDGE");
						return new ModelAndView("redirect:/remises");

					} else {
						return new ModelAndView("scan");
					}
				} else {
					return new ModelAndView("redirect:/remises");
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.error("", e);
				return new ModelAndView("redirect:/remises");
			}

		} else {
			return new ModelAndView("redirect:/login");
		}

	}

}

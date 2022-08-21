package dev.procheck.capweb.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.procheck.capweb.dao.CompteGrRepository;
import dev.procheck.capweb.dao.ParamRefRemiseRepository;
import dev.procheck.capweb.entities.CompteGr;
import dev.procheck.capweb.entities.ParamRefRemise;
import dev.procheck.capweb.entities.User;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReferenceRemiseGenerator {
	
	@Autowired
	private  CompteGrRepository compteGrRepository;
	

	public  long getDifferenceDays(Date d1, Date d2) {
		long diff = d2.getTime() - d1.getTime();
		return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	
	public ParamRefRemise getReferenceRemise(User u) {
		ParamRefRemise  paramRefRemise =  u.getParamRefRemise();
		return paramRefRemise;
	}
	
	public  String getIdCompteGr(User u) {
		Collection<CompteGr> compteGrs = u.getCompteGrs();
		Optional<CompteGr> compteGrOptional = compteGrs.stream().findFirst();
		CompteGr compteGr = compteGrOptional.get();
		String idCompteGr = compteGr.getIdCompteGr();
		return idCompteGr;
	}

	public  CompteGr getCompteGr(String idCompteGr) {
		Optional<CompteGr> compteGrOptional = compteGrRepository.findById(idCompteGr);
		CompteGr compteGr = compteGrOptional.get();
		return compteGr;
	}

	public  String convertDateToString(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String strDate = dateFormat.format(date);
		return strDate;
	}

	public  String getDateNowAsString() {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime localDateForDT = LocalDateTime.now();
		return dateTimeFormatter.format(localDateForDT);
	}
}

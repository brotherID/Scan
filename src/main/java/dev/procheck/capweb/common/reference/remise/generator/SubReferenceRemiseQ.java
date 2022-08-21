package dev.procheck.capweb.common.reference.remise.generator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.procheck.capweb.entities.User;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SubReferenceRemiseQ implements SubReferenceRemise {

	@Value("${sub.formulre.ference.remise.q:Q(\\.\\d+)?}")
	private String subRegexFormulReferenceRemiseQ;

	@Value("${sub.formulre.ference.remise.q.length:3}")
	private String subFormulReferenceRemiseQLength;

	@Value("${sub.formulre.ference.remise.q.length:0}")
	private String subFormulReferenceRemiseQDefaultDigit;

	@Override
	public Optional<String> buildSubReferenceRemiseIfNecessary(String subFormulReferenceRemiseQ, User u) {
		Optional<String> subReferenceRemiseq = Optional.empty();
		if (subFormulReferenceRemiseQ.matches(subRegexFormulReferenceRemiseQ)) {
			String numberOfdigitAsString = subFormulReferenceRemiseQ.split("\\.").length >= 2
					? subFormulReferenceRemiseQ.split("\\.")[1]
					: subFormulReferenceRemiseQLength;
			String year;
			DateTimeFormatter dateTimeFormatterYear = DateTimeFormatter.ofPattern("yyyy");
			LocalDateTime localDateForYear = LocalDateTime.now();
			year = dateTimeFormatterYear.format(localDateForYear);
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			String dateConcat = "01/01/".concat(year);
			Date firstDate = null;
			try {
				firstDate = sdf.parse(dateConcat);
			} catch (ParseException e) {
				log.error("ParseException {}", e);
			}
			DateTimeFormatter dateTimeFormatterMDY = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			LocalDateTime localDateTimeMDY = LocalDateTime.now();
			String dateNow = dateTimeFormatterMDY.format(localDateTimeMDY);
			Date secondDate = null;
			try {
				secondDate = sdf.parse(dateNow);
			} catch (ParseException e) {
				log.error("ParseException {}", e);
			}
			long diff = secondDate.getTime() - firstDate.getTime();
			TimeUnit time = TimeUnit.DAYS;
			long diffrence = time.convert(diff, TimeUnit.MILLISECONDS);
			subReferenceRemiseq = Optional.of(StringUtils.leftPad(String.valueOf(diffrence),
					Integer.valueOf(numberOfdigitAsString), subFormulReferenceRemiseQDefaultDigit));
		}
		return subReferenceRemiseq;
	}

	@Override
	public String getSubFormul() {
		return subRegexFormulReferenceRemiseQ;
	}

	@Override
	public Optional<String> buildFakeSubReferenceRemiseIfNecessary(String subFormulReferenceRemiseQ,String formul,String capturePoint) {
		Optional<String> subReferenceRemiseq = Optional.empty();
		if (subFormulReferenceRemiseQ.matches(subRegexFormulReferenceRemiseQ)) {
			String numberOfdigitAsString = subFormulReferenceRemiseQ.split("\\.").length >= 2
					? subFormulReferenceRemiseQ.split("\\.")[1]
					: subFormulReferenceRemiseQLength;
			String year;
			DateTimeFormatter dateTimeFormatterYear = DateTimeFormatter.ofPattern("yyyy");
			LocalDateTime localDateForYear = LocalDateTime.now();
			year = dateTimeFormatterYear.format(localDateForYear);
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			String dateConcat = "01/01/".concat(year);
			Date firstDate = null;
			try {
				firstDate = sdf.parse(dateConcat);
			} catch (ParseException e) {
				log.error("ParseException {}", e);
			}
			DateTimeFormatter dateTimeFormatterMDY = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			LocalDateTime localDateTimeMDY = LocalDateTime.now();
			String dateNow = dateTimeFormatterMDY.format(localDateTimeMDY);
			Date secondDate = null;
			try {
				secondDate = sdf.parse(dateNow);
			} catch (ParseException e) {
				log.error("ParseException {}", e);
			}
			long diff = secondDate.getTime() - firstDate.getTime();
			TimeUnit time = TimeUnit.DAYS;
			long diffrence = time.convert(diff, TimeUnit.MILLISECONDS);
			subReferenceRemiseq = Optional.of(StringUtils.leftPad(String.valueOf(diffrence),
					Integer.valueOf(numberOfdigitAsString), subFormulReferenceRemiseQDefaultDigit));
		}
		return subReferenceRemiseq;
	}

}

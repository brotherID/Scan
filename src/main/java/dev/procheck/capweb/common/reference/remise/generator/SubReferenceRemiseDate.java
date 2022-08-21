package dev.procheck.capweb.common.reference.remise.generator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.procheck.capweb.entities.User;

@Service
public class SubReferenceRemiseDate implements SubReferenceRemise {

	@Value("${sub.formulre.ference.remise.date:DT(\\.\\d+)?}")
	private String subRegexFormulReferenceRemiseDate;

	@Value("${sub.formulre.ference.remise.date.length:6}")
	private String subFormulReferenceRemiseDateLength;

	@Value("${sub.formulre.ference.remise.date.length:0}")
	private String subFormulReferenceRemiseDateDefaultDigit;

	@Value("${sub.formulre.ference.remise.date.length:ddMMyy}")
	private String subFormulReferenceRemiseDatePatternt;

	@Override
	public Optional<String> buildSubReferenceRemiseIfNecessary(String subFormulReferenceRemiseDate, User u) {
		Optional<String> subReferenceRemisedate = Optional.empty();
		if (subFormulReferenceRemiseDate.matches(subRegexFormulReferenceRemiseDate)) {
			String numberOfdigitAsString = subFormulReferenceRemiseDate.split("\\.").length >= 2
					? subFormulReferenceRemiseDate.split("\\.")[1]
					: subFormulReferenceRemiseDateLength;
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(subFormulReferenceRemiseDatePatternt);
			LocalDateTime localDateForDT = LocalDateTime.now();
			subReferenceRemisedate = Optional.of(StringUtils.leftPad(dateTimeFormatter.format(localDateForDT),
					Integer.valueOf(numberOfdigitAsString), "0"));
		}
		return subReferenceRemisedate;
	}

	@Override
	public String getSubFormul() {

		return subRegexFormulReferenceRemiseDate;
	}

	@Override
	public Optional<String> buildFakeSubReferenceRemiseIfNecessary(String subFormulReferenceRemiseDate, String formul,String capturePoint) {
		Optional<String> subReferenceRemisedate = Optional.empty();
		if (subFormulReferenceRemiseDate.matches(subRegexFormulReferenceRemiseDate)) {
			String numberOfdigitAsString = subFormulReferenceRemiseDate.split("\\.").length >= 2
					? subFormulReferenceRemiseDate.split("\\.")[1]
					: subFormulReferenceRemiseDateLength;
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(subFormulReferenceRemiseDatePatternt);
			LocalDateTime localDateForDT = LocalDateTime.now();
			subReferenceRemisedate = Optional.of(StringUtils.leftPad(dateTimeFormatter.format(localDateForDT),
					Integer.valueOf(numberOfdigitAsString), "0"));
		}
		return subReferenceRemisedate;
	}

}

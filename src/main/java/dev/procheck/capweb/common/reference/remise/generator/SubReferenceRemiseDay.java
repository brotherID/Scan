package dev.procheck.capweb.common.reference.remise.generator;

import java.time.LocalDate;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.procheck.capweb.entities.User;

@Service
public class SubReferenceRemiseDay implements SubReferenceRemise {

	@Value("${sub.formulre.ference.remise.day:[j|J](\\.\\d+)?}")
	private String subRegexFormulReferenceRemiseDay;

	@Value("${sub.formulre.ference.remise.day.length:2}")
	private String subFormulReferenceRemiseDayLength;

	@Value("${sub.formulre.ference.remise.day.length:0}")
	private String subFormulReferenceRemiseDayDefaultDigit;

	@Override
	public Optional<String> buildSubReferenceRemiseIfNecessary(String subFormulReferenceRemiseDay,User u) {
		Optional<String> subReferenceRemiseDay = Optional.empty();
		if (subFormulReferenceRemiseDay.matches(subRegexFormulReferenceRemiseDay)) {
			String numberOfdigitAsString = subFormulReferenceRemiseDay.split("\\.").length >= 2 ? subFormulReferenceRemiseDay.split("\\.")[1]
					: subFormulReferenceRemiseDayLength;
			LocalDate localDateforJ = LocalDate.now();
			subReferenceRemiseDay = Optional.of(StringUtils.leftPad(String.valueOf(localDateforJ.getDayOfMonth()),
					Integer.valueOf(numberOfdigitAsString), subFormulReferenceRemiseDayDefaultDigit));
		}

		return subReferenceRemiseDay;
	}

	@Override
	public String getSubFormul() {
		return subRegexFormulReferenceRemiseDay;
	}

	@Override
	public Optional<String> buildFakeSubReferenceRemiseIfNecessary(String subFormulReferenceRemiseDay, String formul,String capturePoint) {
		Optional<String> subReferenceRemiseDay = Optional.empty();
		if (subFormulReferenceRemiseDay.matches(subRegexFormulReferenceRemiseDay)) {
			String numberOfdigitAsString = subFormulReferenceRemiseDay.split("\\.").length >= 2 ? subFormulReferenceRemiseDay.split("\\.")[1]
					: subFormulReferenceRemiseDayLength;
			LocalDate localDateforJ = LocalDate.now();
			subReferenceRemiseDay = Optional.of(StringUtils.leftPad(String.valueOf(localDateforJ.getDayOfMonth()),
					Integer.valueOf(numberOfdigitAsString), subFormulReferenceRemiseDayDefaultDigit));
		}
		
		return subReferenceRemiseDay;
	}

}

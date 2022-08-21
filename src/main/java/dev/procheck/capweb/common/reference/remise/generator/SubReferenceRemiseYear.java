package dev.procheck.capweb.common.reference.remise.generator;

import java.time.LocalDate;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.procheck.capweb.entities.User;

@Service
public class SubReferenceRemiseYear implements SubReferenceRemise {

	@Value("${sub.formulre.ference.remise.year:A(\\.\\d+)?}")
	private String subRegexFormulReferenceRemiseYear;

	@Value("${sub.formulre.ference.remise.year.length:4}")
	private String subFormulReferenceRemiseYearLength;

	@Value("${sub.formulre.ference.remise.year.length:0}")
	private String subFormulReferenceRemiseYearDefaultDigit;

	@Override
	public Optional<String> buildSubReferenceRemiseIfNecessary(String subFormulReferenceRemiseYear, User u) {
		Optional<String> subReferenceRemiseYear = Optional.empty();

		if (subFormulReferenceRemiseYear.matches(subRegexFormulReferenceRemiseYear)) {
			String numberOfdigitAsString = subFormulReferenceRemiseYear.split("\\.").length >= 2
					? subFormulReferenceRemiseYear.split("\\.")[1]
					: subFormulReferenceRemiseYearLength;
			LocalDate localDateForA = LocalDate.now();
			subReferenceRemiseYear = Optional.of(StringUtils.leftPad(String.valueOf(localDateForA.getYear()),
					Integer.valueOf(numberOfdigitAsString), subFormulReferenceRemiseYearDefaultDigit));
		}

		return subReferenceRemiseYear;
	}

	@Override
	public String getSubFormul() {
		return subRegexFormulReferenceRemiseYear;
	}

	@Override
	public Optional<String> buildFakeSubReferenceRemiseIfNecessary(String subFormulReferenceRemiseYear,String formul,
			String capturePoint) {
		Optional<String> subReferenceRemiseYear = Optional.empty();

		if (subFormulReferenceRemiseYear.matches(subRegexFormulReferenceRemiseYear)) {
			String numberOfdigitAsString = subFormulReferenceRemiseYear.split("\\.").length >= 2
					? subFormulReferenceRemiseYear.split("\\.")[1]
					: subFormulReferenceRemiseYearLength;
			LocalDate localDateForA = LocalDate.now();
			subReferenceRemiseYear = Optional.of(StringUtils.leftPad(String.valueOf(localDateForA.getYear()),
					Integer.valueOf(numberOfdigitAsString), subFormulReferenceRemiseYearDefaultDigit));
		}

		return subReferenceRemiseYear;
	}

}

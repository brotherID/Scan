package dev.procheck.capweb.common.reference.remise.generator;

import java.time.LocalDate;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.procheck.capweb.entities.User;

@Service
public class SubReferenceRemiseMonth implements SubReferenceRemise {

	@Value("${sub.formulre.ference.remise.month:([m|M|]|MM)(\\.\\d+)?}")
	private String subRegexFormulReferenceRemiseMonth;

	@Value("${sub.formulre.ference.remise.month.length:2}")
	private String subFormulReferenceRemiseMonthLength;

	@Value("${sub.formulre.ference.remise.month.length:0}")
	private String subFormulReferenceRemiseMonthDefaultDigit;

	@Override
	public Optional<String> buildSubReferenceRemiseIfNecessary(String subFormulReferenceRemiseMonth, User u) {
		Optional<String> subReferenceRemisemonth = Optional.empty();

		if (subFormulReferenceRemiseMonth.matches(subRegexFormulReferenceRemiseMonth)) {
			String numberOfdigitAsString = subFormulReferenceRemiseMonth.split("\\.").length >= 2
					? subFormulReferenceRemiseMonth.split("\\.")[1]
					: subFormulReferenceRemiseMonthLength;
			LocalDate localDateForM = LocalDate.now();
			subReferenceRemisemonth = Optional.of(StringUtils.leftPad(String.valueOf(localDateForM.getMonthValue()),
					Integer.valueOf(numberOfdigitAsString), subFormulReferenceRemiseMonthDefaultDigit));
		}
		return subReferenceRemisemonth;
	}

	@Override
	public String getSubFormul() {
		return subRegexFormulReferenceRemiseMonth;
	}

	@Override
	public Optional<String> buildFakeSubReferenceRemiseIfNecessary(String subFormulReferenceRemiseMonth, String formul,
			String capturePoint) {
		Optional<String> subReferenceRemisemonth = Optional.empty();

		if (subFormulReferenceRemiseMonth.matches(subRegexFormulReferenceRemiseMonth)) {
			String numberOfdigitAsString = subFormulReferenceRemiseMonth.split("\\.").length >= 2
					? subFormulReferenceRemiseMonth.split("\\.")[1]
					: subFormulReferenceRemiseMonthLength;
			LocalDate localDateForM = LocalDate.now();
			subReferenceRemisemonth = Optional.of(StringUtils.leftPad(String.valueOf(localDateForM.getMonthValue()),
					Integer.valueOf(numberOfdigitAsString), subFormulReferenceRemiseMonthDefaultDigit));
		}
		return subReferenceRemisemonth;
	}

}

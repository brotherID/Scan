package dev.procheck.capweb.common.reference.remise.generator;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.procheck.capweb.entities.User;

@Service
public class SubReferenceRemiseWeek implements SubReferenceRemise {

	@Value("${sub.formulre.ference.remise.week:[w|W](\\.\\d+)?}")
	private String subRegexFormulReferenceRemiseWeek;

	@Value("${sub.formulre.ference.remise.week.length:2}")
	private String subFormulReferenceRemiseWeekLength;

	@Value("${sub.formulre.ference.remise.week.length:0}")
	private String subFormulReferenceRemiseWeekDefaultDigit;

	@Override
	public Optional<String> buildSubReferenceRemiseIfNecessary(String subFormulReferenceRemiseWeek, User u) {
		Optional<String> subReferenceRemiseweek = Optional.empty();

		if (subFormulReferenceRemiseWeek.matches(subRegexFormulReferenceRemiseWeek)) {
			String numberOfdigitAsString = subFormulReferenceRemiseWeek.split("\\.").length >= 2
					? subFormulReferenceRemiseWeek.split("\\.")[1]
					: subFormulReferenceRemiseWeekLength;
			LocalDate localDateForW = LocalDate.now();
			TemporalField weekOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
			int weekNumber = localDateForW.get(weekOfYear);
			subReferenceRemiseweek = Optional.of(StringUtils.leftPad(String.valueOf(weekNumber),
					Integer.valueOf(numberOfdigitAsString), subFormulReferenceRemiseWeekDefaultDigit));
		}
		return subReferenceRemiseweek;
	}

	@Override
	public String getSubFormul() {
		return subRegexFormulReferenceRemiseWeek;
	}

	@Override
	public Optional<String> buildFakeSubReferenceRemiseIfNecessary(String subFormulReferenceRemiseWeek, String formul,
			String capturePoint) {
		Optional<String> subReferenceRemiseweek = Optional.empty();

		if (subFormulReferenceRemiseWeek.matches(subRegexFormulReferenceRemiseWeek)) {
			String numberOfdigitAsString = subFormulReferenceRemiseWeek.split("\\.").length >= 2
					? subFormulReferenceRemiseWeek.split("\\.")[1]
					: subFormulReferenceRemiseWeekLength;
			LocalDate localDateForW = LocalDate.now();
			TemporalField weekOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
			int weekNumber = localDateForW.get(weekOfYear);
			subReferenceRemiseweek = Optional.of(StringUtils.leftPad(String.valueOf(weekNumber),
					Integer.valueOf(numberOfdigitAsString), subFormulReferenceRemiseWeekDefaultDigit));
		}
		return subReferenceRemiseweek;
	}

}

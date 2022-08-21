package dev.procheck.capweb.common.reference.remise.generator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.procheck.capweb.dao.ParamRefRemiseRepository;
import dev.procheck.capweb.entities.ParamRefRemise;
import dev.procheck.capweb.entities.User;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SubReferenceRemiseSD implements SubReferenceRemise {

	@Value("${sub.formulre.ference.remise.sd:SD(\\.\\d+)?}")
	private String subRegexFormulReferenceRemiseSD;

	@Value("${sub.formulre.ference.remise.sd.length:3}")
	private String subFormulReferenceRemiseSDLength;

	@Value("${sub.formulre.ference.remise.sd.length:0}")
	private String subFormulReferenceRemiseSDDefaultDigit;

	@Autowired
	private ParamRefRemiseRepository paramRefRemiseRepository;

	@Override
	public Optional<String> buildSubReferenceRemiseIfNecessary(String subFormulReferenceRemiseSD, User user) {
		Optional<String> subReferenceRemiseSD = Optional.empty();
		if (subFormulReferenceRemiseSD.matches(subRegexFormulReferenceRemiseSD)) {

			String numberOfdigitAsString = subFormulReferenceRemiseSD.split("\\.").length >= 2
					? subFormulReferenceRemiseSD.split("\\.")[1]
					: subFormulReferenceRemiseSDLength;
			ParamRefRemise paramRefRemise = user.getParamRefRemise();
			if (getDateNowAsString().equals(paramRefRemise.getDateTraitement())) {
				if (paramRefRemise.getSequenceDay() == null) {
					paramRefRemise.setSequenceDay(0);
				}
				AtomicInteger atomicIntegerForSD = new AtomicInteger(paramRefRemise.getSequenceDay());
				int sequenceDayUpdated = atomicIntegerForSD.addAndGet(1);
				paramRefRemise.setSequenceDay(sequenceDayUpdated);
				paramRefRemiseRepository.save(paramRefRemise);
			} else {
				paramRefRemise.setSequenceDay(1);
				paramRefRemise.setDateTraitement(getDateNowAsString());
				paramRefRemiseRepository.save(paramRefRemise);
			}
			subReferenceRemiseSD = Optional.of(StringUtils.leftPad(String.valueOf(paramRefRemise.getSequenceDay()),
					Integer.valueOf(numberOfdigitAsString), subFormulReferenceRemiseSDDefaultDigit));
			if(!numberOfdigitAsString.isEmpty()&&(subReferenceRemiseSD.get().length()>Integer.valueOf(numberOfdigitAsString)))
			{
				subReferenceRemiseSD = Optional.of(subReferenceRemiseSD.get().substring((subReferenceRemiseSD.get().length()-
						Integer.valueOf(numberOfdigitAsString)),subReferenceRemiseSD.get().length()));
			}
			
		}

		return subReferenceRemiseSD;

	}

	@Override
	public String getSubFormul() {
		return subRegexFormulReferenceRemiseSD;
	}

	public String getDateNowAsString() {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime localDateForDT = LocalDateTime.now();
		return dateTimeFormatter.format(localDateForDT);
	}

	@Override
	public Optional<String> buildFakeSubReferenceRemiseIfNecessary(String subFormulReferenceRemiseSD, String formul,
			String capturePoint) {
		Optional<String> subReferenceRemiseSD = Optional.empty();
		if (subFormulReferenceRemiseSD.matches(subRegexFormulReferenceRemiseSD)) {

			String numberOfdigitAsString = subFormulReferenceRemiseSD.split("\\.").length >= 2
					? subFormulReferenceRemiseSD.split("\\.")[1]
					: subFormulReferenceRemiseSDLength;

			int sequenceDayUpdated = 1;

			subReferenceRemiseSD = Optional.of(StringUtils.leftPad(String.valueOf(sequenceDayUpdated),
					Integer.valueOf(numberOfdigitAsString), subFormulReferenceRemiseSDDefaultDigit));
		}

		return subReferenceRemiseSD;
	}

}

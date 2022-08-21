package dev.procheck.capweb.common.reference.remise.generator;

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
public class SubReferenceRemiseS implements SubReferenceRemise {

	@Value("${sub.formulre.ference.remise.s:S(\\.\\d+)?}")
	private String subRegexFormulReferenceRemiseS;

	@Value("${sub.formulre.ference.remise.s.length:3}")
	private String subFormulReferenceRemiseSLength;

	@Value("${sub.formulre.ference.remise.s.length:0}")
	private String subFormulReferenceRemiseSDefaultDigit;

	@Autowired
	private ParamRefRemiseRepository paramRefRemiseRepository;

	@Override
	public Optional<String> buildSubReferenceRemiseIfNecessary(String subFormulReferenceRemiseS, User user) {
		Optional<String> subReferenceRemiseS = Optional.empty();
		if (subFormulReferenceRemiseS.matches(subRegexFormulReferenceRemiseS)) {

			String numberOfdigitAsString = subFormulReferenceRemiseS.split("\\.").length >= 2
					? subFormulReferenceRemiseS.split("\\.")[1]
					: subFormulReferenceRemiseSLength;
			ParamRefRemise paramRefRemise = user.getParamRefRemise();
			if (paramRefRemise.getSequence() == null) {
				paramRefRemise.setSequence(0);
			}
			AtomicInteger atomicInteger = new AtomicInteger(paramRefRemise.getSequence());
			Integer sequenceUpdated = atomicInteger.addAndGet(1);
			paramRefRemise.setSequence(sequenceUpdated);
			
			paramRefRemiseRepository.save(paramRefRemise);
			
			subReferenceRemiseS = Optional.of(
					StringUtils.leftPad(String.valueOf(sequenceUpdated), Integer.valueOf(numberOfdigitAsString), "0"));
		}

		return subReferenceRemiseS;
	}

	@Override
	public String getSubFormul() {
		return subRegexFormulReferenceRemiseS;
	}

	@Override
	public Optional<String> buildFakeSubReferenceRemiseIfNecessary(String subFormulReferenceRemiseS,String formul,String capturePoint) {
		Optional<String> subReferenceRemiseS = Optional.empty();
		if (subFormulReferenceRemiseS.matches(subRegexFormulReferenceRemiseS)) {

			String numberOfdigitAsString = subFormulReferenceRemiseS.split("\\.").length >= 2
					? subFormulReferenceRemiseS.split("\\.")[1]
					: subFormulReferenceRemiseSLength;
			Integer sequenceUpdated = 1;
			subReferenceRemiseS = Optional.of(
					StringUtils.leftPad(String.valueOf(sequenceUpdated), Integer.valueOf(numberOfdigitAsString), "0"));
		}

		return subReferenceRemiseS;
	}

}

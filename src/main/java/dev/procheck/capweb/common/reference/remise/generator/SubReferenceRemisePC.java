package dev.procheck.capweb.common.reference.remise.generator;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.procheck.capweb.entities.User;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SubReferenceRemisePC implements SubReferenceRemise {

	@Value("${sub.formulre.ference.remise.PC:PC(\\.\\d+)?}")
	private String subRegexFormulReferenceRemisePC;

	@Value("${sub.formulre.ference.remise.PC.length:5}")
	private String subFormulReferenceRemisePCLength;

	@Value("${sub.formulre.ference.remise.PC.length:0}")
	private String subFormulReferenceRemisePCDefaultDigit;

	@Override
	public Optional<String> buildSubReferenceRemiseIfNecessary(String subFormulReferenceRemisePC, User u) {
		Optional<String> subReferenceRemisePC = Optional.empty();
		if (subFormulReferenceRemisePC.matches(subRegexFormulReferenceRemisePC)) {
			String numberOfdigitAsString = subFormulReferenceRemisePC.split("\\.").length >= 2
					? subFormulReferenceRemisePC.split("\\.")[1]
					: subFormulReferenceRemisePCLength;
			
			subReferenceRemisePC = Optional.of(StringUtils.leftPad(u.getCapturePoint(),
					Integer.valueOf(numberOfdigitAsString), subFormulReferenceRemisePCDefaultDigit));
			if(!numberOfdigitAsString.isEmpty()&&(subReferenceRemisePC.get().length()>Integer.valueOf(numberOfdigitAsString)))
			{
				subReferenceRemisePC = Optional.of(subReferenceRemisePC.get().substring((subReferenceRemisePC.get().length()-
						Integer.valueOf(numberOfdigitAsString)),subReferenceRemisePC.get().length()));
			}
			
		}
		return subReferenceRemisePC;
	}

	@Override
	public String getSubFormul() {
		return subRegexFormulReferenceRemisePC;
	}

	@Override
	public Optional<String> buildFakeSubReferenceRemiseIfNecessary(String subFormulReferenceRemisePC, String formul,String capturePoint) {
		Optional<String> subReferenceRemisePC = Optional.empty();

		if (subFormulReferenceRemisePC.matches(subRegexFormulReferenceRemisePC)) {
			String numberOfdigitAsString = subFormulReferenceRemisePC.split("\\.").length >= 2
					? subFormulReferenceRemisePC.split("\\.")[1]
					: subFormulReferenceRemisePCLength;
			
			subReferenceRemisePC = Optional.of(StringUtils.leftPad(capturePoint,
					Integer.valueOf(numberOfdigitAsString), subFormulReferenceRemisePCDefaultDigit));
		}
		return subReferenceRemisePC;
	}

}

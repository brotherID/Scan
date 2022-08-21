package dev.procheck.capweb.common.reference.remise.generator;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import dev.procheck.capweb.entities.User;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReferenceRemiseGeneratorEngine {

	@Autowired
	private List<SubReferenceRemise> subReferenceRemises;
	@Autowired
	private SubReferenceRemiseS subReferenceRemiseS;
	@Autowired
	private SubReferenceRemiseSD subReferenceRemiseSD;

	@Value("${formule.reference.remise.split:%}")
	private String formuleReferenceRemiseSplit;

	@Value("${reference.remise.max.length:9}")
	private Integer referenceRemiseMaxLength;
	
	@Value("${sub.formulre.ference.remise.sd.s.length:0}")
	private String subFormulReferenceRemiseSDOrSDefaultDigit;
	
	public ReferenceRemiseGenerated buildFakeReferenceRemise(String  formuleReferenceRemise) {
		log.info("********************* Begin buildFakeReferenceRemise *********************");
		ReferenceRemiseGenerated referenceRemiseGenerated = new ReferenceRemiseGenerated();

		Map<String, String> subFormulSubReferenceRemiseMap = new LinkedHashMap<String, String>();
		
		StringBuffer referenceNewRemiseStringBuffer = new StringBuffer();
		Arrays.stream(formuleReferenceRemise.split(formuleReferenceRemiseSplit)).forEach(subFormul -> {
			referenceNewRemiseStringBuffer
					.append(buildFakeSubReferenceRemise(formuleReferenceRemise, subFormul, subFormulSubReferenceRemiseMap));
			log.info("subFormul {}",subFormul);
		});
		
		
		
		
		referenceRemiseGenerated.setReferenceRemiseGenerated(referenceNewRemiseStringBuffer.toString());
		log.info("referenceRemiseGenerated.setReferenceRemiseGenerated {}",referenceRemiseGenerated.getReferenceRemiseGenerated());
		log.info("subFormulSubReferenceRemiseMap {}",subFormulSubReferenceRemiseMap);

		log.info("********************* End buildFakeReferenceRemise *********************");
		return referenceRemiseGenerated;
		
	}
	
	

	public ReferenceRemiseGenerated buildReferenceRemise(User user) {
		log.info("********************* Begin buildReferenceRemise *********************");
		ReferenceRemiseGenerated referenceRemiseGenerated = new ReferenceRemiseGenerated();

		Map<String, String> subFormulSubReferenceRemiseMap = new LinkedHashMap<String, String>();

		String formuleReferenceRemise = user.getParamRefRemise().getFormule();
		StringBuffer referenceNewRemiseStringBuffer = new StringBuffer();
		Arrays.stream(formuleReferenceRemise.split(formuleReferenceRemiseSplit)).forEach(subFormul -> {
			referenceNewRemiseStringBuffer
					.append(buildSubReferenceRemise(user, subFormul, subFormulSubReferenceRemiseMap));
		});

		referenceRemiseGenerated.setReferenceRemiseGenerated(referenceNewRemiseStringBuffer.toString());

		int referenceNewRemiseLength = getReferenceNewRemiseLength(subFormulSubReferenceRemiseMap);
		
		
		if(referenceNewRemiseLength < referenceRemiseMaxLength)
		{
			int minReferenceNewRemiseLength = referenceRemiseMaxLength  - referenceNewRemiseLength;
			
			if ((subFormulSubReferenceRemiseMap.get(subReferenceRemiseS.getSubFormul()) != null)
					&& (subFormulSubReferenceRemiseMap.get(subReferenceRemiseSD.getSubFormul()) != null)) {
				    fromatSAndSDLeftPad(subFormulSubReferenceRemiseMap,minReferenceNewRemiseLength);
				
			} else {
				if (subFormulSubReferenceRemiseMap.get(subReferenceRemiseS.getSubFormul()) != null) {
					fromatSLeftPad(subFormulSubReferenceRemiseMap,minReferenceNewRemiseLength);
				}

				if (subFormulSubReferenceRemiseMap.get(subReferenceRemiseSD.getSubFormul()) != null) {
					fromatSDLeftPad(subFormulSubReferenceRemiseMap,minReferenceNewRemiseLength);
				}
			}
			referenceRemiseGenerated.setReferenceRemiseFromated(subFormulSubReferenceRemiseMap.entrySet().stream()
					.map(Map.Entry::getValue).collect(Collectors.joining()));
		}else if(referenceNewRemiseLength == referenceRemiseMaxLength)
		{
			referenceRemiseGenerated.setReferenceRemiseFromated(subFormulSubReferenceRemiseMap.entrySet().stream()
					.map(Map.Entry::getValue).collect(Collectors.joining()));
		}
		log.info("********************* End buildReferenceRemise *********************");
		return referenceRemiseGenerated;
	}

	private void fromatSDLeftPad(Map<String, String> subFormulSubReferenceRemiseMap, int minReferenceNewRemiseLength) {
		// SD
		if (subFormulSubReferenceRemiseMap.get(subReferenceRemiseSD.getSubFormul()) != null) {
			int lenght = subFormulSubReferenceRemiseMap.get(subReferenceRemiseSD.getSubFormul()).length();
			String newSD =  StringUtils.leftPad(subFormulSubReferenceRemiseMap.get(subReferenceRemiseSD.getSubFormul()), 
					lenght + minReferenceNewRemiseLength,
					subFormulReferenceRemiseSDOrSDefaultDigit);
			subFormulSubReferenceRemiseMap.put(subReferenceRemiseSD.getSubFormul(), newSD);
		}
	}
	
	private void fromatSLeftPad(Map<String, String> subFormulSubReferenceRemiseMap, int minReferenceNewRemiseLength) {
		// S
		if (subFormulSubReferenceRemiseMap.get(subReferenceRemiseS.getSubFormul()) != null) {
			int lenght = subFormulSubReferenceRemiseMap.get(subReferenceRemiseS.getSubFormul()).length();
			String newS =  StringUtils.leftPad(subFormulSubReferenceRemiseMap.get(subReferenceRemiseS.getSubFormul()), 
					lenght + minReferenceNewRemiseLength,
					subFormulReferenceRemiseSDOrSDefaultDigit);
			log.info("newS {}",newS);
			subFormulSubReferenceRemiseMap.put(subReferenceRemiseS.getSubFormul(), newS);
		}
	}
	
	private void fromatSAndSDLeftPad(Map<String, String> subFormulSubReferenceRemiseMap, int minReferenceNewRemiseLength) {
		// SD
		if (subFormulSubReferenceRemiseMap.get(subReferenceRemiseSD.getSubFormul()) != null) {
			int lenght = subFormulSubReferenceRemiseMap.get(subReferenceRemiseSD.getSubFormul()).length();
			String newSD =  StringUtils.leftPad(subFormulSubReferenceRemiseMap.get(subReferenceRemiseSD.getSubFormul()), 
					lenght + minReferenceNewRemiseLength,
					subFormulReferenceRemiseSDOrSDefaultDigit);
			subFormulSubReferenceRemiseMap.put(subReferenceRemiseSD.getSubFormul(), newSD);
		}
	}
	
	
	

	private void fromatS(Map<String, String> subFormulSubReferenceRemiseMap, int extraReferenceNewRemiseLength) {
		// S
		if (subFormulSubReferenceRemiseMap.get(subReferenceRemiseS.getSubFormul()) != null) {
			String newS = subFormulSubReferenceRemiseMap.get(subReferenceRemiseS.getSubFormul()).substring(
							 extraReferenceNewRemiseLength,
					subFormulSubReferenceRemiseMap.get(subReferenceRemiseS.getSubFormul()).length());
			subFormulSubReferenceRemiseMap.put(subReferenceRemiseS.getSubFormul(), newS);
		}
	}

	private void fromatSAndSD(Map<String, String> subFormulSubReferenceRemiseMap, int extraReferenceNewRemiseLength) {
		int extraIndexEndS = 0;
		// S
		if (subFormulSubReferenceRemiseMap.get(subReferenceRemiseS.getSubFormul()) != null) {
			extraIndexEndS = extraReferenceNewRemiseLength / 2;
			String newS = subFormulSubReferenceRemiseMap.get(subReferenceRemiseS.getSubFormul())
					.substring(
							extraIndexEndS,
							subFormulSubReferenceRemiseMap.get(subReferenceRemiseS.getSubFormul()).length()
							);
			subFormulSubReferenceRemiseMap.put(subReferenceRemiseS.getSubFormul(), newS);
		}
		// SD
		if (subFormulSubReferenceRemiseMap.get(subReferenceRemiseSD.getSubFormul()) != null) {
			int extraIndexEndSD = extraReferenceNewRemiseLength - extraIndexEndS;
			String newSD = subFormulSubReferenceRemiseMap.get(subReferenceRemiseSD.getSubFormul())
					.substring(
							extraIndexEndSD,
							subFormulSubReferenceRemiseMap.get(subReferenceRemiseSD.getSubFormul()).length()
							);
			subFormulSubReferenceRemiseMap.put(subReferenceRemiseSD.getSubFormul(), newSD);
		}
	}
	
	
	private void fromatSD(Map<String, String> subFormulSubReferenceRemiseMap, int extraReferenceNewRemiseLength) {
		// SD
		
		if (subFormulSubReferenceRemiseMap.get(subReferenceRemiseSD.getSubFormul()) != null) {
			String newSD = subFormulSubReferenceRemiseMap.get(subReferenceRemiseSD.getSubFormul()).substring(
							extraReferenceNewRemiseLength,
					subFormulSubReferenceRemiseMap.get(subReferenceRemiseSD.getSubFormul()).length());
			subFormulSubReferenceRemiseMap.put(subReferenceRemiseSD.getSubFormul(), newSD);
		}

	}
	
	
	

	private int getReferenceNewRemiseLength(Map<String, String> subFormulSubReferenceRemiseMap) {
		return subFormulSubReferenceRemiseMap.entrySet().stream().map(Map.Entry::getValue).mapToInt(String::length)
				.sum();
	}

	private String buildSubReferenceRemise(User user, String subFormul,
			Map<String, String> subFormulSubReferenceRemiseMap) {
		log.info("********************* Begin buildSubReferenceRemise *********************");
		for (SubReferenceRemise subReferenceRemise : subReferenceRemises) {
			Optional<String> subReferenceRemiseOptional = subReferenceRemise
					.buildSubReferenceRemiseIfNecessary(subFormul, user);
			if (subReferenceRemiseOptional.isPresent()) {
				subFormulSubReferenceRemiseMap.put(subReferenceRemise.getSubFormul(), subReferenceRemiseOptional.get());
				return subReferenceRemiseOptional.get();
			}
		}
		log.info("********************* End buildSubReferenceRemise *********************");
		return "";
	}
	
	
	private String buildFakeSubReferenceRemise(String formuleReferenceRemise, String subFormul,
			Map<String, String> subFormulSubReferenceRemiseMap) {
		log.info("********************* Begin buildSubReferenceRemise *********************");
		for (SubReferenceRemise subReferenceRemise : subReferenceRemises) {
			Optional<String> subReferenceRemiseOptional = subReferenceRemise
					.buildFakeSubReferenceRemiseIfNecessary(subFormul, formuleReferenceRemise,"");
			if (subReferenceRemiseOptional.isPresent()) {
				subFormulSubReferenceRemiseMap.put(subReferenceRemise.getSubFormul(), subReferenceRemiseOptional.get());
				return subReferenceRemiseOptional.get();
			}
		}
		log.info("********************* End buildSubReferenceRemise *********************");
		return "";
	}
	
	

}

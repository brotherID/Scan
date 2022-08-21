package dev.procheck.capweb.common.reference.remise.generator;

import java.util.Optional;

import dev.procheck.capweb.entities.User;

public interface SubReferenceRemise {
	
	String getSubFormul();
	
	Optional<String> buildSubReferenceRemiseIfNecessary(String subFormul,User user);
	
	Optional<String> buildFakeSubReferenceRemiseIfNecessary(String subFormul,String formuleReferenceRemise,String capturePoint);

}

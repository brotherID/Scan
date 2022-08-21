package dev.procheck.capweb.common.reference.remise.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import dev.procheck.capweb.entities.ParamRefRemise;
import dev.procheck.capweb.entities.User;

@SpringBootTest
public class ReferenceRemiseGeneratorEngineTest {

	@Autowired
	private ReferenceRemiseGeneratorEngine referenceRemiseGeneratorEngine;

	@Test
	void testBuildReferenceRemise() {

		User user = new User();
		ParamRefRemise paramRefRemise = new ParamRefRemise();
		paramRefRemise.setFormule("%W%S%SD%M%");
		user.setParamRefRemise(paramRefRemise);
//		assertEquals("050722202218407001",
//				referenceRemiseGeneratorEngine.buildReferenceRemise(user).getReferenceRemiseFromated());
		assertEquals(true, referenceRemiseGeneratorEngine.buildReferenceRemise(user).isHasreferenceRemiseFromated());
//		assertEquals("050722202218407001",
//				referenceRemiseGeneratorEngine.buildReferenceRemise(user).getReferenceRemiseGenerated());

	}

}

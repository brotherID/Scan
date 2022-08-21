package dev.procheck.capweb.common.reference.remise.generator;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import dev.procheck.capweb.PKCapWeb;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PKCapWeb.class})
@Slf4j
public class SubReferenceRemiseSDTest {
	
	@Autowired
    private SubReferenceRemiseSD subReferenceRemiseSD;
 
    @Test
	public void testbuildSubReferenceRemiseIfNecessary() {
		
	}

}

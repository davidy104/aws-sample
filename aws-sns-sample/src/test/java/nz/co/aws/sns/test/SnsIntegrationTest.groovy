package nz.co.aws.sns.test;

import static org.junit.Assert.*
import groovy.util.logging.Slf4j
import nz.co.aws.sns.config.ApplicationContextConfig

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationContextConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Slf4j
class SnsIntegrationTest {
	
	@Test
	public void test() {
		
	}

}

package nz.co.aws.sns.endpoint.email.test;

import static org.junit.Assert.*
import groovy.util.logging.Slf4j
import nz.co.aws.sns.endpoint.email.config.ApplicationContextConfig

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationContextConfig.class)
@Slf4j
class SnsEmailEndpointIntegrationTest {

	@Test
	public void test() {
		Thread.sleep(10000)
	}
}

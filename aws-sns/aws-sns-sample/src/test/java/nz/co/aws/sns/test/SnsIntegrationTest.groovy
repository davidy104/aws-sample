package nz.co.aws.sns.test;

import static org.junit.Assert.*
import groovy.util.logging.Slf4j

import javax.annotation.Resource

import nz.co.aws.sns.AwsSNSGeneralService
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

	static final String TEST_TOPIC_NAME="jysnstopic01"
	static final String TEST_TOPIC_ARN="arn:aws:sns:ap-southeast-2:654628234242:jysnstopic01"
	static final String TEST_EMAIL_ENDPOINT="david.yuan@propellerhead.co.nz"
	static final String TEST_MESSAGE="this is a test message from SNS"

	@Resource
	AwsSNSGeneralService awsSNSGeneralService

	@Test
	void testSendMessageToEmail() {
		String subscriptionArn = awsSNSGeneralService.subscribeTopic(TEST_TOPIC_ARN, "email", TEST_EMAIL_ENDPOINT)
		assertNotNull(subscriptionArn)
		log.info "subscriptionArn: {} $subscriptionArn"
		String messageId = awsSNSGeneralService.publishTopic(TEST_TOPIC_ARN, TEST_MESSAGE)
		assertNotNull(messageId)
		log.info "messageId: {} $messageId"
	}
}

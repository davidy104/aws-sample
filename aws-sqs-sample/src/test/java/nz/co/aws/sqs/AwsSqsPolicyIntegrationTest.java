package nz.co.aws.sqs;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import nz.co.aws.config.AwsConfigBean;
import nz.co.aws.sqs.config.ApplicationContextConfig;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.amazonaws.auth.policy.Action;
import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.actions.SQSActions;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationContextConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class AwsSqsPolicyIntegrationTest {

	@Resource
	private AmazonSQS amazonSqs;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AwsSqsIntegrationTest.class);

	private static final String TEST_QUEUE_NAME = "davidTestQueue";

	private static final String TEST_MESSAGE = "this is a testing message";

	private String createQueueUrl;

	@Resource
	private AwsConfigBean awsConfigBean;

	@After
	public void cleanUp() {
		if (!StringUtils.isEmpty(createQueueUrl)) {
			amazonSqs.deleteQueue(createQueueUrl);
		}
	}

	@Test
	@Ignore
	public void testAllowAll() {
		Action action = SQSActions.AllSQSActions;

		String arn = "arn:aws:sqs:" + Regions.AP_SOUTHEAST_2 + ":"
				+ awsConfigBean.getAccessKey() + ":" + TEST_QUEUE_NAME + "";

		Policy sqsPolicy = new Policy().withStatements(new Statement(
				Statement.Effect.Allow).withPrincipals(Principal.AllUsers)
				.withResources(new com.amazonaws.auth.policy.Resource(arn))
				.withActions(action));

		CreateQueueRequest createQueueRequest = new CreateQueueRequest(
				TEST_QUEUE_NAME);
		createQueueRequest.addAttributesEntry("Policy", sqsPolicy.toJson());
		CreateQueueResult createQueueResult = amazonSqs
				.createQueue(createQueueRequest);
		createQueueUrl = createQueueResult.getQueueUrl();
		assertNotNull(createQueueUrl);
		LOGGER.info("createQueueUrl:{} ", createQueueUrl);
	}

}

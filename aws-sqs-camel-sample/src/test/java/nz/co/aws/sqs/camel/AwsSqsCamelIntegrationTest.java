package nz.co.aws.sqs.camel;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import nz.co.aws.sqs.camel.config.ApplicationContextConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationContextConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class AwsSqsCamelIntegrationTest {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AwsSqsCamelIntegrationTest.class);

	@Resource
	private AmazonSQS amazonSqs;

	@Resource
	private AwsSqsCamelGeneralService awsSqsCamelGeneralService;

	private static final String TEST_QUEUE_NAME = "jyqueue02";

	private static final String TEST_MESSAGE = "this is a testing message";

	private String createQueueUrl;

	@Before
	public void initialize() throws Exception {

		CreateQueueResult createQueueResult = amazonSqs
				.createQueue(TEST_QUEUE_NAME);
		createQueueUrl = createQueueResult.getQueueUrl();
		LOGGER.info("createQueueUrl:{} ", createQueueUrl);

		this.sendTestMessages();

	}

//	@After
//	public void cleanUp() {
//		if (!StringUtils.isEmpty(createQueueUrl)) {
//			amazonSqs.deleteQueue(createQueueUrl);
//		}
//	}

	@Test
	public void testReceiveMessages() {
		awsSqsCamelGeneralService.getMessagesFromQueue1(TEST_QUEUE_NAME);
	}

	private void sendTestMessages() throws Exception {
		Map<String, MessageAttributeValue> messageAttributes = new HashMap<String, MessageAttributeValue>();

		// String attribute
		messageAttributes.put("imageName", new MessageAttributeValue()
				.withDataType("String").withStringValue("James"));
		// Number attribute
		messageAttributes.put("fileLength", new MessageAttributeValue()
				.withDataType("Number").withStringValue("84746"));

		SendMessageRequest sendMessageRequest = new SendMessageRequest();
		sendMessageRequest.withMessageBody(TEST_MESSAGE);
		sendMessageRequest.withQueueUrl(createQueueUrl);
		sendMessageRequest.withMessageAttributes(messageAttributes);

		SendMessageResult sendMessageResult = amazonSqs
				.sendMessage(sendMessageRequest);
		LOGGER.info("sendMessageResult:{} ", sendMessageResult);

		Thread.sleep(1000);
	}
}

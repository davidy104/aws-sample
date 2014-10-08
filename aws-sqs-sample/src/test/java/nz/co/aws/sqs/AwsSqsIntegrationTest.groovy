package nz.co.aws.sqs;

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import groovy.util.logging.Slf4j;

import java.nio.ByteBuffer
import java.util.Map.Entry

import javax.annotation.Resource

import nz.co.aws.AwsClientUtils
import nz.co.aws.sqs.config.ApplicationContextConfig

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.MessageAttributeValue
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sqs.model.SendMessageRequest
import com.amazonaws.services.sqs.model.SendMessageResult

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationContextConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Slf4j
class AwsSqsIntegrationTest {

	@Resource
	private AwsSqsGeneralService awsSqsGeneralService;

	@Resource
	private AmazonSQS amazonSqs;

	private static final String BINARY_IMAGE = "/james.jpg";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AwsSqsIntegrationTest.class);

	private static final String TEST_QUEUE_NAME = "davidTestQueue";

	private static final String TEST_MESSAGE = "this is a testing message";

	private String createQueueUrl;

	@Before
	public void initialize() {
		// must wait for 60sec before create queue with the same name
		// String foundQueueUrl = awsSqsGeneralService
		// .getQueueUrl(TEST_QUEUE_NAME);
		// if (!StringUtils.isEmpty(foundQueueUrl)) {
		// createQueueUrl = foundQueueUrl;
		// } else {

		createQueueUrl = awsSqsGeneralService
				.createQueue(TEST_QUEUE_NAME, null);
		// }
		LOGGER.info("createQueueUrl:{} ", createQueueUrl);
	}

	@After
	public void cleanUp() {
		if (!StringUtils.isEmpty(createQueueUrl)) {
			awsSqsGeneralService.deleteQueue(createQueueUrl);
		}
	}

	@Test
	public void testDeleteQueue() {
		// https://sqs.ap-southeast-2.amazonaws.com/887277183656/davidTestQueue
		// amazonSqs
		// .deleteQueue("https://sqs.ap-southeast-2.amazonaws.com/887277183656/davidTestQueue");
	}

	@Test
	public void testListQueues() {

		List<String> queueUrls = awsSqsGeneralService.listQueues();
		assertNotNull(queueUrls);
		for (String queueUrl : queueUrls) {
			LOGGER.info("queueUrl:{} ", queueUrl);
		}
	}

	@Test
	public void testBasicCrdMessage() {
		String foundQueueUrl = awsSqsGeneralService
				.getQueueUrl(TEST_QUEUE_NAME);
		assertEquals(foundQueueUrl, createQueueUrl);

		List<Message> messages = awsSqsGeneralService
				.getMessagesFromQueue(foundQueueUrl);
		assertEquals(messages.size(), 0);

		awsSqsGeneralService.sendMessageToQueue(foundQueueUrl, TEST_MESSAGE);
		messages = awsSqsGeneralService.getMessagesFromQueue(foundQueueUrl);
		assertEquals(messages.size(), 1);

		Message message = messages.get(0);
		assertNotNull(message);
		LOGGER.info("message:{} ", message);

		awsSqsGeneralService.deleteMessageFromQueue(foundQueueUrl, message);
		messages = awsSqsGeneralService.getMessagesFromQueue(foundQueueUrl);
		assertEquals(messages.size(), 0);
	}

	@Test
	public void testMessageWithAttributes() throws Exception {

		File uploadFile = AwsClientUtils.getFileFromClasspath(BINARY_IMAGE);
		long fileLength = uploadFile.length();
		byte[] fileBytes = FileUtils.readFileToByteArray(uploadFile);

		Map<String, MessageAttributeValue> messageAttributes = new HashMap<String, MessageAttributeValue>();

		// String attribute
		messageAttributes.put("imageName", new MessageAttributeValue()
				.withDataType("String").withStringValue("James"));
		// Number attribute
		messageAttributes.put("fileLength",
				new MessageAttributeValue().withDataType("Number")
						.withStringValue(String.valueOf(fileLength)));
		// binary attribute
		// messageAttributes.put("imageBytes",
		// new MessageAttributeValue().withDataType("Binary")
		// .withBinaryValue(ByteBuffer.wrap(fileBytes)));

		// inary-custom attribute
		messageAttributes.put("NBAPlayer",
				new MessageAttributeValue().withDataType("Binary.JPEG")
						.withBinaryValue(ByteBuffer.wrap(fileBytes)));
		SendMessageRequest sendMessageRequest = new SendMessageRequest();
		sendMessageRequest.withMessageBody(TEST_MESSAGE);
		sendMessageRequest.withQueueUrl(createQueueUrl);
		sendMessageRequest.withMessageAttributes(messageAttributes);

		SendMessageResult sendMessageResult = amazonSqs
				.sendMessage(sendMessageRequest);
		LOGGER.info("sendMessageResult:{} ", sendMessageResult);

		Thread.sleep(3000);

		// in order to get message attributes, The attribute names need to be
		// specified as a property of the ReceiveMessageRequest
		List<String> messageAttributeNames = new ArrayList<>();
		messageAttributeNames.add("imageName");
		messageAttributeNames.add("fileLength");
		messageAttributeNames.add("NBAPlayer");

		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(
				createQueueUrl);
		receiveMessageRequest.setMessageAttributeNames(messageAttributeNames);

		List<Message> messages = amazonSqs
				.receiveMessage(receiveMessageRequest).getMessages();

		assertEquals(messages.size(), 1);
		Message message = messages.get(0);
		assertNotNull(message);
		LOGGER.info("message:{} ", message);

		for (Entry<String, MessageAttributeValue> entry : message
				.getMessageAttributes().entrySet()) {
			LOGGER.info("  Attribute");
			LOGGER.info("    Name:  " + entry.getKey());
			LOGGER.info("    Value: " + entry.getValue());
		}

		awsSqsGeneralService.deleteMessageFromQueue(createQueueUrl, message);
		messages = awsSqsGeneralService.getMessagesFromQueue(createQueueUrl);
		assertEquals(messages.size(), 0);
	}

}

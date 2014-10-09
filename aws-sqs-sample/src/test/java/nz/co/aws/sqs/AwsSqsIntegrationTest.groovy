package nz.co.aws.sqs;

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import groovy.util.logging.Slf4j

import java.nio.ByteBuffer

import javax.annotation.Resource

import nz.co.aws.sqs.config.ApplicationContextConfig

import org.apache.commons.io.IOUtils
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
	AwsSqsGeneralService awsSqsGeneralService

	@Resource
	AmazonSQS amazonSqs

	static final String BINARY_IMAGE = "/image/james.jpg"
	static final String TEST_QUEUE_NAME="jyqueue01"
	static final String TEST_CREATE_QUEUE_NAME = "davidTestQueue"
	static final String TEST_MESSAGE = "this is a testing message"

	byte[] testImageBytes
	String createQueueUrl

	@Before
	void setUp(){
		InputStream is = AwsSqsIntegrationTest.class.getResourceAsStream(BINARY_IMAGE)
		try {
			testImageBytes = IOUtils.toByteArray(is)
		} catch (e) {
			e.printStackTrace()
		} finally {
			is.close()
		}
	}

	@After
	public void cleanUp() {
		if (createQueueUrl) {
			awsSqsGeneralService.deleteQueue(createQueueUrl)
		}
	}

	@Test
	void testCreateQueue(){
		createQueueUrl = awsSqsGeneralService
				.createQueue(TEST_CREATE_QUEUE_NAME, null)
	}


	@Test
	void testDeleteQueue() {
		// https://sqs.ap-southeast-2.amazonaws.com/887277183656/davidTestQueue
		// amazonSqs
		// .deleteQueue("https://sqs.ap-southeast-2.amazonaws.com/887277183656/davidTestQueue");
	}

	@Test
	void testListQueues() {
		List<String> queueUrls = awsSqsGeneralService.listQueues()
		assertNotNull(queueUrls)
		queueUrls.each{ log.info "queueUrl:{} $it" }
	}

	@Test
	void testSendMessage(){
		String foundQueueUrl = awsSqsGeneralService
				.getQueueUrl(TEST_QUEUE_NAME)
		List<Message> messages = awsSqsGeneralService
				.getMessagesFromQueue(foundQueueUrl)
		assertEquals(messages.size(), 0)

		awsSqsGeneralService.sendMessageToQueue(foundQueueUrl, TEST_MESSAGE)
		messages = awsSqsGeneralService.getMessagesFromQueue(foundQueueUrl)
		assertEquals(messages.size(), 1)

		Message message = messages.get(0)
		assertNotNull(message)
		log.info "message:{} $message"
	}

	@Test
	void testDeleteMessage(){
		def msgSize
		Message deleteMessage
		String foundQueueUrl = awsSqsGeneralService
				.getQueueUrl(TEST_QUEUE_NAME)
		List<Message> messages = awsSqsGeneralService
				.getMessagesFromQueue(foundQueueUrl)
		if(messages && !messages.isEmpty()){
			msgSize = messages.size()
			deleteMessage = messages.get(0)
			awsSqsGeneralService.deleteMessageFromQueue(foundQueueUrl, deleteMessage)
			messages = awsSqsGeneralService.getMessagesFromQueue(foundQueueUrl)
			assertEquals(messages.size(), msgSize-1);
		} else {
			log.info "there is no message left in queue for testing."
			assertEquals(messages.size(), 0);
		}
	}


	@Test
	void testMessageWithAttributes() {
		String testQueueUrl = awsSqsGeneralService
				.getQueueUrl(TEST_QUEUE_NAME)

		Map<String, MessageAttributeValue> messageAttributes = [:]

		// String attribute
		messageAttributes.put("imageName", new MessageAttributeValue()
				.withDataType("String").withStringValue("James"))
		// Number attribute
		messageAttributes.put("fileLength",
				new MessageAttributeValue().withDataType("Number")
				.withStringValue(String.valueOf(testImageBytes.length)))
		// inary-custom attribute
		messageAttributes.put("NBAPlayer",
				new MessageAttributeValue().withDataType("Binary.JPEG")
				.withBinaryValue(ByteBuffer.wrap(testImageBytes)))
		SendMessageRequest sendMessageRequest = new SendMessageRequest()
		sendMessageRequest.withMessageBody(TEST_MESSAGE)
		sendMessageRequest.withQueueUrl(testQueueUrl)
		sendMessageRequest.withMessageAttributes(messageAttributes)

		SendMessageResult sendMessageResult = amazonSqs
				.sendMessage(sendMessageRequest)
		log.info "sendMessageResult:{} $sendMessageResult"

		Thread.sleep(3000)

		// in order to get message attributes, The attribute names need to be
		// specified as a property of the ReceiveMessageRequest
		List<String> messageAttributeNames = []
		messageAttributeNames << "imageName"
		messageAttributeNames << "fileLength"
		messageAttributeNames << "NBAPlayer"

		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(
				testQueueUrl)
		receiveMessageRequest.setMessageAttributeNames(messageAttributeNames)

		List<Message> messages = amazonSqs
				.receiveMessage(receiveMessageRequest).messages
		assertFalse(messages.isEmpty())

		Message message = messages.get(0)
		assertNotNull(message)
		log.info "message:{} $message"

		message.messageAttributes.each{k,v->
			log.info "Attribute"
			log.info "Name:  $k"
			log.info "Value: $v"
		}
	}

}

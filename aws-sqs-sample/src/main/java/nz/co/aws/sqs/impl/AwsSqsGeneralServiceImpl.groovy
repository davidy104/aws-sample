package nz.co.aws.sqs;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import nz.co.aws.config.AwsConfigBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

@Service
public class AwsSqsGeneralServiceImpl implements AwsSqsGeneralService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AwsSqsGeneralServiceImpl.class);

	@Resource
	private AwsConfigBean awsConfigBean;

	@Resource
	private AmazonSQS amazonSqs;

	@Override
	public String createQueue(String queueName, Map<String, String> attributes) {
		LOGGER.info("createQueue start:{} ", queueName);
		CreateQueueRequest createQueueRequest = new CreateQueueRequest(
				queueName);
		if (attributes != null && attributes.size() > 0) {
			createQueueRequest.setAttributes(attributes);
		}
		CreateQueueResult createQueueResult = amazonSqs
				.createQueue(createQueueRequest);

		LOGGER.info("createQueue end:{} ", createQueueResult);
		return createQueueResult.getQueueUrl();
	}

	@Override
	public void deleteQueue(String queueUrl) {
		LOGGER.info("deleteQueue start:{} ", queueUrl);
		amazonSqs.deleteQueue(queueUrl);
		LOGGER.info("deleteQueue end");
	}

	@Override
	public String getQueueUrl(String queueName) {
		LOGGER.info("getQueueUrl start:{} ", queueName);
		GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest(
				queueName);
		return amazonSqs.getQueueUrl(getQueueUrlRequest).getQueueUrl();
	}

	@Override
	public List<String> listQueues() {
		ListQueuesResult listQueuesResult = amazonSqs.listQueues();
		if (listQueuesResult != null) {
			return listQueuesResult.getQueueUrls();
		}
		return null;
	}

	@Override
	public String sendMessageToQueue(String queueUrl, String message) {
		LOGGER.info("sendMessageToQueue:{} ", queueUrl);
		SendMessageResult messageResult = amazonSqs
				.sendMessage(new SendMessageRequest(queueUrl, message));
		
		if (messageResult != null) {
			LOGGER.info("sendMessageToQueue end:{} ", messageResult);
			return messageResult.getMessageId();
		}
		return null;
	}

	@Override
	public List<Message> getMessagesFromQueue(String queueUrl) {
		LOGGER.info("getMessagesFromQueue start:{} ", queueUrl);
		ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(
				queueUrl);
		return amazonSqs.receiveMessage(receiveMessageRequest).getMessages();
	}

	@Override
	public void deleteMessageFromQueue(String queueUrl, Message message) {
		LOGGER.info("deleteMessageFromQueue start:{} ", queueUrl);
		LOGGER.info("message:{} ", message);
		String messageRecieptHandle = message.getReceiptHandle();
		LOGGER.info("message deleted:{} " + message.getBody() + "."
				+ message.getReceiptHandle());
		amazonSqs.deleteMessage(new DeleteMessageRequest(queueUrl,
				messageRecieptHandle));
	}

}

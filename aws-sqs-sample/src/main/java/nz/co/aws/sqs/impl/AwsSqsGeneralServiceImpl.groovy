package nz.co.aws.sqs.impl;

import groovy.util.logging.Slf4j

import javax.annotation.Resource

import nz.co.aws.config.AwsConfigBean
import nz.co.aws.sqs.AwsSqsGeneralService

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.model.CreateQueueRequest
import com.amazonaws.services.sqs.model.CreateQueueResult
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.amazonaws.services.sqs.model.GetQueueUrlRequest
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sqs.model.SendMessageRequest

@Service
@Slf4j
class AwsSqsGeneralServiceImpl implements AwsSqsGeneralService {

	@Resource
	AwsConfigBean awsConfigBean

	@Resource
	AmazonSQS amazonSqs

	@Override
	String createQueue(final String queueName, final Map<String, String> attributes) {
		log.info "createQueue start:{} $queueName"
		CreateQueueRequest createQueueRequest = new CreateQueueRequest(
				queueName)
		if (attributes) {
			createQueueRequest.attributes = attributes
		}
		CreateQueueResult createQueueResult = amazonSqs
				.createQueue(createQueueRequest)

		log.info "createQueue end:{} $createQueueResult"
		return createQueueResult.queueUrl
	}

	@Override
	void deleteQueue(final String queueUrl) {
		amazonSqs.deleteQueue(queueUrl)
	}

	@Override
	String getQueueUrl(final String queueName) {
		log.info "getQueueUrl start:{} $queueName"
		return amazonSqs.getQueueUrl(new GetQueueUrlRequest(
		queueName)).queueUrl
	}

	@Override
	List<String> listQueues() {
		return amazonSqs.listQueues().queueUrls
	}

	@Override
	String sendMessageToQueue(final String queueUrl,final String message) {
		return amazonSqs
		.sendMessage(new SendMessageRequest(queueUrl, message)).messageId
	}

	@Override
	List<Message> getMessagesFromQueue(final String queueUrl) {
		return amazonSqs.receiveMessage(new ReceiveMessageRequest(
		queueUrl)).messages
	}

	@Override
	void deleteMessageFromQueue(final String queueUrl,final Message message) {
		amazonSqs.deleteMessage(new DeleteMessageRequest(queueUrl,
				message.receiptHandle))
	}
}

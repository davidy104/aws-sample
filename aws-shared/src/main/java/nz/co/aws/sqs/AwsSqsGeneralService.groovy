package nz.co.aws.sqs;

import com.amazonaws.services.sqs.model.Message

interface AwsSqsGeneralService {

	String createQueue(String queueName,Map<String,String> attributes)

	void deleteQueue(String queueUrl)

	String getQueueUrl(String queueName)

	List<String> listQueues()

	String sendMessageToQueue(String queueUrl, String message)

	List<Message> getMessagesFromQueue(String queueUrl)

	void deleteMessageFromQueue(String queueUrl, Message message)
}

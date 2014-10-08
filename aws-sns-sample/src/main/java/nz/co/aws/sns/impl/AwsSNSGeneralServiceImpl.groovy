package nz.co.aws.sns.impl

import groovy.util.logging.Slf4j

import javax.annotation.Resource

import nz.co.aws.sns.AwsSNSGeneralService

import org.springframework.stereotype.Service

import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sns.model.CreateTopicRequest
import com.amazonaws.services.sns.model.CreateTopicResult
import com.amazonaws.services.sns.model.DeleteTopicRequest
import com.amazonaws.services.sns.model.PublishRequest
import com.amazonaws.services.sns.model.PublishResult
import com.amazonaws.services.sns.model.SubscribeRequest
import com.amazonaws.services.sns.model.SubscribeResult

@Service
@Slf4j
class AwsSNSGeneralServiceImpl implements AwsSNSGeneralService{

	@Resource
	AmazonSNSClient amazonSnsClient

	@Override
	String createTopic(final String topicName) {
		CreateTopicResult createTopicResult = amazonSnsClient.createTopic(new CreateTopicRequest(topicName))
		return createTopicResult.topicArn
	}

	@Override
	String subscribeTopic(final String topicArn,final String protocol,final String endpoint) {
		SubscribeResult subscribeResult = amazonSnsClient.subscribe(new SubscribeRequest(topicArn, protocol, endpoint))
		return subscribeResult.subscriptionArn
	}

	@Override
	String publishTopic(final String topicArn,final String message) {
		PublishResult publishResult = amazonSnsClient.publish(new PublishRequest(topicArn, message))
		return publishResult.messageId
	}

	@Override
	void deleteTopic(String topicArn) {		
		amazonSnsClient.deleteTopic(new DeleteTopicRequest(topicArn))
	}
}

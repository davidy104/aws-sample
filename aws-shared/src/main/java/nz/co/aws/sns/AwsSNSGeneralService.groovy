package nz.co.aws.sns;

interface AwsSNSGeneralService {
	String createTopic(String topicName)
	String subscribeTopic(String topicArn, String protocol, String endpoint)
	String publishTopic(String topicArn, String message)
	void deleteTopic(String topicArn)
}

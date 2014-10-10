package nz.co.aws.sns.endpoint.http

import groovy.transform.ToString

@ToString(includeNames = true, includeFields=true)
class SnsMessage {
	String type
	String token
	String messageId
	String topicArn
	String subject
	String message
	String timestamp
	String signatureVersion
	String signature
	String signingCertURL
	String unsubscribeURL
	String subscribeURL
}

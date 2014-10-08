package nz.co.aws.config;

import javax.annotation.Resource

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment

import com.amazonaws.ClientConfiguration
import com.amazonaws.Protocol
import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.sns.AmazonSNSClient
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClient

@Configuration
@PropertySource("classpath:aws.properties")
class AwsConfiguration {

	@Resource
	Environment environment

	static final String BUCKET_NAME = "aws.bucketName"
	static final String ACCESS_KEY = "aws.accessKey"
	static final String SECRET_KEY = "aws.secretKey"

	@Bean
	AwsConfigBean awsConfigBean() {
		return new AwsConfigBean(accessKey:environment.getRequiredProperty(ACCESS_KEY),secretKey:environment.getRequiredProperty(SECRET_KEY),bucketName:environment.getRequiredProperty(BUCKET_NAME))
	}

	@Bean
	AmazonS3 amazonS3() {
		final ClientConfiguration clientConfig = new ClientConfiguration()
		clientConfig.setProtocol(Protocol.HTTP)
		AmazonS3 amazonS3 = new AmazonS3Client(credentials(), clientConfig)
		amazonS3.setRegion(region())
		return amazonS3
	}

	@Bean
	AmazonSQS amazonSqs() {
		AmazonSQS amazonSqs = new AmazonSQSClient(credentials())
		amazonSqs.setRegion(region())
		return amazonSqs
	}

	@Bean
	AmazonSNSClient amazonSnsClient(){
		AmazonSNSClient snsClient = new AmazonSNSClient(credentials())
		snsClient.setRegion(region())
		return snsClient
	}

	@Bean
	AWSCredentials credentials() {
		AwsConfigBean awsClientConfig = awsConfigBean()
		return new BasicAWSCredentials(awsClientConfig.getAccessKey(),
		awsClientConfig.getSecretKey())
	}

	@Bean
	Region region(){
		return Region.getRegion(Regions.AP_SOUTHEAST_2)
	}
}

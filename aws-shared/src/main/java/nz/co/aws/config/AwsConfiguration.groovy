package nz.co.aws.config;

import javax.annotation.Resource;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;

@Configuration
@PropertySource("classpath:aws.properties")
public class AwsConfiguration {

	@Resource
	private Environment environment;

	private static final String BUCKET_NAME = "aws.bucketName";
	private static final String ACCESS_KEY = "aws.accessKey";
	private static final String SECRET_KEY = "aws.secretKey";

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public AwsConfigBean awsConfigBean() {
		return AwsConfigBean.getBuilder(
				environment.getRequiredProperty(ACCESS_KEY),
				environment.getRequiredProperty(SECRET_KEY),
				environment.getRequiredProperty(BUCKET_NAME)).build();
	}

	@Bean
	public AmazonS3 amazonS3() {
		final ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.setProtocol(Protocol.HTTP);
		AmazonS3 amazonS3 = new AmazonS3Client(credentials(), clientConfig);
//		Region region = Region.getRegion(Regions.AP_SOUTHEAST_2);
//		amazonS3.setRegion(region);
		return amazonS3;
	}

	@Bean
	public AmazonSQS amazonSqs() {
		AmazonSQS amazonSqs = new AmazonSQSClient(credentials());
		Region region = Region.getRegion(Regions.AP_SOUTHEAST_2);
		amazonSqs.setRegion(region);
		return amazonSqs;
	}

	private AWSCredentials credentials() {
		AwsConfigBean awsClientConfig = awsConfigBean();
		return new BasicAWSCredentials(awsClientConfig.getAccessKey(),
				awsClientConfig.getSecretKey());
	}

}

package nz.co.aws.sqs.camel.config;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import nz.co.aws.config.AwsConfiguration;
import nz.co.aws.config.CamelActivemqConfig;
import nz.co.aws.config.CamelSpringContextConfig;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.amazonaws.services.sqs.AmazonSQS;

@Configuration
@ComponentScan(basePackages = "nz.co.aws.sqs.camel")
@Import(value = { AwsConfiguration.class, CamelSpringContextConfig.class,
		CamelActivemqConfig.class })
public class ApplicationContextConfig {

	@Resource
	private CamelContext camelContext;

	@Resource
	private AmazonSQS amazonSqs;

	@PostConstruct
	public void initializeCamelContext() throws Exception {
		SpringCamelContext springCamelContext = (SpringCamelContext) camelContext;
		SimpleRegistry registry = new SimpleRegistry();
		registry.put("amazonSqs", amazonSqs);
		springCamelContext.setRegistry(registry);
	}
}

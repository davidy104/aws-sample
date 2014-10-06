package nz.co.aws.s3.camel.config;

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

import com.amazonaws.services.s3.AmazonS3;

@Configuration
@ComponentScan(basePackages = "nz.co.aws.s3.camel")
@Import(value = { AwsConfiguration.class, CamelSpringContextConfig.class,
		CamelActivemqConfig.class })
public class ApplicationContextConfig {

	@Resource
	private CamelContext camelContext;

	@Resource
	private AmazonS3 amazonS3;

	@PostConstruct
	public void initializeCamelContext() throws Exception {
		SpringCamelContext springCamelContext = (SpringCamelContext) camelContext;
		SimpleRegistry registry = new SimpleRegistry();
		registry.put("amazonS3", amazonS3);
		springCamelContext.setRegistry(registry);
	}
}

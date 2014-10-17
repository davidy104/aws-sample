package nz.co.aws.s3.image.config;

import javax.annotation.PostConstruct
import javax.annotation.Resource

import nz.co.aws.config.AwsConfiguration
import nz.co.aws.config.CamelSpringContextConfig
import nz.co.aws.s3.image.route.ImageS3ProcessRoute

import org.apache.camel.CamelContext
import org.apache.camel.impl.SimpleRegistry
import org.apache.camel.spring.SpringCamelContext
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

import com.amazonaws.services.s3.AmazonS3

@Configuration
@ComponentScan(basePackages = "nz.co.aws.s3.image")
@Import(value = [ AwsConfiguration.class, CamelSpringContextConfig.class ])
class ApplicationContextConfig {
	@Resource
	CamelContext camelContext
	@Resource
	AmazonS3 amazonS3
	@Resource
	ImageS3ProcessRoute imageS3ProcessRoute

	@PostConstruct
	void initializeCamelContext() {
		SpringCamelContext springCamelContext = (SpringCamelContext) camelContext
		SimpleRegistry registry = new SimpleRegistry()
		registry.put("amazonS3", amazonS3);
		springCamelContext.setRegistry(registry)
		springCamelContext.addRoutes(imageS3ProcessRoute)
	}
}

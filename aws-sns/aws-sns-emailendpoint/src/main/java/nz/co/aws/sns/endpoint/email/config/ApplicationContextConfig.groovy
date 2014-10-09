package nz.co.aws.sns.endpoint.email.config;

import javax.annotation.Resource

import nz.co.aws.config.AwsConfiguration
import nz.co.aws.config.CamelSpringContextConfig

import org.apache.camel.CamelContext
import org.apache.camel.component.properties.PropertiesComponent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ComponentScan(basePackages = "nz.co.aws.sns.endpoint.email")
@Import(value = [ AwsConfiguration.class, CamelSpringContextConfig.class ])
public class ApplicationContextConfig {

	@Resource
	CamelContext camelContext
	
	@Bean
	static PropertiesComponent properties(){
		PropertiesComponent pcomponent = new PropertiesComponent()
		pcomponent.location = "classpath:mail.config"
		return pcomponent
	}

//	@Bean
//	static PropertyPlaceholderConfigurer properties() {
//		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer()
//		ClassPathResource[] resources =  [
//			new ClassPathResource('mail.config')
//		]
//		ppc.setLocations(resources)
//		ppc.setIgnoreUnresolvablePlaceholders(true)
//		return ppc
//	}


	//	@PostConstruct
	//	void initializeCamelContext() throws Exception {
	//		SpringCamelContext springCamelContext = (SpringCamelContext) camelContext
	//		SimpleRegistry registry = new SimpleRegistry()
	//		registry.put("amazonS3", amazonS3);
	//		springCamelContext.setRegistry(registry)
	//	}
}

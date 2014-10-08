package nz.co.aws.config;

import java.util.concurrent.TimeUnit

import javax.annotation.Resource

import org.apache.camel.CamelContext
import org.apache.camel.ThreadPoolRejectedPolicy
import org.apache.camel.spi.ThreadPoolProfile
import org.apache.camel.spring.CamelBeanPostProcessor
import org.apache.camel.spring.CamelContextFactoryBean
import org.apache.camel.spring.SpringCamelContext
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CamelSpringContextConfig {

	@Resource
	ApplicationContext context

	@Bean
	CamelBeanPostProcessor camelBeanPostProcessor() {
		CamelBeanPostProcessor camelBeanPostProcessor = new CamelBeanPostProcessor()
		camelBeanPostProcessor.setApplicationContext(context)
		return camelBeanPostProcessor
	}

	@Bean
	CamelContext camelContext() throws Exception {
		CamelContextFactoryBean factory = new CamelContextFactoryBean()
		factory.setApplicationContext(context)
		factory.setId("aws-server")
		SpringCamelContext camelContext = factory.getContext()
		camelContext.getExecutorServiceManager().setDefaultThreadPoolProfile(
				genericThreadPoolProfile())
		return camelContext
	}

	@Bean
	ThreadPoolProfile genericThreadPoolProfile() {
		ThreadPoolProfile profile = new ThreadPoolProfile()
		profile.setId("genericThreadPool")
		profile.setKeepAliveTime(120L)
		profile.setPoolSize(2)
		profile.setMaxPoolSize(10)
		profile.setTimeUnit(TimeUnit.SECONDS)
		profile.setRejectedPolicy(ThreadPoolRejectedPolicy.Abort)
		return profile
	}
}

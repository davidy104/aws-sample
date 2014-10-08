package nz.co.aws.config;

import javax.annotation.Resource

import org.apache.activemq.ActiveMQConnectionFactory
import org.apache.activemq.pool.PooledConnectionFactory
import org.apache.camel.component.jms.JmsComponent
import org.apache.camel.component.jms.JmsConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.jms.connection.JmsTransactionManager

@Configuration
@PropertySource("classpath:mq-config.properties")
public class CamelActivemqConfig {
	@Autowired
	PooledConnectionFactory pooledConnectionFactory

	@Resource
	Environment environment

	static final String ACTIVITYMQ_URL = "activitymq_url"
	static final String ACTIVITYMQ_TRANSACTED = "activitymq_transacted"
	static final String ACTIVITYMQ_MAXCONNECTIONS = "activitymq_maxConnections"
	static final String ACTIVITYMQ_SENDTIMEOUT = "activitymq_sendTimeoutInMillis"
	static final String ACTIVITYMQ_WATCHTOPICADVISORIES = "activitymq_watchTopicAdvisories"

	@Bean
	ActiveMQConnectionFactory activeMQConnectionFactory() {
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
				environment.getRequiredProperty(ACTIVITYMQ_URL));
		connectionFactory.setSendTimeout(Integer.valueOf(environment
				.getRequiredProperty(ACTIVITYMQ_SENDTIMEOUT)));
		connectionFactory.setMaxThreadPoolSize(5);
		connectionFactory.setWatchTopicAdvisories(Boolean.valueOf(environment
				.getRequiredProperty(ACTIVITYMQ_WATCHTOPICADVISORIES)));
		connectionFactory.setUseDedicatedTaskRunner(false);
		return connectionFactory
	}

	@Bean(initMethod = "start", destroyMethod = "stop")
	PooledConnectionFactory pooledConnectionFactory() {
		PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory()
		pooledConnectionFactory.setMaxConnections(Integer.valueOf(environment
				.getRequiredProperty(ACTIVITYMQ_MAXCONNECTIONS)))
		pooledConnectionFactory
				.setConnectionFactory(activeMQConnectionFactory())
		return pooledConnectionFactory
	}

	@Bean
	JmsComponent jmsComponent() {
		JmsComponent jmsComponent = new JmsComponent()
		JmsConfiguration jmsConfiguration = new JmsConfiguration()
		jmsConfiguration.setConnectionFactory(pooledConnectionFactory)
		jmsConfiguration.setTransactionManager(jmsTransactionManager())
		jmsConfiguration.setTransacted(Boolean.valueOf(environment
				.getRequiredProperty(ACTIVITYMQ_TRANSACTED)))
		jmsConfiguration.setCacheLevelName("CACHE_CONSUMER")
		jmsComponent.setConfiguration(jmsConfiguration)
		return jmsComponent
	}

	@Bean
	JmsTransactionManager jmsTransactionManager() {
		JmsTransactionManager jmsTransactionManager = new JmsTransactionManager()
		jmsTransactionManager.setConnectionFactory(pooledConnectionFactory)
		return jmsTransactionManager
	}
}

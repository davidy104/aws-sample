package nz.co.aws.sns.endpoint.email.impl

import groovy.util.logging.Slf4j

import org.apache.camel.Consume
import org.springframework.stereotype.Component

@Component
@Slf4j
class EmailListener {
	@Consume(uri = 'imaps://imap.gmail.com?username={{email.username}}&password={{email.password}}&delete=false&unseen=true&consumer.delay=60000')
	Object onMessage(Object message) {
		log.info "***************************Message: {} $message"
		return message
	}
}

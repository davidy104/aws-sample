package nz.co.aws.sqs.camel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("awsSqsGeneralServiceCamelImpl")
public class AwsSqsCamelGeneralService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AwsSqsCamelGeneralService.class);

	@Resource
	private CamelContext camelContext;

	@Produce
	private ProducerTemplate producerTemplate;

	public String sendMessageToQueue(String queueName, String message) {
		LOGGER.info("sendMessageToQueue start:{} ", queueName);
		String endpoint = "aws-sqs://" + queueName
				+ "?amazonSQSClient=#amazonSqs";

		return null;
	}

	public void getMessagesFromQueue(String queueName) {
		LOGGER.info("getMessagesFromQueue start:{} ", queueName);
		ConsumerTemplate consumerTemplate = camelContext
				.createConsumerTemplate();

		List<String> attributes = new ArrayList<String>();
		attributes.add("imageName");

		String endpoint = "aws-sqs://"
				+ queueName
				+ "?amazonSQSClient=#amazonSqs&attributeNames=123&attributeNames=234";

		Exchange exchange = consumerTemplate.receive(endpoint);
		LOGGER.info("exchange:{} ", exchange);
		Message message = exchange.getIn();

		Map<String, Object> headers = message.getHeaders();
		for (Map.Entry<String, Object> entry : headers.entrySet()) {
			LOGGER.info("Key : " + entry.getKey() + " Value : "
					+ entry.getValue());
		}
		String body = message.getBody(String.class);
		LOGGER.info("body:{} ", body);
	}

}

package nz.co.aws.s3.image.test;

import static org.junit.Assert.*
import groovy.util.logging.Slf4j

import javax.annotation.Resource

import nz.co.aws.config.AwsConfigBean
import nz.co.aws.s3.image.ImageRequest
import nz.co.aws.s3.image.ImageScalingConfig
import nz.co.aws.s3.image.config.ApplicationContextConfig

import org.apache.camel.CamelContext
import org.apache.camel.ConsumerTemplate
import org.apache.camel.EndpointInject
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.Produce
import org.apache.camel.ProducerTemplate
import org.apache.camel.component.mock.MockEndpoint
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationContextConfig.class)
@Slf4j
class ImageRouteTest {

	static final String TEST_IMAGE = "/abc.jpg"

	@Produce
	ProducerTemplate producer
	ImageRequest imageRequest

	@EndpointInject(uri = "mock:afterRetrieve")
	MockEndpoint mockAfterRetrieve

	@EndpointInject(uri = "mock:beforeScaling")
	MockEndpoint mockBeforeScaling

	@EndpointInject(uri = "mock:beforeS3Endpoint")
	MockEndpoint mockBeforeS3Endpoint

	@Resource
	CamelContext camelContext
	@Resource
	AwsConfigBean awsConfigBean

	String testKey = "image/standard.jpg"

	@Before
	void setUp(){
		File imageFile = new File(getClass().getResource(TEST_IMAGE).file)
		imageRequest = new ImageRequest(imageFile:imageFile)
		ImageScalingConfig config = new ImageScalingConfig(name:'thumbnail',width:1217,height:1217,outputEndpoint:'file',outputPath:'outbox')
		imageRequest.scalingConfigs << config
		config = new ImageScalingConfig(name:'standard',width:1024,height:1024,outputEndpoint:'s3',outputPath:'image')
		imageRequest.scalingConfigs << config
		config = new ImageScalingConfig(name:'original',outputPath:'outbox')
		imageRequest.scalingConfigs << config
	}

	@Test
	void testPushToS3() {
		producer.requestBody('direct:ImageS3Process', imageRequest, Map.class)
		log.info "mockAfterRetrieve:{}... $mockAfterRetrieve"
		Map bodyMap = mockAfterRetrieve.getExchanges().get(0).getIn()
				.getBody(Map.class)
		assertNotNull(bodyMap)
		log.info "image meta data print start..."
		bodyMap.each{k,v->
			println "body key:{} $k, value:{} $v"
		}
		log.info "image meta data print end..."

		String imageExtension = mockAfterRetrieve.getExchanges().get(0)
				.getProperties().get('imageExtension')
		assertNotNull(imageExtension)
		log.info "imageExtension: {} $imageExtension"


		List<Exchange> exchanges = mockBeforeScaling.getExchanges()
		log.info "exchanges size: {} ${exchanges.size()}"
		exchanges.each {
			def body = it.getIn().getBody()
			println "transfrom: {} $body"
		}

		Map headers = mockBeforeS3Endpoint.getExchanges().get(0).getIn().getHeaders()
		headers.each {k,v->
			println "s3 header key:{} $k, value:{} $v"
		}
		
		Thread.sleep(2000)

		ConsumerTemplate consumer = camelContext.createConsumerTemplate()
		Exchange responseExchange = consumer.receive("aws-s3://${awsConfigBean.bucketName}?amazonS3Client=#amazonS3&maxMessagesPerPoll=1&deleteAfterRead=true&prefix=${testKey}", 3000)
		Message msg = responseExchange.getIn()
		Map respHeaders = msg.getHeaders()
		respHeaders.each {k,v->
			println "respHeaders key:{} $k, value:{} $v"
		}
		assertNotNull(msg.body)
	}
}

package nz.co.aws.s3.image.test;

import static org.junit.Assert.*
import groovy.util.logging.Slf4j
import nz.co.aws.s3.image.ImageRequest
import nz.co.aws.s3.image.ImageScalingConfig
import nz.co.aws.s3.image.config.ApplicationContextConfig

import org.apache.camel.EndpointInject
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

	@EndpointInject(uri = "mock:beforePush")
	MockEndpoint mockBeforePush

	@Before
	void setUp(){
		File imageFile = new File(getClass().getResource(TEST_IMAGE).file)
		imageRequest = new ImageRequest(imageName:'testimg',imageFile:imageFile,outputPath:'/imageName/')
		ImageScalingConfig config = new ImageScalingConfig(name:'thumbnail',width:1217,height:1217)
		imageRequest.scalingConfigs << config
		config = new ImageScalingConfig(name:'standard',width:1024,height:1024)
		imageRequest.scalingConfigs << config
		config = new ImageScalingConfig(name:'original')
		imageRequest.scalingConfigs << config
	}

	@Test
	void testPushToS3() {
		producer.requestBodyAndHeader('direct:ImageS3Process', imageRequest, 'outputEndpoint', 's3', Map.class)
		mockBeforePush.getProperties()
		
		
		
//		log.info 'response: {} $responseMap'
	}
}

package nz.co.aws.s3.camel;

import static org.junit.Assert.assertNotNull

import javax.annotation.Resource

import nz.co.aws.s3.AwsS3GeneralService
import nz.co.aws.s3.camel.config.ApplicationContextConfig

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationContextConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class AwsS3GeneralServiceCamelIntegrationTest {

	static final String TEST_DOWNLOAD_FILE_NAME = "test.jpg"
	static final String TEST_ADD_ASSET_KEY = "image/james.jpg"

	InputStream testFileStream

	@Before
	void setUp(){
		testFileStream = AwsS3GeneralServiceCamelIntegrationTest.class.getResourceAsStream("/images/james.jpg")
	}

	@Resource
	AwsS3GeneralService awsS3GeneralServiceCamelImpl

	@Test
	public void testPutFile() throws Exception {
		awsS3GeneralServiceCamelImpl.putAsset(TEST_ADD_ASSET_KEY, testFileStream,
				"image/jpeg")
	}

	@Test
	public void testRemoveFile() throws Exception {
	}
}

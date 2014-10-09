package nz.co.aws.s3;

import static org.junit.Assert.assertNotNull
import groovy.util.logging.Slf4j

import javax.annotation.Resource

import nz.co.aws.s3.config.ApplicationContextConfig

import org.apache.commons.io.FileUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.DirtiesContext.ClassMode
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationContextConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Slf4j
class AwsS3ClientIntegrationTest {
	@Resource
	AwsS3GeneralService awsS3GeneralService

	static final String TEST_DOWNLOAD_FILE_NAME = "test.jpg"
	static final String TEST_ADD_ASSET_KEY = "image/james.jpg"

	InputStream testFileStream

	@Before
	void setUp(){
		testFileStream = AwsS3ClientIntegrationTest.class.getResourceAsStream("/images/james.jpg")
	}

	@Test
	void testCrd() {
		awsS3GeneralService.putAsset(TEST_ADD_ASSET_KEY, testFileStream, "image/jpeg")
		AssetBean asset = awsS3GeneralService.getAssetByName(TEST_ADD_ASSET_KEY)
		assertNotNull(asset)
		log.info "found asset: {} $asset"

		File downloadFile = new File(com.google.common.io.Files.createTempDir(), TEST_DOWNLOAD_FILE_NAME);
		//		FileUtils.writeByteArrayToFile(downloadFile, asset.getContent())
		FileUtils.copyInputStreamToFile(asset.getContent(), downloadFile)

		log.info "localClasspathImagePath:{} ${downloadFile.getAbsolutePath()}"
		awsS3GeneralService.deleteAssert(TEST_ADD_ASSET_KEY)
	}
}

package nz.co.aws.s3.camel;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.annotation.Resource;

import nz.co.aws.AwsClientUtils;
import nz.co.aws.FileStream;
import nz.co.aws.s3.AwsS3GeneralService;
import nz.co.aws.s3.camel.config.ApplicationContextConfig;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ApplicationContextConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class AwsS3GeneralServiceCamelIntegrationTest {
	private static final String TEST_SEARCH_ASSET_KEY = "vernon/296984/original.jpg";

	private static final String TEST_DOWNLOAD_FILE_NAME = "test.jpg";

	private static final String TEST_ADD_ASSET_KEY = "vernon/400000/james.jpg";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AwsS3GeneralServiceCamelIntegrationTest.class);

	@Resource
	private AwsS3GeneralService awsS3GeneralServiceCamelImpl;

	@Test
	public void testPutFile() throws Exception {
		File uploadFile = AwsClientUtils.getFileFromClasspath("/james.jpg");
		assertNotNull(uploadFile);
		LOGGER.info("uploadFile:{} ", uploadFile.getAbsolutePath());
		InputStream asset = new ByteArrayInputStream(
				FileUtils.readFileToByteArray(uploadFile));

		awsS3GeneralServiceCamelImpl.putAsset(TEST_ADD_ASSET_KEY, asset,
				"image/jpeg");

	}

	@Test
	public void testGetFile() throws Exception {
		FileStream fileStream = awsS3GeneralServiceCamelImpl
				.getAssetByName(TEST_SEARCH_ASSET_KEY);
		assertNotNull(fileStream);
		assertNotNull(fileStream.getInputStream());
	}

	@Test
	public void testRemoveFile() throws Exception {

	}

}

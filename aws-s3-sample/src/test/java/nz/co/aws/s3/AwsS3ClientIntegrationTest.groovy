package nz.co.aws.s3;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.annotation.Resource;

import nz.co.aws.AwsClientUtils;
import nz.co.aws.FileStream;
import nz.co.aws.s3.config.ApplicationContextConfig;

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
public class AwsS3ClientIntegrationTest {

	@Resource
	private AwsS3GeneralService awsS3GeneralService;

	private static final String TEST_SEARCH_ASSET_KEY = "vernon/av/296984/original.jpg";

	private static final String TEST_DOWNLOAD_FILE_NAME = "test.jpg";

	private static final String TEST_ADD_ASSET_KEY = "image/james.jpg";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AwsS3ClientIntegrationTest.class);

	@Test
	public void testDownloadImage() throws Exception {
		FileStream fileStream = awsS3GeneralService
				.getAssetByName(TEST_SEARCH_ASSET_KEY);
		assertNotNull(fileStream);
		InputStream imageStream = fileStream.getInputStream();
		File imageFile = AwsClientUtils.writeTempFileToClasspath(
				TEST_DOWNLOAD_FILE_NAME, imageStream);
		String localClasspathImagePath = imageFile.getAbsolutePath();
		LOGGER.info("localClasspathImagePath:{} ", localClasspathImagePath);
	}

	@Test
	public void testPutFile() throws Exception {
		File uploadFile = AwsClientUtils.getFileFromClasspath("/james.jpg");
		assertNotNull(uploadFile);
		LOGGER.info("uploadFile:{} ", uploadFile.getAbsolutePath());
		InputStream asset = new ByteArrayInputStream(
				FileUtils.readFileToByteArray(uploadFile));
		awsS3GeneralService.putAsset(TEST_ADD_ASSET_KEY, asset, "image/jpeg");
	}

	@Test
	public void testDeleteFile() throws Exception {
		awsS3GeneralService.deleteAssert(TEST_ADD_ASSET_KEY);
	}
	
	@Test
	public void testDelete()throws Exception {
		
	}
}

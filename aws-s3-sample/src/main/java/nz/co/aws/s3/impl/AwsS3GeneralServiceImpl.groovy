package nz.co.aws.s3;

import static nz.co.aws.AwsClientUtils.FOLDER_SUFFIX;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import nz.co.aws.AwsClientUtils;
import nz.co.aws.FileStream;
import nz.co.aws.config.AwsConfigBean;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

@Service
public class AwsS3GeneralServiceImpl implements AwsS3GeneralService {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AwsS3GeneralServiceImpl.class);

	@Resource
	private AwsConfigBean awsConfigBean;

	@Resource
	private AmazonS3 amazonS3;

	@Override
	public void putAsset(String key, InputStream asset, String contentType) {
		LOGGER.info("putAsset start:{} ", key);
		LOGGER.info("awsConfigBean: {} ",awsConfigBean);
		if (asset != null) {
			final ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(((ByteArrayInputStream) asset).available());
			if (!StringUtils.isEmpty(contentType)) {
				meta.setContentType(contentType);
			}

			amazonS3.putObject(new PutObjectRequest(awsConfigBean
					.getBucketName(), key, asset, meta));

			try {
				asset.close();
			} catch (IOException e) {
				LOGGER.error("stream close failed.", e);
			}
		}

		LOGGER.info("putAsset end ");
	}

	@Override
	public List<String> getAssetList(String prefix) {
		LOGGER.debug("getAssetList start:{} ", prefix);
		final List<String> result = new ArrayList<>();
		final ObjectListing objList = amazonS3.listObjects(
				awsConfigBean.getBucketName(),
				AwsClientUtils.formatPath(prefix));
		if (objList != null) {
			for (final S3ObjectSummary summary : objList.getObjectSummaries()) {
				// ignore folders
				if (!summary.getKey().endsWith(FOLDER_SUFFIX)) {
					result.add(summary.getKey().substring(prefix.length()));
				}
			}
		}
		LOGGER.debug("getAssetList end:{}", result.size());
		return result;
	}

	@Override
	public FileStream getAssetByName(String name) throws Exception {
		LOGGER.debug("getAssetByName start:{}", name);
		S3Object obj = null;

		obj = amazonS3.getObject(new GetObjectRequest(awsConfigBean
				.getBucketName(), name));

		final FileStream result = FileStream.getBuilder(obj.getObjectContent(),
				obj.getObjectMetadata().getContentLength()).build();

		LOGGER.debug("getAssetByName end");
		return result;
	}

	@Override
	public void deleteAssert(String key) throws Exception {
		LOGGER.debug("deleteAssert start:{}", key);
		amazonS3.deleteObject(awsConfigBean.getBucketName(), key);
		LOGGER.debug("deleteAssert end");
	}

}

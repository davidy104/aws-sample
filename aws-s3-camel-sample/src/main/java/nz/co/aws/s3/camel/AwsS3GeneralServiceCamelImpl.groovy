package nz.co.aws.s3.camel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import nz.co.aws.FileStream;
import nz.co.aws.config.AwsConfigBean;
import nz.co.aws.s3.AwsS3GeneralService;

import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service(value = "awsS3GeneralServiceCamelImpl")
public class AwsS3GeneralServiceCamelImpl implements AwsS3GeneralService {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AwsS3GeneralServiceCamelImpl.class);

	@Resource
	private CamelContext camelContext;

	@Resource
	private AwsConfigBean awsConfigBean;

	@Produce
	private ProducerTemplate producerTemplate;

	@Override
	public void putAsset(String key, InputStream asset, String contentType) {
		LOGGER.info("putAsset start:{} ", key);

		String endpointUri = "aws-s3://" + awsConfigBean.getBucketName()
				+ "?amazonS3Client=#amazonS3";

		if (asset != null) {
			Map<String, Object> headers = new HashMap<String, Object>();
			headers.put("CamelAwsS3Key", key);
			headers.put("CamelAwsS3ContentLength",
					((ByteArrayInputStream) asset).available());
			if (!StringUtils.isEmpty(contentType)) {
				headers.put("CamelAwsS3ContentType", contentType);
			}

			producerTemplate.requestBodyAndHeaders(endpointUri, asset, headers);

			try {
				asset.close();
			} catch (IOException e) {
				LOGGER.error("stream close failed.", e);
			}
		}
		LOGGER.info("putAsset end");
	}

	@Override
	public List<String> getAssetList(String prefix) {

		return null;
	}

	@Override
	public FileStream getAssetByName(String key) throws Exception {
		LOGGER.info("getAssetByName start:{} ", key);
		FileStream result = null;
		ConsumerTemplate template = camelContext.createConsumerTemplate();

		Exchange exchange = template
				.receive(
						"aws-s3://"
								+ awsConfigBean.getBucketName()
								+ "?amazonS3Client=#amazonS3&maxMessagesPerPoll=1&prefix="
								+ key + "", 5000L);

		if (null != exchange) {
			Message message = exchange.getIn();
			// String awsS3Key = (String) message.getHeader("CamelAwsS3Key");
			Long awsS3ContentLength = (Long) message
					.getHeader("CamelAwsS3ContentLength");
			InputStream fileStream = message.getBody(InputStream.class);

			result = FileStream.getBuilder(fileStream, awsS3ContentLength)
					.build();

			Map<String, Object> headers = message.getHeaders();
			for (Map.Entry<String, Object> entry : headers.entrySet()) {
				LOGGER.info("Header : " + entry.getKey() + " - Value : "
						+ entry.getValue());
			}
		}
		LOGGER.info("getAssetByName end ");
		return result;
	}

	@Override
	public void deleteAssert(String key) throws Exception {
		LOGGER.info("deleteAssert start:{} ", key);

		LOGGER.info("deleteAssert end ");
	}

}

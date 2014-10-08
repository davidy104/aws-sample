package nz.co.aws.s3.camel;

import groovy.util.logging.Slf4j

import javax.annotation.Resource

import nz.co.aws.config.AwsConfigBean
import nz.co.aws.s3.AssetBean
import nz.co.aws.s3.AwsS3GeneralService

import org.apache.camel.CamelContext
import org.apache.camel.ConsumerTemplate
import org.apache.camel.Exchange
import org.apache.camel.Message
import org.apache.camel.Produce
import org.apache.camel.ProducerTemplate
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service(value = "awsS3GeneralServiceCamelImpl")
@Slf4j
class AwsS3GeneralServiceCamelImpl implements AwsS3GeneralService {

	@Resource
	CamelContext camelContext

	@Resource
	AwsConfigBean awsConfigBean

	@Produce
	ProducerTemplate producerTemplate

	@Override
	void putAsset(final String key, final InputStream asset,final String contentType) {
		log.info "putAsset start:{} $key"
		String endpointUri = "aws-s3://${awsConfigBean.bucketName}?amazonS3Client=#amazonS3"
		if (asset) {
			try {
				Map headers = [:]
				headers.put("CamelAwsS3Key", key)
				headers.put("CamelAwsS3ContentLength",asset.available())
				if (contentType) {
					headers.put("CamelAwsS3ContentType", contentType)
				}
				producerTemplate.requestBodyAndHeaders(endpointUri, asset, headers)
			} catch (e) {
				throw new RuntimeException(e)
			}finally{
				asset.close()
			}
		}
		log.debug "putAsset end"
	}

	@Override
	List<String> getAssetList(final String prefix) {

		return null
	}

	@Override
	public AssetBean getAssetByName(final String key) {
		log.info "getAssetByName start:{} $key "
		AssetBean result
		ConsumerTemplate template = camelContext.createConsumerTemplate()
		InputStream fileStream
		String uri = "aws-s3://${awsConfigBean.getBucketName()}?amazonS3Client=#amazonS3&maxMessagesPerPoll=1&prefix=${key}"

		Exchange exchange = template.receive(uri, 5000L);

		if (exchange) {
			Message message = exchange.getIn()
			String awsS3Key = (String) message.getHeader("CamelAwsS3Key")
			Long awsS3ContentLength = (Long) message
					.getHeader("CamelAwsS3ContentLength");
			fileStream = message.getBody(InputStream.class);
			byte[] cotentBytes = IOUtils.toByteArray(fileStream)
			result = new AssetBean(key:awsS3Key,content:cotentBytes,size:awsS3ContentLength)
			//just print for testing
			message.getHeaders().each{k,v->
				log.info("Header : $k - Value : $v")
			}
		}
		log.info "getAssetByName end "
		return result
	}

	@Override
	void deleteAssert(String key) {
	}
}

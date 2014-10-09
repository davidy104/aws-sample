package nz.co.aws.s3.impl;

import static nz.co.aws.AwsClientUtils.FOLDER_SUFFIX
import groovy.util.logging.Slf4j

import javax.annotation.Resource

import nz.co.aws.AwsClientUtils
import nz.co.aws.config.AwsConfigBean
import nz.co.aws.s3.AssetBean
import nz.co.aws.s3.AwsS3GeneralService

import org.apache.commons.io.input.ProxyInputStream
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.model.ObjectListing
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.model.PutObjectRequest
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.model.S3ObjectSummary

@Service
@Slf4j
class AwsS3GeneralServiceImpl implements AwsS3GeneralService {

	@Resource
	AwsConfigBean awsConfigBean

	@Resource
	AmazonS3 amazonS3

	@Override
	void putAsset(final String key,final InputStream asset,final String contentType) {
		log.debug "putAsset start:{} $key"
		log.debug "awsConfigBean: {} $awsConfigBean"
		if (asset) {
			try {
				ObjectMetadata meta = new ObjectMetadata()
				meta.setContentLength(asset.available())
				if (contentType) {
					meta.setContentType(contentType)
				}
				amazonS3.putObject(new PutObjectRequest(awsConfigBean
						.getBucketName(), key, asset, meta))
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
		log.debug "getAssetList start:{} $prefix"
		List<String> result = []
		ObjectListing objList = amazonS3.listObjects(
				awsConfigBean.getBucketName(),
				AwsClientUtils.formatPath(prefix))
		if (objList) {
			for (S3ObjectSummary summary : objList.getObjectSummaries()) {
				// ignore folders
				if (!summary.getKey().endsWith(FOLDER_SUFFIX)) {
					result << summary.getKey().substring(prefix.length())
				}
			}
		}
		log.debug "getAssetList end:{} ${result.size()}"
		return result
	}

	@Override
	AssetBean getAssetByName(final String name) {
		log.debug "getAssetByName start:{} $name"
		S3Object obj
		def result
		obj  = amazonS3.getObject(new GetObjectRequest(awsConfigBean
				.getBucketName(), name))
		if(obj){
			result = new AssetBean(bucketName:obj.bucketName,key:obj.key,size:obj.getObjectMetadata().getContentLength())
			ProxyInputStream contentIs = new ProxyInputStream(obj.getObjectContent()) {
						@Override
						void close() {
							super.close()
							obj.close()
						}
					}
			result.content = contentIs
		}
		return result
	}

	@Override
	void deleteAssert(final String key) {
		amazonS3.deleteObject(awsConfigBean.getBucketName(), key)
	}

}

package nz.co.aws.s3.image.route

import javax.annotation.Resource

import nz.co.aws.config.AwsConfigBean
import nz.co.aws.s3.image.processor.ImageMetadataRetrievingProcessor
import nz.co.aws.s3.image.processor.ImageScalingProcessor

import org.apache.camel.ExchangePattern
import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class ImageS3ProcessRoute extends RouteBuilder {

	@Resource
	AwsConfigBean awsConfigBean
	@Resource
	ImageMetadataRetrievingProcessor imageMetadataRetrievingProcessor
	@Resource
	ImageScalingProcessor imageScalingProcessor

	@Override
	public void configure() throws Exception {
		from("direct:ImageS3Process")
				.routeId('direct:ImageS3Process')
				.setExchangePattern(ExchangePattern.InOut)
				.setProperty('imageTransforms', '${body.scalingConfigs}')
				.setProperty('outputPath', '${body.outputPath}')
				.process(imageMetadataRetrievingProcessor)
				.split(simple('${property.imageTransforms}'))
				.process(imageScalingProcessor)
				.to("mock:beforePush")
//				.to('seda:pushToEndpoint')
				.end()

		from("seda:pushToEndpoint")
				.choice()
				.when()
				.simple('${property.outputEndpoint} == "s3"')
				.setHeader('CamelAwsS3Key', '${property.outputPath}+${property.imageName}')
				.setHeader('CamelAwsS3ContentLength', '${property.contentLength}')
				.setHeader('CamelAwsS3ContentType', 'image/jpeg')
				.to('aws-s3://' + awsConfigBean.bucketName+ '?amazonS3Client=#amazonS3')
				.otherwise()
				.setHeader('CamelFileName', '${property.outputPath}+${property.imageName}')
				.to('file://outbox?fileName=${property.imageName}')
				.end()
	}
}

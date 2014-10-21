package nz.co.aws.s3.image.route

import javax.annotation.Resource

import nz.co.aws.config.AwsConfigBean
import nz.co.aws.s3.image.processor.ImageMetadataRetrievingProcessor
import nz.co.aws.s3.image.processor.ImageScalingProcessor

import org.apache.camel.ExchangePattern
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.builder.ThreadPoolProfileBuilder
import org.apache.camel.model.ModelCamelContext
import org.apache.camel.spi.ThreadPoolProfile
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
		ThreadPoolProfile customThreadPoolProfile = new ThreadPoolProfileBuilder(
				"customThreadPoolProfile").poolSize(5).maxQueueSize(100)
				.build()
		getContext().getExecutorServiceManager().registerThreadPoolProfile(
				customThreadPoolProfile)

		from("direct:ImageS3Process")
				.routeId('direct:ImageS3Process')
				.setExchangePattern(ExchangePattern.InOut)
				.setProperty('imageTransforms', simple('${body.scalingConfigs}'))
				.process(imageMetadataRetrievingProcessor)
				.to("mock:afterRetrieve")
				.split(simple('${property.imageTransforms}')).executorServiceRef("customThreadPoolProfile")
				.to("mock:beforeScaling")
				.process(imageScalingProcessor)
				.to('direct:pushToEndpoint')
				.end()

		from("direct:pushToEndpoint")
				.choice()
				.when()
				.simple('${property.outputEndpoint} == "s3"')
				.to('direct:s3')
				.otherwise()
				.to('direct:file')
				.end()

		from("direct:s3")
				.setHeader('CamelAwsS3Key', simple('${property.outputPath}/${property.imageName}'))
				.setHeader('CamelAwsS3ContentLength', simple('${property.contentLength}'))
				.setHeader('CamelAwsS3ContentType', constant('image/jpeg'))
				.to("mock:beforeS3Endpoint")
				.to('aws-s3://' + awsConfigBean.bucketName+ '?amazonS3Client=#amazonS3')

		from("direct:file")
				.setHeader('CamelFileName', simple('${property.imageName}'))
				.to('file://outbox?fileName=${property.imageName}')
	}
}

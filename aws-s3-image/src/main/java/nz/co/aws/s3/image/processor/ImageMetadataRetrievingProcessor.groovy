package nz.co.aws.s3.image.processor

import groovy.util.logging.Slf4j
import nz.co.aws.s3.image.ImageRequest

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.input.ProxyInputStream
import org.apache.sanselan.Sanselan
import org.apache.sanselan.common.IImageMetadata
import org.apache.sanselan.common.ImageMetadata.Item
import org.springframework.stereotype.Component

@Component
@Slf4j
class ImageMetadataRetrievingProcessor implements Processor {

	@Override
	void process(Exchange exchange) {
		log.info 'ImageMetadataRetrievingProcessor start...'
		ImageRequest imageRequest = exchange.in.getBody(ImageRequest.class)
		def metadataMap = [:]
		File image = imageRequest.imageFile
		exchange.setProperty("imageBytes", FileUtils.readFileToByteArray(image))
		exchange.setProperty("imageExtension", FilenameUtils.getExtension(image.getAbsolutePath()))
		
		String fileName = FilenameUtils.getName(image.getAbsolutePath())
		log.info "fileName: {} $fileName" 

		InputStream imageStream = FileUtils.openInputStream(image)
		final IImageMetadata metadata = Sanselan.getMetadata(new ProxyInputStream(imageStream) {
					@Override
					public void close() throws IOException {
						super.close()
					}
				},fileName)
		if(metadata) {
			metadata.getItems().each{
				Item item = (Item)it
				metadataMap.put(item.keyword, item.text)
			}
		}
		exchange.in.setBody(metadataMap, Map.class)
		log.info 'ImageMetadataRetrievingProcessor end...'
	}
}

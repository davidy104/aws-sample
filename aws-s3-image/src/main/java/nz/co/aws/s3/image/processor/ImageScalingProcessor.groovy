package nz.co.aws.s3.image.processor;


import groovy.util.logging.Slf4j

import java.awt.image.BufferedImage

import javax.imageio.ImageIO

import nz.co.aws.s3.image.ImageScalingConfig

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.commons.io.input.ProxyInputStream
import org.imgscalr.Scalr
import org.imgscalr.Scalr.Mode
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@Slf4j
class ImageScalingProcessor implements Processor {
	@Override
	void process(Exchange exchange)  {
		log.info "ImageScalingProcessor start..."
		byte[] imageBytes = exchange.getProperty("imageBytes")
		String imageExtension = exchange.getProperty('imageExtension')
		ImageScalingConfig imageScalingConfig = exchange.in.getBody()
		String fileName = imageScalingConfig.name + "."+imageExtension
		log.info "fileName:{} $fileName"
		exchange.setProperty('imageName', fileName)
		exchange.setProperty('outputEndpoint', imageScalingConfig.outputEndpoint)
		exchange.setProperty('outputPath', imageScalingConfig.outputPath)

		InputStream imageInputStream= new ByteArrayInputStream(imageBytes)

		if(imageScalingConfig.name != 'original'){
			BufferedImage img = ImageIO.read(imageInputStream)
			final BufferedImage bufferedImage = Scalr.resize(img,
					Mode.AUTOMATIC,imageScalingConfig.width, imageScalingConfig.height)
			final ByteArrayOutputStream output = new ByteArrayOutputStream() {
						@Override
						public synchronized byte[] toByteArray() {
							return this.buf
						}
					}
			ImageIO.write(bufferedImage, imageExtension, output)
			imageInputStream = new ByteArrayInputStream(
					output.toByteArray(), 0, output.size())
			exchange.setProperty('contentLength', imageInputStream.available())
		}

		exchange.in.setBody(new ProxyInputStream(imageInputStream) {
					@Override
					public void close() throws IOException {
						super.close()
					}
				}, InputStream.class)

		log.info "ImageScalingProcessor end..."
	}
}

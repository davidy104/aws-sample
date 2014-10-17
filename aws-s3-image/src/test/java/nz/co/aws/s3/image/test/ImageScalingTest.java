package nz.co.aws.s3.image.test;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Mode;
import org.junit.Test;

//@Ignore("not run all the time")
public class ImageScalingTest {
	private static final String TIFF_IMAGE = "/AMNZ83867-a.tif";
	private static final String JPG_IMAGE = "/abc.jpg";

	@Test
	public void testTiff() throws Exception {
		int width = 4000;
		int height = 3600;
		ImageIO.setUseCache(false);
		URL url = ImageScalingTest.class.getResource(TIFF_IMAGE);
		BufferedImage image = ImageIO.read(url);

		// int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image
		// .getType();
		// BufferedImage resizedImg = resizeImageWithHint(image, type, width,
		// hegith);
		final BufferedImage bufferedImage = Scalr.resize(image, Mode.AUTOMATIC,
				width, height);
		final File outputFile = new File("/tmp/output.tif");
		try (final OutputStream imageStream = new FileOutputStream(outputFile)) {
			ImageIO.write(bufferedImage, "tif", imageStream);
			imageStream.flush();
		}
	}

	@Test
	public void testJpg() throws Exception {
		int width = 4000;
		int hegith = 3600;
		ImageIO.setUseCache(false);
		URL url = ImageScalingTest.class.getResource(JPG_IMAGE);
		System.out.println(url.getFile());
		System.out.println(url.getPath());
		BufferedImage image = ImageIO.read(url);

		int type = image.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : image
				.getType();
		BufferedImage resizedImg = resizeImageWithHint(image, type, width,
				hegith);

		final File outputFile = new File("/tmp/output.jpg");
		try (final OutputStream imageStream = new FileOutputStream(outputFile)) {
			ImageIO.write(resizedImg, "jpg", imageStream);
			imageStream.flush();
		}
	}

	public static BufferedImage resizeImage(BufferedImage originalImage,
			int type, int wight, int height) {
		BufferedImage resizedImage = new BufferedImage(wight, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, wight, height, null);
		g.dispose();
		return resizedImage;
	}

	public static BufferedImage resizeImageWithHint(
			BufferedImage originalImage, int type, int wight, int height) {

		BufferedImage resizedImage = new BufferedImage(wight, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, wight, height, null);
		g.dispose();
		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		return resizedImage;
	}

}

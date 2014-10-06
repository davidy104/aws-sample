package nz.co.aws;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AwsClientUtils {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(AwsClientUtils.class);

	public static final String FOLDER_SUFFIX = "/";

	public static File writeTempFileToClasspath(String fileName,
			InputStream inputStream) {
		LOGGER.info("writeTempFileToClasspath start:{}", fileName);
		String classpath = AwsClientUtils.class.getProtectionDomain()
				.getCodeSource().getLocation().getPath();
		LOGGER.info("classpath:{}", classpath);
		File tempFile = null;
		tempFile = new File(classpath + fileName);
		if (!tempFile.exists()) {
			try {
				tempFile.createNewFile();
			} catch (IOException e) {
				LOGGER.debug("create file error", e);
				new RuntimeException("create file error", e);
			}
		}

		if (tempFile != null) {
			LOGGER.info("write content to tempFile start");
			try {
				byte[] content = IOUtils.toByteArray(inputStream);
				FileUtils.writeByteArrayToFile(tempFile, content);
			} catch (IOException e) {
				LOGGER.error("write file error", e);
			}
		}
		return tempFile;
	}

	public static File getFileFromClasspath(String filenameOnClasspath) {
		LOGGER.info("getFileFromClasspath start:{}", filenameOnClasspath);
		URL url = AwsClientUtils.class.getResource(filenameOnClasspath);
		if (url == null) {
			return null;
		} else {
			String fullyQualifiedFilename = url.getFile();
			return new File(fullyQualifiedFilename);
		}
	}

	public static String formatPath(final String path) {
		// remove root path: /
		String formattedPath = null;
		if (path.startsWith(FOLDER_SUFFIX)) {
			formattedPath = path.substring(1);
		} else {
			formattedPath = path + FOLDER_SUFFIX;
		}
		return formattedPath;
	}
}

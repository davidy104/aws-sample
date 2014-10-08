package nz.co.aws.s3;

import java.io.InputStream;
import java.util.List;

import nz.co.aws.FileStream;

public interface AwsS3GeneralService {

	void putAsset(String key, InputStream asset, String contentType);

	List<String> getAssetList(String prefix);

	FileStream getAssetByName(String key) throws Exception;

	void deleteAssert(String key) throws Exception;
}

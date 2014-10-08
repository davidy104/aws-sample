package nz.co.aws.s3;


 interface AwsS3GeneralService {

	void putAsset(String key, InputStream asset, String contentType)

	List<String> getAssetList(String prefix)

	AssetBean getAssetByName(String key)

	void deleteAssert(String key)
}

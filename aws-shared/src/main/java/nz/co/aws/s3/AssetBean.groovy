package nz.co.aws.s3;

import groovy.transform.ToString

@ToString(includeNames = true, includeFields=true)
class AssetBean {
	String bucketName
	String key
	byte[] content
	long size
}

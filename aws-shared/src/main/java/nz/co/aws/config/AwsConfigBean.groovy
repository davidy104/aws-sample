package nz.co.aws.config;

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, includeFields=true)
@EqualsAndHashCode(includes=["secretKey","accessKey"])
class AwsConfigBean {
	String accessKey
	String secretKey
	String bucketName
}

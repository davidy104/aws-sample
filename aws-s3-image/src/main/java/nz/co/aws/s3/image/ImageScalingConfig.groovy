package nz.co.aws.s3.image

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@ToString(includeNames = true, includeFields=true)
@EqualsAndHashCode(includes=["name"])
class ImageScalingConfig implements Serializable{
	String name
	Integer width
	Integer height
	String outputPath
	String outputEndpoint='file'
}

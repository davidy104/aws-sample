package nz.co.aws.s3.image

import groovy.transform.ToString
@ToString(includeNames = true, includeFields=true)
class ImageRequest implements Serializable{
	String imageName
	File imageFile
	String outputPath
	Set<ImageScalingConfig> scalingConfigs = []
}

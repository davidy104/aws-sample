package nz.co.aws.s3.image

import groovy.transform.ToString
@ToString(includeNames = true, includeFields=true)
class ImageRequest implements Serializable{
	File imageFile
	Set<ImageScalingConfig> scalingConfigs = []
}

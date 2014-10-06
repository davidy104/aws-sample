package nz.co.aws.config;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AwsConfigBean {

	private String accessKey;
	private String secretKey;
	private String bucketName;

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public static Builder getBuilder(String accessKey, String secretKey,
			String bucketName) {
		return new Builder(accessKey, secretKey, bucketName);
	}

	public static class Builder {

		private AwsConfigBean built;

		public Builder(String accessKey, String secretKey, String bucketName) {
			built = new AwsConfigBean();
			built.accessKey = accessKey;
			built.secretKey = secretKey;
			built.bucketName = bucketName;
		}

		public AwsConfigBean build() {
			return built;
		}
	}

	@Override
	public boolean equals(Object obj) {
		EqualsBuilder builder = new EqualsBuilder();
		return builder.append(this.secretKey, ((AwsConfigBean) obj).secretKey)
				.append(this.accessKey, ((AwsConfigBean) obj).accessKey)
				.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		return builder.append(this.accessKey).append(this.secretKey)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
				.append("accessKey", accessKey).append("secretKey", secretKey)
				.append("bucketName", bucketName).toString();

	}
}

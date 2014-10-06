package nz.co.aws;

import java.io.InputStream;

public class FileStream {
	private InputStream inputStream;
	private long size;

	public InputStream getInputStream() {
		return this.inputStream;
	}

	public void setInputStream(final InputStream input) {
		this.inputStream = input;
	}

	public long getSize() {
		return this.size;
	}

	public void setSize(final long size) {
		this.size = size;
	}

	public static Builder getBuilder(final InputStream input, final long size) {
		return new Builder(input, size);
	}

	public static class Builder {

		private final FileStream built;

		@SuppressWarnings("synthetic-access")
		public Builder(final InputStream input, final long size) {
			this.built = new FileStream();
			this.built.inputStream = input;
			this.built.size = size;
		}

		public FileStream build() {
			return this.built;
		}
	}
}

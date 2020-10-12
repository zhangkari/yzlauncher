package rmkj.lib.rzp.io;

import java.io.IOException;
import java.io.InputStream;

import rmkj.lib.rzp.unrzp.UnRZPEngine;

public abstract class BaseInputStream extends InputStream {

	public int read() throws IOException {
		return 0;
	}
	
	public void seek(long pos) throws IOException {
	}
	
	public int available() throws IOException {
		return 0;
	}
	
	public UnRZPEngine getUnzipEngine() {
		return null;
	}

}

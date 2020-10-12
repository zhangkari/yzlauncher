/*
* Copyright 2010 Srikanth Reddy Lingala  
* 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License. 
* You may obtain a copy of the License at 
* 
* http://www.apache.org/licenses/LICENSE-2.0 
* 
* Unless required by applicable law or agreed to in writing, 
* software distributed under the License is distributed on an "AS IS" BASIS, 
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
* See the License for the specific language governing permissions and 
* limitations under the License. 
*/

package rmkj.lib.rzp.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import rmkj.lib.rzp.exception.RZPException;
import rmkj.lib.rzp.util.InternalRZPConstants;
import rmkj.lib.rzp.util.RZPUtil;
import rmkj.lib.rzp.util.Raw;

public class SplitOutputStream extends OutputStream {
	
	private RandomAccessFile raf;
	private long splitLength;
	private File zipFile;
	private File outFile;
	private int currSplitFileCounter;
	private long bytesWrittenForThisPart;
	
	public SplitOutputStream(String name) throws FileNotFoundException, RZPException {
		this(RZPUtil.isStringNotNullAndNotEmpty(name) ?
				new File(name) : null);
	}
	
	public SplitOutputStream(File file) throws FileNotFoundException, RZPException {
		this(file, -1);
	}
	
	public SplitOutputStream(String name, long splitLength) throws FileNotFoundException, RZPException {
		this(!RZPUtil.isStringNotNullAndNotEmpty(name) ?
				new File(name) : null, splitLength);
	}
	
	public SplitOutputStream(File file, long splitLength) throws FileNotFoundException, RZPException {
		if (splitLength >= 0 && splitLength < InternalRZPConstants.MIN_SPLIT_LENGTH) {
			throw new RZPException("split length less than minimum allowed split length of " + InternalRZPConstants.MIN_SPLIT_LENGTH +" Bytes");
		}
		this.raf = new RandomAccessFile(file, InternalRZPConstants.WRITE_MODE);
		this.splitLength = splitLength;
		this.outFile = file;
		this.zipFile = file;
		this.currSplitFileCounter = 0;
		this.bytesWrittenForThisPart = 0;
	}
	
	public void write(int b) throws IOException {
		byte[] buff = new byte[1];
		buff[0] = (byte) b;
	    write(buff, 0, 1);
	}
	
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		if (len <= 0) return;
		
		if (splitLength != -1) {

			if (splitLength < InternalRZPConstants.MIN_SPLIT_LENGTH) {
				throw new IOException("split length less than minimum allowed split length of " + InternalRZPConstants.MIN_SPLIT_LENGTH +" Bytes");
			}
			
			if (bytesWrittenForThisPart >= splitLength) {
				startNextSplitFile();
				raf.write(b, off, len);
				bytesWrittenForThisPart = len;
			} else if (bytesWrittenForThisPart + len > splitLength) {
				if (isHeaderData(b)) {
					startNextSplitFile();
					raf.write(b, off, len);
					bytesWrittenForThisPart = len;
				} else {
					raf.write(b, off, (int)(splitLength - bytesWrittenForThisPart));
					startNextSplitFile();
					raf.write(b, off + (int)(splitLength - bytesWrittenForThisPart), (int)(len - (splitLength - bytesWrittenForThisPart)));
					bytesWrittenForThisPart = len - (splitLength - bytesWrittenForThisPart);
				}
			} else {
				raf.write(b, off, len);
				bytesWrittenForThisPart += len;
			}
		
		} else {
			raf.write(b, off, len);
			bytesWrittenForThisPart += len;
		}
		
	}
	
	private void startNextSplitFile() throws IOException {
		try {
			String zipFileWithoutExt = RZPUtil.getZipFileNameWithoutExt(outFile.getName());
			File currSplitFile = null;
			String zipFileName = zipFile.getAbsolutePath();
			
			if (currSplitFileCounter < 9) {
				currSplitFile = new File(outFile.getParent() + System.getProperty("file.separator") + 
						zipFileWithoutExt + ".z0" + (currSplitFileCounter + 1));
			} else {
				currSplitFile = new File(outFile.getParent() + System.getProperty("file.separator") + 
						zipFileWithoutExt + ".z" + (currSplitFileCounter + 1));
			}
			
			raf.close();
			
			if (currSplitFile.exists()) {
				throw new IOException("split file: " + currSplitFile.getName() + " already exists in the current directory, cannot rename this file");
			}
			
			if (!zipFile.renameTo(currSplitFile)) {
				throw new IOException("cannot rename newly created split file");
			}
			
			zipFile = new File(zipFileName);
			raf = new RandomAccessFile(zipFile, InternalRZPConstants.WRITE_MODE);
			currSplitFileCounter++;
		} catch (RZPException e) {
			throw new IOException(e.getMessage());
		}
	}
	
	private boolean isHeaderData(byte[] buff) {
		if (buff == null || buff.length < 4) {
			return false;
		}
		
		int signature = Raw.readIntLittleEndian(buff, 0);
		long[] allHeaderSignatures = RZPUtil.getAllHeaderSignatures();
		if (allHeaderSignatures != null && allHeaderSignatures.length > 0) {
			for (int i = 0; i < allHeaderSignatures.length; i++) {
				//Ignore split signature
				if (allHeaderSignatures[i] != InternalRZPConstants.SPLITSIG() && 
						allHeaderSignatures[i] == signature) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the buffer size is sufficient for the current split file. If not
	 * a new split file will be started.
	 * @param bufferSize
	 * @return true if a new split file was started else false
	 * @throws RZPException
	 */
	public boolean checkBuffSizeAndStartNextSplitFile(int bufferSize) throws RZPException {
		if (bufferSize < 0) {
			throw new RZPException("negative buffersize for checkBuffSizeAndStartNextSplitFile");
		}
		
		if (!isBuffSizeFitForCurrSplitFile(bufferSize)) {
			try {
				startNextSplitFile();
				bytesWrittenForThisPart = 0;
				return true;
			} catch (IOException e) {
				throw new RZPException(e);
			}
		}
		
		return false;
	}
	
	/**
	 * Checks if the given buffer size will be fit in the current split file.
	 * If this output stream is a non-split file, then this method always returns true
	 * @param bufferSize
	 * @return true if the buffer size is fit in the current split file or else false.
	 * @throws RZPException
	 */
	public boolean isBuffSizeFitForCurrSplitFile(int bufferSize) throws RZPException {
		if (bufferSize < 0) {
			throw new RZPException("negative buffersize for isBuffSizeFitForCurrSplitFile");
		}
		
		if (splitLength >= InternalRZPConstants.MIN_SPLIT_LENGTH) {
			return (bytesWrittenForThisPart + bufferSize <= splitLength);
		} else {
			//Non split zip -- return true
			return true;
		}
	}
	
	public void seek(long pos) throws IOException {
		raf.seek(pos);
	}
	
	public void close() throws IOException {
		if (raf != null)
			raf.close();
	}
	
	public void flush() throws IOException {
	}
	
	public long getFilePointer() throws IOException {
		return raf.getFilePointer();
	}
	
	public boolean isSplitZipFile() {
		return splitLength!=-1;
	}

	public long getSplitLength() {
		return splitLength;
	}

	public int getCurrSplitFileCounter() {
		return currSplitFileCounter;
	}
}

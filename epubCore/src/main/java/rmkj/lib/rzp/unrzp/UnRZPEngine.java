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

package rmkj.lib.rzp.unrzp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.zip.CRC32;

import rmkj.lib.rzp.core.HeaderReader;
import rmkj.lib.rzp.crypto.AESDecrypter;
import rmkj.lib.rzp.crypto.IDecrypter;
import rmkj.lib.rzp.crypto.StandardDecrypter;
import rmkj.lib.rzp.exception.RZPException;
import rmkj.lib.rzp.io.InflaterInputStream;
import rmkj.lib.rzp.io.PartInputStream;
import rmkj.lib.rzp.io.RZPInputStream;
import rmkj.lib.rzp.model.AESExtraDataRecord;
import rmkj.lib.rzp.model.FileHeader;
import rmkj.lib.rzp.model.LocalFileHeader;
import rmkj.lib.rzp.model.RZPModel;
import rmkj.lib.rzp.model.UnRZPParameters;
import rmkj.lib.rzp.progress.ProgressMonitor;
import rmkj.lib.rzp.util.InternalRZPConstants;
import rmkj.lib.rzp.util.RZPConstants;
import rmkj.lib.rzp.util.RZPUtil;
import rmkj.lib.rzp.util.Raw;

public class UnRZPEngine {
	
	private RZPModel zipModel;
	private FileHeader fileHeader;
	private int currSplitFileCounter = 0;
	private LocalFileHeader localFileHeader;
	private IDecrypter decrypter;
	private CRC32 crc;
	
	public UnRZPEngine(RZPModel zipModel, FileHeader fileHeader) throws RZPException {
		if (zipModel == null || fileHeader == null) {
			throw new RZPException("Invalid parameters passed to StoreUnzip. One or more of the parameters were null");
		}
		
		this.zipModel = zipModel;
		this.fileHeader = fileHeader;
		this.crc = new CRC32();
	}
	
	public void unzipFile(ProgressMonitor progressMonitor, 
			String outPath, String newFileName, UnRZPParameters unzipParameters) throws RZPException {
		if (zipModel == null || fileHeader == null || !RZPUtil.isStringNotNullAndNotEmpty(outPath)) {
			throw new RZPException("Invalid parameters passed during unzipping file. One or more of the parameters were null");
		}
		InputStream is = null;
		OutputStream os = null;
		try {
			byte[] buff = new byte[InternalRZPConstants.BUFF_SIZE];
			int readLength = -1;
			
			is = getInputStream();
			os = getOutputStream(outPath, newFileName);
			
			while ((readLength = is.read(buff)) != -1) {
				os.write(buff, 0, readLength);
				progressMonitor.updateWorkCompleted(readLength);
				if (progressMonitor.isCancelAllTasks()) {
					progressMonitor.setResult(ProgressMonitor.RESULT_CANCELLED);
					progressMonitor.setState(ProgressMonitor.STATE_READY);
					return;
				}
			}
			
			closeStreams(is, os);
			
			UnRZPUtil.applyFileAttributes(fileHeader, new File(getOutputFileNameWithPath(outPath, newFileName)), unzipParameters);
			
		} catch (IOException e) {
			throw new RZPException(e);
		} catch (Exception e) {
			throw new RZPException(e);
		} finally {
			closeStreams(is, os);
		}
	}
	
	public RZPInputStream getInputStream() throws RZPException {
		if (fileHeader == null) {
			throw new RZPException("file header is null, cannot get inputstream");
		}
		
		RandomAccessFile raf = null;
		try {
			raf = createFileHandler(InternalRZPConstants.READ_MODE);
			String errMsg = "local header and file header do not match";
			//checkSplitFile();
			
			if (!checkLocalHeader())
				throw new RZPException(errMsg);
			
			init(raf);
			
			long comprSize = localFileHeader.getCompressedSize();
			long offsetStartOfData = localFileHeader.getOffsetStartOfData();
			
			if (localFileHeader.isEncrypted()) {
				if (localFileHeader.getEncryptionMethod() == RZPConstants.ENC_METHOD_AES) {
					if (decrypter instanceof AESDecrypter) {
						comprSize -= (((AESDecrypter)decrypter).getSaltLength() + 
								((AESDecrypter)decrypter).getPasswordVerifierLength() + 10);
						offsetStartOfData += (((AESDecrypter)decrypter).getSaltLength() + 
								((AESDecrypter)decrypter).getPasswordVerifierLength());
					} else {
						throw new RZPException("invalid decryptor when trying to calculate " +
								"compressed size for AES encrypted file: " + fileHeader.getFileName());
					}
				} else if (localFileHeader.getEncryptionMethod() == RZPConstants.ENC_METHOD_STANDARD) {
					comprSize -= InternalRZPConstants.STD_DEC_HDR_SIZE;
					offsetStartOfData += InternalRZPConstants.STD_DEC_HDR_SIZE;
				}
			}
			
			int compressionMethod = fileHeader.getCompressionMethod();
			if (fileHeader.getEncryptionMethod() == RZPConstants.ENC_METHOD_AES) {
				if (fileHeader.getAesExtraDataRecord() != null) {
					compressionMethod = fileHeader.getAesExtraDataRecord().getCompressionMethod();
				} else {
					throw new RZPException("AESExtraDataRecord does not exist for AES encrypted file: " + fileHeader.getFileName());
				}
			}
			raf.seek(offsetStartOfData);
			switch (compressionMethod) {
			case RZPConstants.COMP_STORE:
				return new RZPInputStream(new PartInputStream(raf, offsetStartOfData, comprSize, this));
			case RZPConstants.COMP_DEFLATE:
				return new RZPInputStream(new InflaterInputStream(raf, offsetStartOfData, comprSize, this));
			default:
				throw new RZPException("compression type not supported");
			}
		} catch (RZPException e) {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e1) {
					//ignore
				}
			}
			throw e;
		} catch (Exception e) {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e1) {
				}
			}
			throw new RZPException(e);
		}
		
	}
	
	private void init(RandomAccessFile raf) throws RZPException {
		
		if (localFileHeader == null) {
			throw new RZPException("local file header is null, cannot initialize input stream");
		}
		
		try {
			initDecrypter(raf);
		} catch (RZPException e) {
			throw e;
		} catch (Exception e) {
			throw new RZPException(e);
		}
	}
	
	private void initDecrypter(RandomAccessFile raf) throws RZPException {
		if (localFileHeader == null) {
			throw new RZPException("local file header is null, cannot init decrypter");
		}
		
		if (localFileHeader.isEncrypted()) {
			if (localFileHeader.getEncryptionMethod() == RZPConstants.ENC_METHOD_STANDARD) {
				decrypter = new StandardDecrypter(fileHeader, getStandardDecrypterHeaderBytes(raf));
			} else if (localFileHeader.getEncryptionMethod() == RZPConstants.ENC_METHOD_AES) {
				decrypter = new AESDecrypter(localFileHeader, getAESSalt(raf), getAESPasswordVerifier(raf));
			} else {
				throw new RZPException("unsupported encryption method");
			}
		}
	}
	
	private byte[] getStandardDecrypterHeaderBytes(RandomAccessFile raf) throws RZPException {
		try {
			byte[] headerBytes = new byte[InternalRZPConstants.STD_DEC_HDR_SIZE];
			raf.seek(localFileHeader.getOffsetStartOfData());
			raf.read(headerBytes, 0, 12);
			return headerBytes;
		} catch (IOException e) {
			throw new RZPException(e);
		} catch (Exception e) {
			throw new RZPException(e);
		}
	}
	
	private byte[] getAESSalt(RandomAccessFile raf) throws RZPException {
		if (localFileHeader.getAesExtraDataRecord() == null)
			return null;
		
		try {
			AESExtraDataRecord aesExtraDataRecord = localFileHeader.getAesExtraDataRecord();
			byte[] saltBytes = new byte[calculateAESSaltLength(aesExtraDataRecord)]; 			
			raf.seek(localFileHeader.getOffsetStartOfData());
			raf.read(saltBytes);
			return saltBytes;
		} catch (IOException e) {
			throw new RZPException(e);
		}
	}
	
	private byte[] getAESPasswordVerifier(RandomAccessFile raf) throws RZPException {
		try {
			byte[] pvBytes = new byte[2];
			raf.read(pvBytes);
			return pvBytes;
		} catch (IOException e) {
			throw new RZPException(e);
		}
	}
	
	private int calculateAESSaltLength(AESExtraDataRecord aesExtraDataRecord) throws RZPException {
		if (aesExtraDataRecord == null) {
			throw new RZPException("unable to determine salt length: AESExtraDataRecord is null");
		}
		switch (aesExtraDataRecord.getAesStrength()) {
		case RZPConstants.AES_STRENGTH_128:
			return 8;
		case RZPConstants.AES_STRENGTH_192:
			return 12;
		case RZPConstants.AES_STRENGTH_256:
			return 16;
		default:
			throw new RZPException("unable to determine salt length: invalid aes key strength");
		}
	}
	
	public void checkCRC() throws RZPException {
		if (fileHeader != null) {
			if (fileHeader.getEncryptionMethod() == RZPConstants.ENC_METHOD_AES) {
				if (decrypter != null && decrypter instanceof AESDecrypter) {
					byte[] tmpMacBytes = ((AESDecrypter)decrypter).getCalculatedAuthenticationBytes();
					byte[] storedMac = ((AESDecrypter)decrypter).getStoredMac();
					byte[] calculatedMac = new byte[InternalRZPConstants.AES_AUTH_LENGTH]; 
					
					if (calculatedMac == null || storedMac == null) {
						throw new RZPException("CRC (MAC) check failed for " + fileHeader.getFileName());
					}
					
					System.arraycopy(tmpMacBytes, 0, calculatedMac, 0, InternalRZPConstants.AES_AUTH_LENGTH);
					
					if (!Arrays.equals(calculatedMac, storedMac)) {
						throw new RZPException("invalid CRC (MAC) for file: " + fileHeader.getFileName());
					}
				}
			} else {
				long calculatedCRC = crc.getValue() & 0xffffffffL;
				if (calculatedCRC != fileHeader.getCrc32()) {
					String errMsg = "invalid CRC for file: " + fileHeader.getFileName();
					if (localFileHeader.isEncrypted() && 
							localFileHeader.getEncryptionMethod() == RZPConstants.ENC_METHOD_STANDARD) {
						errMsg += " - Wrong Password?";
					}
					throw new RZPException(errMsg);
				}
			}
		}
	}
	
//	private void checkCRC() throws ZipException {
//		if (fileHeader != null) {
//			if (fileHeader.getEncryptionMethod() == Zip4jConstants.ENC_METHOD_AES) {
//				if (decrypter != null && decrypter instanceof AESDecrypter) {
//					byte[] tmpMacBytes = ((AESDecrypter)decrypter).getCalculatedAuthenticationBytes();
//					byte[] actualMacBytes = ((AESDecrypter)decrypter).getStoredMac();
//					if (tmpMacBytes == null || actualMacBytes == null) {
//						throw new ZipException("null mac value for AES encrypted file: " + fileHeader.getFileName());
//					}
//					byte[] calcMacBytes = new byte[10];
//					System.arraycopy(tmpMacBytes, 0, calcMacBytes, 0, 10);
//					if (!Arrays.equals(calcMacBytes, actualMacBytes)) {
//						throw new ZipException("invalid CRC(mac) for file: " + fileHeader.getFileName());
//					}
//				} else {
//					throw new ZipException("invalid decryptor...cannot calculate mac value for file: " 
//							+ fileHeader.getFileName());
//				}
//			} else if (unzipEngine != null) {
//				long calculatedCRC = unzipEngine.getCRC();
//				long actualCRC = fileHeader.getCrc32();
//				if (calculatedCRC != actualCRC) {
//					throw new ZipException("invalid CRC for file: " + fileHeader.getFileName());
//				}
//			}
//		}
//	}
	
	private boolean checkLocalHeader() throws RZPException {
		RandomAccessFile rafForLH = null;
		try {
			rafForLH = checkSplitFile();
			
			if (rafForLH == null) {
				rafForLH = new RandomAccessFile(new File(this.zipModel.getZipFile()), InternalRZPConstants.READ_MODE);
			}
			
			HeaderReader headerReader = new HeaderReader(rafForLH);
			this.localFileHeader = headerReader.readLocalFileHeader(fileHeader);
			
			if (localFileHeader == null) {
				throw new RZPException("error reading local file header. Is this a valid zip file?");
			}
			
			//TODO Add more comparision later
			if (localFileHeader.getCompressionMethod() != fileHeader.getCompressionMethod()) {
				return false;
			}
			
			return true;
		} catch (FileNotFoundException e) {
			throw new RZPException(e);
		} finally {
			if (rafForLH != null) {
				try {
					rafForLH.close();
				} catch (IOException e) {
					// Ignore this
				} catch (Exception e) {
					//Ignore this
				}
			}
		}
	}
	
	private RandomAccessFile checkSplitFile() throws RZPException {
		if (zipModel.isSplitArchive()) {
			int diskNumberStartOfFile = fileHeader.getDiskNumberStart();
			currSplitFileCounter = diskNumberStartOfFile + 1;
			String curZipFile = zipModel.getZipFile();
			String partFile = null;
			if (diskNumberStartOfFile == zipModel.getEndCentralDirRecord().getNoOfThisDisk()) {
				partFile = zipModel.getZipFile();
			} else {
				if (diskNumberStartOfFile >= 9) {
					partFile = curZipFile.substring(0, curZipFile.lastIndexOf(".")) + ".z" + (diskNumberStartOfFile+ 1);
				} else{
					partFile = curZipFile.substring(0, curZipFile.lastIndexOf(".")) + ".z0" + (diskNumberStartOfFile+ 1);
				}
			}
			
			try {
				RandomAccessFile raf = new RandomAccessFile(partFile, InternalRZPConstants.READ_MODE);
				
				if (currSplitFileCounter == 1) {
					byte[] splitSig = new byte[4];
					raf.read(splitSig);
					if (Raw.readIntLittleEndian(splitSig, 0) != InternalRZPConstants.SPLITSIG()) {
						throw new RZPException("invalid first part split file signature");
					}
				}
				return raf;
			} catch (FileNotFoundException e) {
				throw new RZPException(e);
			} catch (IOException e) {
				throw new RZPException(e);
			}
		}
		return null;
	}
	
	private RandomAccessFile createFileHandler(String mode) throws RZPException {
		if (this.zipModel == null || !RZPUtil.isStringNotNullAndNotEmpty(this.zipModel.getZipFile())) {
			throw new RZPException("input parameter is null in getFilePointer");
		}
		
		try {
			RandomAccessFile raf = null;
			if (zipModel.isSplitArchive()) {
				raf = checkSplitFile();
			} else {
				raf = new RandomAccessFile(new File(this.zipModel.getZipFile()), mode);
			}
			return raf;
		} catch (FileNotFoundException e) {
			throw new RZPException(e);
		} catch (Exception e) {
			throw new RZPException(e);
		}
	}
	
	private FileOutputStream getOutputStream(String outPath, String newFileName) throws RZPException {
		if (!RZPUtil.isStringNotNullAndNotEmpty(outPath)) {
			throw new RZPException("invalid output path");
		}
		
		try {
			File file = new File(getOutputFileNameWithPath(outPath, newFileName));
			
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			
			if (file.exists()) {
				file.delete();
			}
			
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			return fileOutputStream;
		} catch (FileNotFoundException e) {
			throw new RZPException(e);
		}
	}
	
	private String getOutputFileNameWithPath(String outPath, String newFileName) throws RZPException {
		String fileName = null;
		if (RZPUtil.isStringNotNullAndNotEmpty(newFileName)) {
			fileName = newFileName;
		} else {
			fileName = fileHeader.getFileName();
		}
		return outPath + System.getProperty("file.separator") + fileName;
	}
	
	public RandomAccessFile startNextSplitFile() throws IOException, FileNotFoundException {
		String currZipFile = zipModel.getZipFile();
		String partFile = null;
		if (currSplitFileCounter == zipModel.getEndCentralDirRecord().getNoOfThisDisk()) {
			partFile = zipModel.getZipFile();
		} else {
			if (currSplitFileCounter >= 9) {
				partFile = currZipFile.substring(0, currZipFile.lastIndexOf(".")) + ".z" + (currSplitFileCounter + 1);
			} else {
				partFile = currZipFile.substring(0, currZipFile.lastIndexOf(".")) + ".z0" + (currSplitFileCounter + 1);
			}
		}
		currSplitFileCounter++;
		try {
			if(!RZPUtil.checkFileExists(partFile)) {
				throw new IOException("zip split file does not exist: " + partFile);
			}
		} catch (RZPException e) {
			throw new IOException(e.getMessage());
		}
		return new RandomAccessFile(partFile, InternalRZPConstants.READ_MODE);
	}
	
	private void closeStreams(InputStream is, OutputStream os) throws RZPException {
		try {
			if (is != null) {
				is.close();
				is = null;
			}
		} catch (IOException e) {
			if (e != null && RZPUtil.isStringNotNullAndNotEmpty(e.getMessage())) {
				if (e.getMessage().indexOf(" - Wrong Password?") >= 0) {
					throw new RZPException(e.getMessage());
				}
			}
		} finally {
			try {
				if (os != null) {
					os.close();
					os = null;
				}
			} catch (IOException e) {
				//do nothing
			}
		}
	}
	
	public void updateCRC(int b) {
		crc.update(b);
	}
	
	public void updateCRC(byte[] buff, int offset, int len) {
		if (buff != null) {
			crc.update(buff, offset, len);
		}
	}

	public FileHeader getFileHeader() {
		return fileHeader;
	}

	public IDecrypter getDecrypter() {
		return decrypter;
	}

	public RZPModel getZipModel() {
		return zipModel;
	}

	public LocalFileHeader getLocalFileHeader() {
		return localFileHeader;
	}
}

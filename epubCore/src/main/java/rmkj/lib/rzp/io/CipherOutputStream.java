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
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.CRC32;

import rmkj.lib.rzp.core.HeaderWriter;
import rmkj.lib.rzp.crypto.AESEncrpyter;
import rmkj.lib.rzp.crypto.IEncrypter;
import rmkj.lib.rzp.crypto.StandardEncrypter;
import rmkj.lib.rzp.exception.RZPException;
import rmkj.lib.rzp.model.AESExtraDataRecord;
import rmkj.lib.rzp.model.CentralDirectory;
import rmkj.lib.rzp.model.EndCentralDirRecord;
import rmkj.lib.rzp.model.FileHeader;
import rmkj.lib.rzp.model.LocalFileHeader;
import rmkj.lib.rzp.model.RZPParameters;
import rmkj.lib.rzp.model.RZPModel;
import rmkj.lib.rzp.util.InternalRZPConstants;
import rmkj.lib.rzp.util.RZPConstants;
import rmkj.lib.rzp.util.RZPUtil;
import rmkj.lib.rzp.util.Raw;

public class CipherOutputStream extends BaseOutputStream {
	
	protected OutputStream outputStream;
	private File sourceFile;
	protected FileHeader fileHeader;
	protected LocalFileHeader localFileHeader;
	private IEncrypter encrypter;
	protected RZPParameters zipParameters;
	protected RZPModel zipModel;
	private long totalBytesWritten;
	protected CRC32 crc;
	private long bytesWrittenForThisFile;
	private byte[] pendingBuffer;
	private int pendingBufferLength;
	private long totalBytesRead;
	
	public CipherOutputStream(OutputStream outputStream, RZPModel zipModel) {
		this.outputStream = outputStream;
		initZipModel(zipModel);
		crc = new CRC32();
		this.totalBytesWritten = 0;
		this.bytesWrittenForThisFile = 0;
		this.pendingBuffer = new byte[InternalRZPConstants.AES_BLOCK_SIZE];
		this.pendingBufferLength = 0;
		this.totalBytesRead = 0;
	}
	
	public void putNextEntry(File file, RZPParameters zipParameters) throws RZPException {
		if (!zipParameters.isSourceExternalStream() && file == null) {
			throw new RZPException("input file is null");
		}
		
		if (!zipParameters.isSourceExternalStream() && !RZPUtil.checkFileExists(file)) {
			throw new RZPException("input file does not exist");
		}
		
		if (zipParameters == null) {
			zipParameters = new RZPParameters();
		}
		
		try {
			sourceFile = file;
			
			this.zipParameters = (RZPParameters)zipParameters.clone();
			
			if (!zipParameters.isSourceExternalStream()) {
				if (sourceFile.isDirectory()) {
					this.zipParameters.setEncryptFiles(false);
					this.zipParameters.setEncryptionMethod(-1);
					this.zipParameters.setCompressionMethod(RZPConstants.COMP_STORE);
				}
			} else {
				if (!RZPUtil.isStringNotNullAndNotEmpty(this.zipParameters.getFileNameInZip())) {
					throw new RZPException("file name is empty for external stream");
				}
				if (this.zipParameters.getFileNameInZip().endsWith("/") || 
						this.zipParameters.getFileNameInZip().endsWith("\\")) {
					this.zipParameters.setEncryptFiles(false);
					this.zipParameters.setEncryptionMethod(-1);
					this.zipParameters.setCompressionMethod(RZPConstants.COMP_STORE);
				}
			}
			
			createFileHeader();
			createLocalFileHeader();
			
			if (zipModel.isSplitArchive()) {
				if (zipModel.getCentralDirectory() == null || 
						zipModel.getCentralDirectory().getFileHeaders() == null || 
						zipModel.getCentralDirectory().getFileHeaders().size() == 0) {
					byte[] intByte = new byte[4];
					Raw.writeIntLittleEndian(intByte, 0, (int)InternalRZPConstants.SPLITSIG());
					outputStream.write(intByte);
					totalBytesWritten += 4;
				}
			}
			
			if (this.outputStream instanceof SplitOutputStream) {
				if (totalBytesWritten == 4) {
					fileHeader.setOffsetLocalHeader(4);
				} else {
					fileHeader.setOffsetLocalHeader(((SplitOutputStream)outputStream).getFilePointer());
				}
			} else {
				if (totalBytesWritten == 4) {
					fileHeader.setOffsetLocalHeader(4);
				} else {
					fileHeader.setOffsetLocalHeader(totalBytesWritten);
				}
			}
			
			HeaderWriter headerWriter = new HeaderWriter();
			totalBytesWritten += headerWriter.writeLocalFileHeader(zipModel, localFileHeader, outputStream);
			
			if (this.zipParameters.isEncryptFiles()) {
				initEncrypter();
				if (encrypter != null) {
					if (zipParameters.getEncryptionMethod() == RZPConstants.ENC_METHOD_STANDARD) {
						byte[] headerBytes = ((StandardEncrypter)encrypter).getHeaderBytes();
						outputStream.write(headerBytes);
						totalBytesWritten += headerBytes.length;
						bytesWrittenForThisFile += headerBytes.length;
					} else if (zipParameters.getEncryptionMethod() == RZPConstants.ENC_METHOD_AES) {
						byte[] saltBytes = ((AESEncrpyter)encrypter).getSaltBytes();
						byte[] passwordVerifier = ((AESEncrpyter)encrypter).getDerivedPasswordVerifier();
						outputStream.write(saltBytes);
						outputStream.write(passwordVerifier);
						totalBytesWritten += saltBytes.length + passwordVerifier.length;
						bytesWrittenForThisFile += saltBytes.length + passwordVerifier.length;
					}
				}
			} 
			
			crc.reset();
		} catch (CloneNotSupportedException e) {
			throw new RZPException(e);
		} catch (RZPException e) {
			throw e;
		} catch (Exception e) {
			throw new RZPException(e);
		}
	}
	
	private void initEncrypter() throws RZPException {
		if (!zipParameters.isEncryptFiles()) {
			encrypter = null;
			return;
		}
		
		switch (zipParameters.getEncryptionMethod()) {
		case RZPConstants.ENC_METHOD_STANDARD:
			// Since we do not know the crc here, we use the modification time for encrypting.
			encrypter = new StandardEncrypter(zipParameters.getPassword(), (localFileHeader.getLastModFileTime() & 0x0000ffff) << 16);
			break;
		case RZPConstants.ENC_METHOD_AES:
			encrypter = new AESEncrpyter(zipParameters.getPassword(), zipParameters.getAesKeyStrength());
			break;
		default:
			throw new RZPException("invalid encprytion method");
		}
	}
	
	private void initZipModel(RZPModel zipModel) {
		if (zipModel == null) {
			this.zipModel = new RZPModel();
		} else {
			this.zipModel = zipModel;
		}
		
		if (this.zipModel.getEndCentralDirRecord() == null)
			this.zipModel.setEndCentralDirRecord(new EndCentralDirRecord());
		
		if (this.zipModel.getCentralDirectory() == null)
			this.zipModel.setCentralDirectory(new CentralDirectory());
		
		if (this.zipModel.getCentralDirectory().getFileHeaders() == null)
			this.zipModel.getCentralDirectory().setFileHeaders(new ArrayList());
		
		if (this.zipModel.getLocalFileHeaderList() == null)
			this.zipModel.setLocalFileHeaderList(new ArrayList());
		
		if (this.outputStream instanceof SplitOutputStream) {
			if (((SplitOutputStream)outputStream).isSplitZipFile()) {
				this.zipModel.setSplitArchive(true);
				this.zipModel.setSplitLength(((SplitOutputStream)outputStream).getSplitLength());
			}
		}
		
		this.zipModel.getEndCentralDirRecord().setSignature(InternalRZPConstants.ENDSIG());
	}
	
	public void write(int bval) throws IOException {
	    byte[] b = new byte[1];
	    b[0] = (byte) bval;
	    write(b, 0, 1);
	}
	
	public void write(byte[] b) throws IOException {
		if (b == null)
			throw new NullPointerException();
		
		if (b.length == 0) return;
		
		write(b, 0, b.length);
	}
	
	public void write(byte[] b, int off, int len) throws IOException {
		if (len == 0) return;
		
		if (zipParameters.isEncryptFiles() && 
				zipParameters.getEncryptionMethod() == RZPConstants.ENC_METHOD_AES) {
			if (pendingBufferLength != 0) {
				if (len >= (InternalRZPConstants.AES_BLOCK_SIZE - pendingBufferLength)) {
					System.arraycopy(b, off, pendingBuffer, pendingBufferLength,
									(InternalRZPConstants.AES_BLOCK_SIZE - pendingBufferLength));
					encryptAndWrite(pendingBuffer, 0, pendingBuffer.length);
					off = (InternalRZPConstants.AES_BLOCK_SIZE - pendingBufferLength);
					len = len - off;
					pendingBufferLength = 0;
				} else {
					System.arraycopy(b, off, pendingBuffer, pendingBufferLength,
							len);
					pendingBufferLength += len;
					return;
				}
			}
			if (len != 0 && len % 16 != 0) {
				System.arraycopy(b, (len + off) - (len % 16), pendingBuffer, 0, len % 16);
				pendingBufferLength = len % 16;
				len = len - pendingBufferLength; 
			}
		}
		if (len != 0)
			encryptAndWrite(b, off, len);
	}
	
	private void encryptAndWrite(byte[] b, int off, int len) throws IOException {
		if (encrypter != null) {
			try {
				encrypter.encryptData(b, off, len);
			} catch (RZPException e) {
				throw new IOException(e.getMessage());
			}
		}
		outputStream.write(b, off, len);
		totalBytesWritten += len;
		bytesWrittenForThisFile += len;
	}
	
	public void closeEntry() throws IOException, RZPException {
		
		if (this.pendingBufferLength != 0) {
			encryptAndWrite(pendingBuffer, 0, pendingBufferLength);
			pendingBufferLength = 0;
		}
		
		if (this.zipParameters.isEncryptFiles() && 
				this.zipParameters.getEncryptionMethod() == RZPConstants.ENC_METHOD_AES) {
			if (encrypter instanceof AESEncrpyter) {
				outputStream.write(((AESEncrpyter)encrypter).getFinalMac());
				bytesWrittenForThisFile += 10;
				totalBytesWritten += 10;
			} else {
				throw new RZPException("invalid encrypter for AES encrypted file");
			}
		}
		fileHeader.setCompressedSize(bytesWrittenForThisFile);
		localFileHeader.setCompressedSize(bytesWrittenForThisFile);
		
		if (zipParameters.isSourceExternalStream()) {
			fileHeader.setUncompressedSize(totalBytesRead);
			if (localFileHeader.getUncompressedSize() != totalBytesRead) {
				localFileHeader.setUncompressedSize(totalBytesRead);
			}
		}
		
		long crc32 = crc.getValue();
		if (fileHeader.isEncrypted()) {
			if (fileHeader.getEncryptionMethod() == RZPConstants.ENC_METHOD_AES) {
				crc32 = 0;
			}
		}
		
		if (zipParameters.isEncryptFiles() && 
				zipParameters.getEncryptionMethod() == RZPConstants.ENC_METHOD_AES) {
			fileHeader.setCrc32(0);
			localFileHeader.setCrc32(0);
		} else {
			fileHeader.setCrc32(crc32);
			localFileHeader.setCrc32(crc32);
		}
		
		zipModel.getLocalFileHeaderList().add(localFileHeader);
		zipModel.getCentralDirectory().getFileHeaders().add(fileHeader);
		
		HeaderWriter headerWriter = new HeaderWriter();
		totalBytesWritten += headerWriter.writeExtendedLocalHeader(localFileHeader, outputStream);
		
		crc.reset();
		bytesWrittenForThisFile = 0;
		encrypter = null;
		totalBytesRead = 0;
	}
	
	public void finish() throws IOException, RZPException {
		zipModel.getEndCentralDirRecord().setOffsetOfStartOfCentralDir(totalBytesWritten);
		
		HeaderWriter headerWriter = new HeaderWriter();
		headerWriter.finalizeZipFile(zipModel, outputStream);
	}
	
	public void close() throws IOException {
		if (outputStream != null)
			outputStream.close();
	}
	
	private void createFileHeader() throws RZPException {
		this.fileHeader = new FileHeader();
		fileHeader.setSignature((int)InternalRZPConstants.CENSIG());
		fileHeader.setVersionMadeBy(20);
		fileHeader.setVersionNeededToExtract(20);
		if (zipParameters.isEncryptFiles() && 
				zipParameters.getEncryptionMethod() == RZPConstants.ENC_METHOD_AES) {
			fileHeader.setCompressionMethod(RZPConstants.ENC_METHOD_AES);
			fileHeader.setAesExtraDataRecord(generateAESExtraDataRecord(zipParameters));
		} else {
			fileHeader.setCompressionMethod(zipParameters.getCompressionMethod());
		}
		if (zipParameters.isEncryptFiles()) {
			fileHeader.setEncrypted(true);
			fileHeader.setEncryptionMethod(zipParameters.getEncryptionMethod());
		}
		String fileName = null;
		if (zipParameters.isSourceExternalStream()) {
			fileHeader.setLastModFileTime((int) RZPUtil.javaToDosTime(System.currentTimeMillis()));
			if (!RZPUtil.isStringNotNullAndNotEmpty(zipParameters.getFileNameInZip())) {
				throw new RZPException("fileNameInZip is null or empty");
			}
			fileName = zipParameters.getFileNameInZip();
		} else {
			fileHeader.setLastModFileTime((int) RZPUtil.javaToDosTime((RZPUtil.getLastModifiedFileTime(
					sourceFile, zipParameters.getTimeZone()))));
			fileHeader.setUncompressedSize(sourceFile.length());
			fileName = RZPUtil.getRelativeFileName(
					sourceFile.getAbsolutePath(), zipParameters.getRootFolderInZip(), zipParameters.getDefaultFolderPath());
			
		}
		
		if (!RZPUtil.isStringNotNullAndNotEmpty(fileName)) {
			throw new RZPException("fileName is null or empty. unable to create file header");
		}
		
		fileHeader.setFileName(fileName);
		
		if (RZPUtil.isStringNotNullAndNotEmpty(zipModel.getFileNameCharset())) {
			fileHeader.setFileNameLength(RZPUtil.getEncodedStringLength(fileName, 
					zipModel.getFileNameCharset()));
		} else {
			fileHeader.setFileNameLength(RZPUtil.getEncodedStringLength(fileName));
		}
		
		if (outputStream instanceof SplitOutputStream) {
			fileHeader.setDiskNumberStart(((SplitOutputStream)outputStream).getCurrSplitFileCounter());
		} else {
			fileHeader.setDiskNumberStart(0);
		}
		
		int fileAttrs = 0;
		if (!zipParameters.isSourceExternalStream())
			fileAttrs = getFileAttributes(sourceFile);
		byte[] externalFileAttrs = {(byte)fileAttrs, 0, 0, 0};
		fileHeader.setExternalFileAttr(externalFileAttrs);
		
		if (zipParameters.isSourceExternalStream()) {
			fileHeader.setDirectory(fileName.endsWith("/") || fileName.endsWith("\\"));
		} else {
			fileHeader.setDirectory(this.sourceFile.isDirectory());
		}
		if (fileHeader.isDirectory()) {
			fileHeader.setCompressedSize(0);
			fileHeader.setUncompressedSize(0);
		} else {
			if (!zipParameters.isSourceExternalStream()) {
				long fileSize = RZPUtil.getFileLengh(sourceFile);
				if (zipParameters.getCompressionMethod() == RZPConstants.COMP_STORE) {
					if (zipParameters.getEncryptionMethod() == RZPConstants.ENC_METHOD_STANDARD) {
						fileHeader.setCompressedSize(fileSize
								+ InternalRZPConstants.STD_DEC_HDR_SIZE);
					} else if (zipParameters.getEncryptionMethod() == RZPConstants.ENC_METHOD_AES) {
						int saltLength = 0;
						switch (zipParameters.getAesKeyStrength()) {
						case RZPConstants.AES_STRENGTH_128:
							saltLength = 8;
							break;
						case RZPConstants.AES_STRENGTH_256:
							saltLength = 16;
							break;
						default:
							throw new RZPException("invalid aes key strength, cannot determine key sizes");
						}
						fileHeader.setCompressedSize(fileSize + saltLength
								+ InternalRZPConstants.AES_AUTH_LENGTH + 2); //2 is password verifier
					} else {
						fileHeader.setCompressedSize(0);
					}
				} else {
					fileHeader.setCompressedSize(0);
				}
				fileHeader.setUncompressedSize(fileSize);
			}
		}
		if (zipParameters.isEncryptFiles() && 
				zipParameters.getEncryptionMethod() == RZPConstants.ENC_METHOD_STANDARD) {
			fileHeader.setCrc32(zipParameters.getSourceFileCRC());
		}
		byte[] shortByte = new byte[2]; 
		shortByte[0] = Raw.bitArrayToByte(generateGeneralPurposeBitArray(
				fileHeader.isEncrypted(), zipParameters.getCompressionMethod()));
		boolean isFileNameCharsetSet = RZPUtil.isStringNotNullAndNotEmpty(zipModel.getFileNameCharset());
	    if ((isFileNameCharsetSet &&
	            zipModel.getFileNameCharset().equalsIgnoreCase(InternalRZPConstants.CHARSET_UTF8)) ||
	        (!isFileNameCharsetSet &&
	            RZPUtil.detectCharSet(fileHeader.getFileName()).equals(InternalRZPConstants.CHARSET_UTF8))) {
	        shortByte[1] = 8;
	    } else {
	        shortByte[1] = 0;
	    }
		fileHeader.setGeneralPurposeFlag(shortByte);
	}
	
	private void createLocalFileHeader() throws RZPException {
		if (fileHeader == null) {
			throw new RZPException("file header is null, cannot create local file header");
		}
		this.localFileHeader = new LocalFileHeader();
		localFileHeader.setSignature((int)InternalRZPConstants.LOCSIG());
		localFileHeader.setVersionNeededToExtract(fileHeader.getVersionNeededToExtract());
		localFileHeader.setCompressionMethod(fileHeader.getCompressionMethod());
		localFileHeader.setLastModFileTime(fileHeader.getLastModFileTime());
		localFileHeader.setUncompressedSize(fileHeader.getUncompressedSize());
		localFileHeader.setFileNameLength(fileHeader.getFileNameLength());
		localFileHeader.setFileName(fileHeader.getFileName());
		localFileHeader.setEncrypted(fileHeader.isEncrypted());
		localFileHeader.setEncryptionMethod(fileHeader.getEncryptionMethod());
		localFileHeader.setAesExtraDataRecord(fileHeader.getAesExtraDataRecord());
		localFileHeader.setCrc32(fileHeader.getCrc32());
		localFileHeader.setCompressedSize(fileHeader.getCompressedSize());
		localFileHeader.setGeneralPurposeFlag((byte[])fileHeader.getGeneralPurposeFlag().clone());
	}
	
	/**
	 * Checks the file attributes and returns an integer
	 * @param file
	 * @return
	 * @throws RZPException
	 */
	private int getFileAttributes(File file) throws RZPException {
		if (file == null) {
			throw new RZPException("input file is null, cannot get file attributes");
		}
		
		if (!file.exists()) {
			return 0;
		}
		
		if (file.isDirectory()) {
			if (file.isHidden()) {
				return InternalRZPConstants.FOLDER_MODE_HIDDEN;
			} else {
				return InternalRZPConstants.FOLDER_MODE_NONE;
			}
		} else {
			if (!file.canWrite() && file.isHidden()) {
				return InternalRZPConstants.FILE_MODE_READ_ONLY_HIDDEN;
			} else if (!file.canWrite()) {
				return InternalRZPConstants.FILE_MODE_READ_ONLY;
			} else if (file.isHidden()) {
				return InternalRZPConstants.FILE_MODE_HIDDEN;
			} else {
				return InternalRZPConstants.FILE_MODE_NONE;
			}
		}
	}
	
	private int[] generateGeneralPurposeBitArray(boolean isEncrpyted, int compressionMethod) {
		
		int[] generalPurposeBits = new int[8];
		if (isEncrpyted) {
			generalPurposeBits[0] = 1;
		} else {
			generalPurposeBits[0] = 0;
		}
		
		if (compressionMethod == RZPConstants.COMP_DEFLATE) {
			// Have to set flags for deflate
		} else {
			generalPurposeBits[1] = 0;
			generalPurposeBits[2] = 0;
		}

		generalPurposeBits[3] = 1;
		
		return generalPurposeBits;
	}
	
	private AESExtraDataRecord generateAESExtraDataRecord(RZPParameters parameters) throws RZPException {
		
		if (parameters == null) {
			throw new RZPException("zip parameters are null, cannot generate AES Extra Data record");
		}
		
		AESExtraDataRecord aesDataRecord = new AESExtraDataRecord();
		aesDataRecord.setSignature(InternalRZPConstants.AESSIG());
		aesDataRecord.setDataSize(7);
		aesDataRecord.setVendorID("AE");
		// Always set the version number to 2 as we do not store CRC for any AES encrypted files
		// only MAC is stored and as per the specification, if version number is 2, then MAC is read
		// and CRC is ignored
		aesDataRecord.setVersionNumber(2); 
		if (parameters.getAesKeyStrength() == RZPConstants.AES_STRENGTH_128) {
			aesDataRecord.setAesStrength(RZPConstants.AES_STRENGTH_128);
		} else if (parameters.getAesKeyStrength() == RZPConstants.AES_STRENGTH_256) {
			aesDataRecord.setAesStrength(RZPConstants.AES_STRENGTH_256);
		} else {
			throw new RZPException("invalid AES key strength, cannot generate AES Extra data record");
		}
		aesDataRecord.setCompressionMethod(parameters.getCompressionMethod());
		
		return aesDataRecord;
	}
	
	public void decrementCompressedFileSize(int value) {
		if (value <= 0) return;
		
		if (value <= this.bytesWrittenForThisFile) {
			this.bytesWrittenForThisFile -= value;
		}
	}
	
	protected void updateTotalBytesRead(int toUpdate) {
		if (toUpdate > 0) {
			totalBytesRead += toUpdate;
		}
	}
	
	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public File getSourceFile() {
		return sourceFile;
	}
}

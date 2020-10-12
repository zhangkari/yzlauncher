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

package rmkj.lib.rzp.rzp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;

import rmkj.lib.rzp.exception.RZPException;
import rmkj.lib.rzp.io.RZPOutputStream;
import rmkj.lib.rzp.io.SplitOutputStream;
import rmkj.lib.rzp.model.EndCentralDirRecord;
import rmkj.lib.rzp.model.FileHeader;
import rmkj.lib.rzp.model.RZPParameters;
import rmkj.lib.rzp.model.RZPModel;
import rmkj.lib.rzp.progress.ProgressMonitor;
import rmkj.lib.rzp.util.ArchiveMaintainer;
import rmkj.lib.rzp.util.CRCUtil;
import rmkj.lib.rzp.util.InternalRZPConstants;
import rmkj.lib.rzp.util.RZPConstants;
import rmkj.lib.rzp.util.RZPUtil;

public class RZPEngine {
	
	private RZPModel zipModel;
	
	public RZPEngine(RZPModel zipModel) throws RZPException {
		
		if (zipModel == null) {
			throw new RZPException("zip model is null in ZipEngine constructor");
		}
		
		this.zipModel = zipModel;
	}
	
	public void addFiles(final ArrayList fileList, final RZPParameters parameters,
			final ProgressMonitor progressMonitor, boolean runInThread) throws RZPException {
		
		if (fileList == null || parameters == null) {
			throw new RZPException("one of the input parameters is null when adding files");
		}
		
		if(fileList.size() <= 0) {
			throw new RZPException("no files to add");
		}
		
		progressMonitor.setTotalWork(calculateTotalWork(fileList, parameters));
		progressMonitor.setCurrentOperation(ProgressMonitor.OPERATION_ADD);
		progressMonitor.setState(ProgressMonitor.STATE_BUSY);
		progressMonitor.setResult(ProgressMonitor.RESULT_WORKING);
		
		if (runInThread) {
			
			Thread thread = new Thread(InternalRZPConstants.THREAD_NAME) {
				public void run() {
					try {
						initAddFiles(fileList, parameters, progressMonitor);
					} catch (RZPException e) {
					}
				}
			};
			thread.start();
			
		} else {
			initAddFiles(fileList, parameters, progressMonitor);
		}
	}
	
	private void initAddFiles(ArrayList fileList, RZPParameters parameters,
			ProgressMonitor progressMonitor) throws RZPException {
		
		if (fileList == null || parameters == null) {
			throw new RZPException("one of the input parameters is null when adding files");
		}
		
		if(fileList.size() <= 0) {
			throw new RZPException("no files to add");
		}
		
		if (zipModel.getEndCentralDirRecord() == null) {
			zipModel.setEndCentralDirRecord(createEndOfCentralDirectoryRecord());
		}
		
		RZPOutputStream outputStream  = null;
		InputStream inputStream = null;
		try {
			checkParameters(parameters);
			
			removeFilesIfExists(fileList, parameters, progressMonitor);
			
			boolean isZipFileAlreadExists = RZPUtil.checkFileExists(zipModel.getZipFile());
			
			SplitOutputStream splitOutputStream = new SplitOutputStream(new File(zipModel.getZipFile()), zipModel.getSplitLength());
			outputStream = new RZPOutputStream(splitOutputStream, this.zipModel);
			
			if (isZipFileAlreadExists) {
				if (zipModel.getEndCentralDirRecord() == null) {
					throw new RZPException("invalid end of central directory record");
				}
				splitOutputStream.seek(zipModel.getEndCentralDirRecord().getOffsetOfStartOfCentralDir());
			}
			byte[] readBuff = new byte[InternalRZPConstants.BUFF_SIZE];
			int readLen = -1;
			for (int i = 0; i < fileList.size(); i++) {
				RZPParameters fileParameters = (RZPParameters) parameters.clone();
				
				progressMonitor.setFileName(((File)fileList.get(i)).getAbsolutePath());
				
				if (!((File)fileList.get(i)).isDirectory()) {
					if (fileParameters.isEncryptFiles() && fileParameters.getEncryptionMethod() == RZPConstants.ENC_METHOD_STANDARD) {
						progressMonitor.setCurrentOperation(ProgressMonitor.OPERATION_CALC_CRC);
						fileParameters.setSourceFileCRC((int)CRCUtil.computeFileCRC(((File)fileList.get(i)).getAbsolutePath(), progressMonitor));
						progressMonitor.setCurrentOperation(ProgressMonitor.OPERATION_ADD);
					}
					
					if (RZPUtil.getFileLengh((File)fileList.get(i)) == 0) {
						fileParameters.setCompressionMethod(RZPConstants.COMP_STORE);
					}
				}
				
				outputStream.putNextEntry((File)fileList.get(i), fileParameters);
				if (((File)fileList.get(i)).isDirectory()) {
					outputStream.closeEntry();
					continue;
				}
				
				inputStream = new FileInputStream((File)fileList.get(i));
				
				while ((readLen = inputStream.read(readBuff)) != -1) {
					outputStream.write(readBuff, 0, readLen);
					progressMonitor.updateWorkCompleted(readLen);
				}
				
				outputStream.closeEntry();
				
				if (inputStream != null) {
					inputStream.close();
				}
			}
			
			outputStream.finish();
			progressMonitor.endProgressMonitorSuccess();
		} catch (RZPException e) {
			progressMonitor.endProgressMonitorError(e);
			throw e;
		} catch (Exception e) {
			progressMonitor.endProgressMonitorError(e);
			throw new RZPException(e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
			
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public void addStreamToZip(InputStream inputStream, RZPParameters parameters) throws RZPException {
		if (inputStream == null || parameters == null) {
			throw new RZPException("one of the input parameters is null, cannot add stream to zip");
		}
		
		RZPOutputStream outputStream  = null;
		
		try {
			checkParameters(parameters);
			
			boolean isZipFileAlreadExists = RZPUtil.checkFileExists(zipModel.getZipFile());
			
			SplitOutputStream splitOutputStream = new SplitOutputStream(new File(zipModel.getZipFile()), zipModel.getSplitLength());
			outputStream = new RZPOutputStream(splitOutputStream, this.zipModel);
			
			if (isZipFileAlreadExists) {
				if (zipModel.getEndCentralDirRecord() == null) {
					throw new RZPException("invalid end of central directory record");
				}
				splitOutputStream.seek(zipModel.getEndCentralDirRecord().getOffsetOfStartOfCentralDir());
			}
			
			byte[] readBuff = new byte[InternalRZPConstants.BUFF_SIZE];
			int readLen = -1;
			
			outputStream.putNextEntry(null, parameters);
			
			if (!parameters.getFileNameInZip().endsWith("/") && 
					!parameters.getFileNameInZip().endsWith("\\")) {
				while ((readLen = inputStream.read(readBuff)) != -1) {
					outputStream.write(readBuff, 0, readLen);
				}
			}
			
			outputStream.closeEntry();
			outputStream.finish();
			
		} catch (RZPException e) {
			throw e;
		} catch (Exception e) {
			throw new RZPException(e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					//ignore
				}
			}
		}
	}
	
	public void addFolderToZip(File file, RZPParameters parameters, 
			ProgressMonitor progressMonitor, boolean runInThread) throws RZPException {
		if (file == null || parameters == null) {
			throw new RZPException("one of the input parameters is null, cannot add folder to zip");
		}
		
		if (!RZPUtil.checkFileExists(file.getAbsolutePath())) {
			throw new RZPException("input folder does not exist");
		}
		
		if (!file.isDirectory()) {
			throw new RZPException("input file is not a folder, user addFileToZip method to add files");
		}
		
		if (!RZPUtil.checkFileReadAccess(file.getAbsolutePath())) {
			throw new RZPException("cannot read folder: " + file.getAbsolutePath());
		}
		
		String rootFolderPath = null;
		if (parameters.isIncludeRootFolder()) {
			if (file.getAbsolutePath() != null) {
				rootFolderPath = file.getAbsoluteFile().getParentFile() != null ? file.getAbsoluteFile().getParentFile().getAbsolutePath() : "";
			} else {
				rootFolderPath = file.getParentFile() != null ? file.getParentFile().getAbsolutePath() : "";
			}
		} else {
		    rootFolderPath = file.getAbsolutePath();
		}
		
		parameters.setDefaultFolderPath(rootFolderPath);
		
		ArrayList fileList = RZPUtil.getFilesInDirectoryRec(file, parameters.isReadHiddenFiles());
		
		if (parameters.isIncludeRootFolder()) {
			if (fileList == null) {
				fileList = new ArrayList();
			}
			fileList.add(file);
		}
		
		addFiles(fileList, parameters, progressMonitor, runInThread);
	}
	
	
	private void checkParameters(RZPParameters parameters) throws RZPException {
		
		if (parameters == null) {
			throw new RZPException("cannot validate zip parameters");
		}
		
		if ((parameters.getCompressionMethod() != RZPConstants.COMP_STORE) && 
				parameters.getCompressionMethod() != RZPConstants.COMP_DEFLATE) {
			throw new RZPException("unsupported compression type");
		}
		
		if (parameters.getCompressionMethod() == RZPConstants.COMP_DEFLATE) {
			if (parameters.getCompressionLevel() < 0 && parameters.getCompressionLevel() > 9) {
				throw new RZPException("invalid compression level. compression level dor deflate should be in the range of 0-9");
			}
		}
		
		if (parameters.isEncryptFiles()) {
			if (parameters.getEncryptionMethod() != RZPConstants.ENC_METHOD_STANDARD && 
					parameters.getEncryptionMethod() != RZPConstants.ENC_METHOD_AES) {
				throw new RZPException("unsupported encryption method");
			}
			
			if (parameters.getPassword() == null || parameters.getPassword().length <= 0) {
				throw new RZPException("input password is empty or null");
			}
		} else {
			parameters.setAesKeyStrength(-1);
			parameters.setEncryptionMethod(-1);
		}
		
	}
	
	/**
	 * Before adding a file to a zip file, we check if a file already exists in the zip file
	 * with the same fileName (including path, if exists). If yes, then we remove this file
	 * before adding the file<br><br>
	 * 
	 * <b>Note:</b> Relative path has to be passed as the fileName
	 * 
	 * @param zipModel
	 * @param fileName
	 * @throws RZPException
	 */
	private void removeFilesIfExists(ArrayList fileList, RZPParameters parameters, ProgressMonitor progressMonitor) throws RZPException {
		
		if (zipModel == null || zipModel.getCentralDirectory() == null || 
				zipModel.getCentralDirectory().getFileHeaders() == null || 
						zipModel.getCentralDirectory().getFileHeaders().size() <= 0) {
			//For a new zip file, this condition satisfies, so do nothing
			return;
		}
		RandomAccessFile outputStream = null;
		
		try {
			for (int i = 0; i < fileList.size(); i++) {
				File file = (File) fileList.get(i);
				
				String fileName = RZPUtil.getRelativeFileName(file.getAbsolutePath(), 
						parameters.getRootFolderInZip(), parameters.getDefaultFolderPath());
				
				FileHeader fileHeader = RZPUtil.getFileHeader(zipModel, fileName);
				if (fileHeader != null) {

					if (outputStream != null) {
						outputStream.close();
						outputStream = null;
					}

					ArchiveMaintainer archiveMaintainer = new ArchiveMaintainer();
					progressMonitor.setCurrentOperation(ProgressMonitor.OPERATION_REMOVE);
					HashMap retMap = archiveMaintainer.initRemoveZipFile(zipModel,
							fileHeader, progressMonitor);

					if (progressMonitor.isCancelAllTasks()) {
						progressMonitor.setResult(ProgressMonitor.RESULT_CANCELLED);
						progressMonitor.setState(ProgressMonitor.STATE_READY);
						return;
					}

					progressMonitor
							.setCurrentOperation(ProgressMonitor.OPERATION_ADD);

					if (outputStream == null) {
						outputStream = prepareFileOutputStream();

						if (retMap != null) {
							if (retMap.get(InternalRZPConstants.OFFSET_CENTRAL_DIR) != null) {
								long offsetCentralDir = -1;
								try {
									offsetCentralDir = Long
											.parseLong((String) retMap
													.get(InternalRZPConstants.OFFSET_CENTRAL_DIR));
								} catch (NumberFormatException e) {
									throw new RZPException(
											"NumberFormatException while parsing offset central directory. " +
											"Cannot update already existing file header");
								} catch (Exception e) {
									throw new RZPException(
											"Error while parsing offset central directory. " +
											"Cannot update already existing file header");
								}

								if (offsetCentralDir >= 0) {
									outputStream.seek(offsetCentralDir);
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			throw new RZPException(e);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					//ignore
				}
			}
		}
	}
	
	private RandomAccessFile prepareFileOutputStream() throws RZPException {
		String outPath = zipModel.getZipFile();
		if (!RZPUtil.isStringNotNullAndNotEmpty(outPath)) {
			throw new RZPException("invalid output path");
		}
		
		try {
			File outFile = new File(outPath);
			if (!outFile.getParentFile().exists()) {
				outFile.getParentFile().mkdirs();
			}
			return new RandomAccessFile(outFile, InternalRZPConstants.WRITE_MODE);
		} catch (FileNotFoundException e) {
			throw new RZPException(e);
		}
	}
	
	private EndCentralDirRecord createEndOfCentralDirectoryRecord() {
		EndCentralDirRecord endCentralDirRecord = new EndCentralDirRecord();
		endCentralDirRecord.setSignature(InternalRZPConstants.ENDSIG());
		endCentralDirRecord.setNoOfThisDisk(0);
		endCentralDirRecord.setTotNoOfEntriesInCentralDir(0);
		endCentralDirRecord.setTotNoOfEntriesInCentralDirOnThisDisk(0);
		endCentralDirRecord.setOffsetOfStartOfCentralDir(0);
		return endCentralDirRecord;
	}
	
	private long calculateTotalWork(ArrayList fileList, RZPParameters parameters) throws RZPException {
		if (fileList == null) {
			throw new RZPException("file list is null, cannot calculate total work");
		}
		
		long totalWork = 0;
		
		for (int i = 0; i < fileList.size(); i++) {
			if(fileList.get(i) instanceof File) {
				if (((File)fileList.get(i)).exists()) {
					if (parameters.isEncryptFiles() && 
							parameters.getEncryptionMethod() == RZPConstants.ENC_METHOD_STANDARD) {
						totalWork += (RZPUtil.getFileLengh((File)fileList.get(i)) * 2);
					} else {
						totalWork += RZPUtil.getFileLengh((File)fileList.get(i));
					}
					
					if (zipModel.getCentralDirectory() != null && 
							zipModel.getCentralDirectory().getFileHeaders() != null && 
							zipModel.getCentralDirectory().getFileHeaders().size() > 0) {
						String relativeFileName = RZPUtil.getRelativeFileName(
								((File)fileList.get(i)).getAbsolutePath(), parameters.getRootFolderInZip(), parameters.getDefaultFolderPath());
						FileHeader fileHeader = RZPUtil.getFileHeader(zipModel, relativeFileName);
						if (fileHeader != null) {
							totalWork += (RZPUtil.getFileLengh(new File(zipModel.getZipFile())) - fileHeader.getCompressedSize());
						}
					}
				}
			}
		}
		
		return totalWork;
	}
}

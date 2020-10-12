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
import java.util.ArrayList;

import rmkj.lib.rzp.exception.RZPException;
import rmkj.lib.rzp.io.RZPInputStream;
import rmkj.lib.rzp.model.CentralDirectory;
import rmkj.lib.rzp.model.FileHeader;
import rmkj.lib.rzp.model.RZPModel;
import rmkj.lib.rzp.model.UnRZPParameters;
import rmkj.lib.rzp.progress.ProgressMonitor;
import rmkj.lib.rzp.util.InternalRZPConstants;
import rmkj.lib.rzp.util.RZPUtil;

public class UnRZP {
	
	private RZPModel zipModel;
	
	public UnRZP(RZPModel zipModel) throws RZPException {
		
		if (zipModel == null) {
			throw new RZPException("ZipModel is null");
		}
		
		this.zipModel = zipModel;
	}
	
	public void extractAll(final UnRZPParameters unzipParameters, final String outPath,
			final ProgressMonitor progressMonitor, boolean runInThread) throws RZPException {
		
		CentralDirectory centralDirectory = zipModel.getCentralDirectory();
		
		if (centralDirectory == null || 
				centralDirectory.getFileHeaders() == null) {
			throw new RZPException("invalid central directory in zipModel");
		}
		
		final ArrayList fileHeaders = centralDirectory.getFileHeaders();
		
		progressMonitor.setCurrentOperation(ProgressMonitor.OPERATION_EXTRACT);
		progressMonitor.setTotalWork(calculateTotalWork(fileHeaders));
		progressMonitor.setState(ProgressMonitor.STATE_BUSY);
		
		if (runInThread) {
			Thread thread = new Thread(InternalRZPConstants.THREAD_NAME) {
				public void run() {
					try {
						initExtractAll(fileHeaders, unzipParameters, progressMonitor, outPath);
						progressMonitor.endProgressMonitorSuccess();
					} catch (RZPException e) {
					}
				}
			};
			thread.start();
		} else {
			initExtractAll(fileHeaders, unzipParameters, progressMonitor, outPath);
		}
		
	}
	
	private void initExtractAll(ArrayList fileHeaders, UnRZPParameters unzipParameters, 
			ProgressMonitor progressMonitor, String outPath) throws RZPException {
		
		for (int i = 0; i < fileHeaders.size(); i++) {
			FileHeader fileHeader = (FileHeader)fileHeaders.get(i);
			initExtractFile(fileHeader, outPath, unzipParameters, null, progressMonitor);
			if (progressMonitor.isCancelAllTasks()) {
				progressMonitor.setResult(ProgressMonitor.RESULT_CANCELLED);
				progressMonitor.setState(ProgressMonitor.STATE_READY);
				return;
			}
		}
	}
	
	public void extractFile(final FileHeader fileHeader, final String outPath,
			final UnRZPParameters unzipParameters, final String newFileName, 
			final ProgressMonitor progressMonitor, boolean runInThread) throws RZPException {
		if (fileHeader == null) {
			throw new RZPException("fileHeader is null");
		}
		
		progressMonitor.setCurrentOperation(ProgressMonitor.OPERATION_EXTRACT);
		progressMonitor.setTotalWork(fileHeader.getCompressedSize());
		progressMonitor.setState(ProgressMonitor.STATE_BUSY);
		progressMonitor.setPercentDone(0);
		progressMonitor.setFileName(fileHeader.getFileName());
		
		if (runInThread) {
			Thread thread = new Thread(InternalRZPConstants.THREAD_NAME) {
				public void run() {
					try {
						initExtractFile(fileHeader, outPath, unzipParameters, newFileName, progressMonitor);
						progressMonitor.endProgressMonitorSuccess();
					} catch (RZPException e) {
					}
				}
			};
			thread.start();
		} else {
			initExtractFile(fileHeader, outPath, unzipParameters, newFileName, progressMonitor);
			progressMonitor.endProgressMonitorSuccess();
		}
		
	}
	
	private void initExtractFile(FileHeader fileHeader, String outPath,
			UnRZPParameters unzipParameters, String newFileName, ProgressMonitor progressMonitor) throws RZPException {

		if (fileHeader == null) {
			throw new RZPException("fileHeader is null");
		}
		
		try {
			progressMonitor.setFileName(fileHeader.getFileName());
			
			if (!outPath.endsWith(InternalRZPConstants.FILE_SEPARATOR)) {
				outPath += InternalRZPConstants.FILE_SEPARATOR;
			}
			
			// If file header is a directory, then check if the directory exists
			// If not then create a directory and return
			if (fileHeader.isDirectory()) {
				try {
					String fileName = fileHeader.getFileName();
					if (!RZPUtil.isStringNotNullAndNotEmpty(fileName)) {
						return;
					}
					String completePath = outPath + fileName;
					File file = new File(completePath);
					if (!file.exists()) {
						file.mkdirs();
					}
				} catch (Exception e) {
					progressMonitor.endProgressMonitorError(e);
					throw new RZPException(e);
				}
			} else {
				//Create Directories
				checkOutputDirectoryStructure(fileHeader, outPath, newFileName);
				
				UnRZPEngine unzipEngine = new UnRZPEngine(zipModel, fileHeader);
				try {
					unzipEngine.unzipFile(progressMonitor, outPath, newFileName, unzipParameters);
				} catch (Exception e) {
					progressMonitor.endProgressMonitorError(e);
					throw new RZPException(e);
				}
			}
		} catch (RZPException e) {
			progressMonitor.endProgressMonitorError(e);
			throw e;
		} catch (Exception e) {
			progressMonitor.endProgressMonitorError(e);
			throw new RZPException(e);
		}
	}
	
	public RZPInputStream getInputStream(FileHeader fileHeader) throws RZPException {
		UnRZPEngine unzipEngine = new UnRZPEngine(zipModel, fileHeader);
		return unzipEngine.getInputStream();
	}
	
	private void checkOutputDirectoryStructure(FileHeader fileHeader, String outPath, String newFileName) throws RZPException {
		if (fileHeader == null || !RZPUtil.isStringNotNullAndNotEmpty(outPath)) {
			throw new RZPException("Cannot check output directory structure...one of the parameters was null");
		}
		
		String fileName = fileHeader.getFileName();
		
		if (RZPUtil.isStringNotNullAndNotEmpty(newFileName)) {
			fileName = newFileName;
		}
		
		if (!RZPUtil.isStringNotNullAndNotEmpty(fileName)) {
			// Do nothing
			return;
		}
		
		String compOutPath = outPath + fileName;
		try {
			File file = new File(compOutPath);
			String parentDir = file.getParent();
			File parentDirFile = new File(parentDir);
			if (!parentDirFile.exists()) {
				parentDirFile.mkdirs();
			}
		} catch (Exception e) {
			throw new RZPException(e);
		}
	}
	
	private long calculateTotalWork(ArrayList fileHeaders) throws RZPException {
		
		if (fileHeaders == null) {
			throw new RZPException("fileHeaders is null, cannot calculate total work");
		}
		
		long totalWork = 0;
		
		for (int i = 0; i < fileHeaders.size(); i++) {
			FileHeader fileHeader = (FileHeader)fileHeaders.get(i);
			if (fileHeader.getZip64ExtendedInfo() != null && 
					fileHeader.getZip64ExtendedInfo().getUnCompressedSize() > 0) {
				totalWork += fileHeader.getZip64ExtendedInfo().getCompressedSize();
			} else {
				totalWork += fileHeader.getCompressedSize();
			}
			
		}
		
		return totalWork;
	}
	
}

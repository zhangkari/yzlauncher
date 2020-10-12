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

package rmkj.lib.rzp.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import rmkj.lib.rzp.exception.RZPException;
import rmkj.lib.rzp.exception.RZPExceptionConstants;
import rmkj.lib.rzp.io.RZPInputStream;
import rmkj.lib.rzp.model.FileHeader;
import rmkj.lib.rzp.model.RZPModel;
import rmkj.lib.rzp.model.RZPParameters;
import rmkj.lib.rzp.model.UnRZPParameters;
import rmkj.lib.rzp.progress.ProgressMonitor;
import rmkj.lib.rzp.rzp.RZPEngine;
import rmkj.lib.rzp.unrzp.UnRZP;
import rmkj.lib.rzp.util.ArchiveMaintainer;
import rmkj.lib.rzp.util.InternalRZPConstants;
import rmkj.lib.rzp.util.RZPUtil;

/**
 * Base class to handle zip files. Some of the operations supported
 * in this class are:<br>
 * <ul>
 * 		<li>Create Zip File</li>
 * 		<li>Add files to zip file</li>
 * 		<li>Add folder to zip file</li>
 * 		<li>Extract files from zip files</li>
 * 		<li>Remove files from zip file</li>
 * </ul>
 *
 */

public class RZPFile {
	
	
	public final static int RZPMODE_UNKNOWN = -1;
	public final static int RZPMODE_RZP = 1;
	public final static int RZPMODE_ZIP = 2;
	
	
	private String file;
	private int mode;
	private RZPModel zipModel;
	private boolean isEncrypted;
	private ProgressMonitor progressMonitor;
	private boolean runInThread;
	private String fileNameCharset;
	
	/**
	 * Creates a new Zip File Object with the given zip file path.
	 * If the zip file does not exist, it is not created at this point. 
	 * @param zipFile
	 * @throws RZPException
	 */
	public RZPFile(String zipFile) throws RZPException {
		this(new File(zipFile));
	}
	
	
	public void setZipMode(int mode)
	{
		InternalRZPConstants.init(mode);
	}
	
	/**
	 * Creates a new Zip File Object with the input file.
	 * If the zip file does not exist, it is not created at this point.
	 * @param zipFile
	 * @throws RZPException
	 */
	public RZPFile(File zipFile) throws RZPException {
		if (zipFile == null) {
			throw new RZPException("Input zip file parameter is not null", 
					RZPExceptionConstants.inputZipParamIsNull);
		}
		
		this.file = zipFile.getPath();
		this.mode = InternalRZPConstants.MODE_UNZIP;
		this.progressMonitor = new ProgressMonitor();
		this.runInThread = false;
		InternalRZPConstants.init(RZPMODE_RZP);
	}
	
	/**
	 * Creates a zip file and adds the source file to the zip file. If the zip file
	 * exists then this method throws an exception. Parameters such as compression type, etc
	 * can be set in the input parameters
	 * @param sourceFile - File to be added to the zip file
	 * @param parameters - parameters to create the zip file
	 * @throws RZPException
	 */
	public void createZipFile(File sourceFile, RZPParameters parameters) throws RZPException {
		ArrayList<File> sourceFileList = new ArrayList<File>();
		sourceFileList.add(sourceFile);
		createZipFile(sourceFileList, parameters, false, -1);
	}
	
	/**
	 * Creates a zip file and adds the source file to the zip file. If the zip file
	 * exists then this method throws an exception. Parameters such as compression type, etc
	 * can be set in the input parameters. While the method addFile/addFiles also creates the 
	 * zip file if it does not exist, the main functionality of this method is to create a split
	 * zip file. To create a split zip file, set the splitArchive parameter to true with a valid
	 * splitLength. Split Length has to be more than 65536 bytes
	 * @param sourceFile - File to be added to the zip file
	 * @param parameters - parameters to create the zip file
	 * @param splitArchive - if archive has to be split or not
	 * @param splitLength - if archive has to be split, then length in bytes at which it has to be split
	 * @throws RZPException
	 */
	public void createZipFile(File sourceFile, RZPParameters parameters, 
			boolean splitArchive, long splitLength) throws RZPException {
		
		ArrayList<File> sourceFileList = new ArrayList<File>();
		sourceFileList.add(sourceFile);
		createZipFile(sourceFileList, parameters, splitArchive, splitLength);
	}
	
	/**
	 * Creates a zip file and adds the list of source file(s) to the zip file. If the zip file
	 * exists then this method throws an exception. Parameters such as compression type, etc
	 * can be set in the input parameters
	 * @param sourceFileList - File to be added to the zip file
	 * @param parameters - parameters to create the zip file
	 * @throws RZPException
	 */
	public void createZipFile(ArrayList<File> sourceFileList, 
			RZPParameters parameters) throws RZPException {
		createZipFile(sourceFileList, parameters, false, -1);
	}
	
	/**
	 * Creates a zip file and adds the list of source file(s) to the zip file. If the zip file
	 * exists then this method throws an exception. Parameters such as compression type, etc
	 * can be set in the input parameters. While the method addFile/addFiles also creates the 
	 * zip file if it does not exist, the main functionality of this method is to create a split
	 * zip file. To create a split zip file, set the splitArchive parameter to true with a valid
	 * splitLength. Split Length has to be more than 65536 bytes
	 * @param sourceFileList - File to be added to the zip file
	 * @param parameters - zip parameters for this file list
	 * @param splitArchive - if archive has to be split or not
	 * @param splitLength - if archive has to be split, then length in bytes at which it has to be split
	 * @throws RZPException
	 */
	public void createZipFile(ArrayList<File> sourceFileList, RZPParameters parameters, 
			boolean splitArchive, long splitLength) throws RZPException {
		
		if (!RZPUtil.isStringNotNullAndNotEmpty(file)) {
			throw new RZPException("zip file path is empty");
		}
		
		if (RZPUtil.checkFileExists(file)) {
			throw new RZPException("zip file: " + file + " already exists. To add files to existing zip file use addFile method");
		}
		
		if (sourceFileList == null) {
			throw new RZPException("input file ArrayList is null, cannot create zip file");
		}
		
		if (!RZPUtil.checkArrayListTypes(sourceFileList, InternalRZPConstants.LIST_TYPE_FILE)) {
			throw new RZPException("One or more elements in the input ArrayList is not of type File");
		}
		
		createNewZipModel();
		this.zipModel.setSplitArchive(splitArchive);
		this.zipModel.setSplitLength(splitLength);
		addFiles(sourceFileList, parameters);
	}
	
	/**
	 * Creates a zip file and adds the files/folders from the specified folder to the zip file.
	 * This method does the same functionality as in addFolder method except that this method
	 * can also create split zip files when adding a folder. To create a split zip file, set the 
	 * splitArchive parameter to true and specify the splitLength. Split length has to be more than
	 * or equal to 65536 bytes. Note that this method throws an exception if the zip file already 
	 * exists.
	 * @param folderToAdd
	 * @param parameters
	 * @param splitArchive
	 * @param splitLength
	 * @throws RZPException
	 */
	public void createZipFileFromFolder(String folderToAdd, RZPParameters parameters, 
			boolean splitArchive, long splitLength) throws RZPException {
		
		if (!RZPUtil.isStringNotNullAndNotEmpty(folderToAdd)) {
			throw new RZPException("folderToAdd is empty or null, cannot create Zip File from folder");
		}
		
		createZipFileFromFolder(new File(folderToAdd), parameters, splitArchive, splitLength);
		
	}
	
	/**
	 * Creates a zip file and adds the files/folders from the specified folder to the zip file.
	 * This method does the same functionality as in addFolder method except that this method
	 * can also create split zip files when adding a folder. To create a split zip file, set the 
	 * splitArchive parameter to true and specify the splitLength. Split length has to be more than
	 * or equal to 65536 bytes. Note that this method throws an exception if the zip file already 
	 * exists.
	 * @param folderToAdd
	 * @param parameters
	 * @param splitArchive
	 * @param splitLength
	 * @throws RZPException
	 */
	public void createZipFileFromFolder(File folderToAdd, RZPParameters parameters, 
			boolean splitArchive, long splitLength) throws RZPException {
		
		if (folderToAdd == null) {
			throw new RZPException("folderToAdd is null, cannot create zip file from folder");
		}
		
		if (parameters == null) {
			throw new RZPException("input parameters are null, cannot create zip file from folder");
		}
		
		if (RZPUtil.checkFileExists(file)) {
			throw new RZPException("zip file: " + file + " already exists. To add files to existing zip file use addFolder method");
		}
		
		createNewZipModel();
		this.zipModel.setSplitArchive(splitArchive);
		if (splitArchive)
			this.zipModel.setSplitLength(splitLength);
		
		addFolder(folderToAdd, parameters, false);
	}
	
	/**
	 * Adds input source file to the zip file. If zip file does not exist, then 
	 * this method creates a new zip file. Parameters such as compression type, etc
	 * can be set in the input parameters.
	 * @param sourceFile - File to tbe added to the zip file
	 * @param parameters - zip parameters for this file
	 * @throws RZPException
	 */
	public void addFile(File sourceFile, RZPParameters parameters) throws RZPException {
		ArrayList<File> sourceFileList = new ArrayList<File>();
		sourceFileList.add(sourceFile);
		addFiles(sourceFileList, parameters);
	}
	
	/**
	 * Adds the list of input files to the zip file. If zip file does not exist, then 
	 * this method creates a new zip file. Parameters such as compression type, etc
	 * can be set in the input parameters.
	 * @param sourceFileList
	 * @param parameters
	 * @throws RZPException
	 */
	public void addFiles(ArrayList<File> sourceFileList, RZPParameters parameters) throws RZPException {
		
		checkZipModel();
		
		if (this.zipModel == null) {
			throw new RZPException("internal error: zip model is null");
		}
		
		if (sourceFileList == null) {
			throw new RZPException("input file ArrayList is null, cannot add files");
		}
		
		if (!RZPUtil.checkArrayListTypes(sourceFileList, InternalRZPConstants.LIST_TYPE_FILE)) {
			throw new RZPException("One or more elements in the input ArrayList is not of type File");
		}
		
		if (parameters == null) {
			throw new RZPException("input parameters are null, cannot add files to zip");
		}
		
		if (progressMonitor.getState() == ProgressMonitor.STATE_BUSY) {
			throw new RZPException("invalid operation - Zip4j is in busy state");
		}
		
		if (RZPUtil.checkFileExists(file)) {
			if (zipModel.isSplitArchive()) {
				throw new RZPException("Zip file already exists. Zip file format does not allow updating split/spanned files");
			}
		}
		
		RZPEngine zipEngine = new RZPEngine(zipModel);
		zipEngine.addFiles(sourceFileList, parameters, progressMonitor, runInThread);
	}
	
	/**
	 * Adds the folder in the given path to the zip file. If zip file does not exist, 
	 * then a new zip file is created. If input folder path is invalid then an exception
	 * is thrown. Zip parameters for the files in the folder to be added can be set in
	 * the input parameters
	 * @param path
	 * @param parameters
	 * @throws RZPException
	 */
	public void addFolder(String path, RZPParameters parameters) throws RZPException {
		if (!RZPUtil.isStringNotNullAndNotEmpty(path)) {
			throw new RZPException("input path is null or empty, cannot add folder to zip file");
		}
		
		addFolder(new File(path), parameters);
	}
	
	/**
	 * Adds the folder in the given file object to the zip file. If zip file does not exist, 
	 * then a new zip file is created. If input folder is invalid then an exception
	 * is thrown. Zip parameters for the files in the folder to be added can be set in
	 * the input parameters
	 * @param path
	 * @param parameters
	 * @throws RZPException
	 */
	public void addFolder(File path, RZPParameters parameters) throws RZPException {
		if (path == null) {
			throw new RZPException("input path is null, cannot add folder to zip file");
		}
		
		if (parameters == null) {
			throw new RZPException("input parameters are null, cannot add folder to zip file");
		}
		
		addFolder(path, parameters, true);
	}
	
	/**
	 * Internal method to add a folder to the zip file.
	 * @param path
	 * @param parameters
	 * @param checkSplitArchive
	 * @throws RZPException
	 */
	private void addFolder(File path, RZPParameters parameters, 
			boolean checkSplitArchive) throws RZPException {
		
		checkZipModel();
		
		if (this.zipModel == null) {
			throw new RZPException("internal error: zip model is null");
		}
		
		if (checkSplitArchive) {
			if (this.zipModel.isSplitArchive()) {
				throw new RZPException("This is a split archive. Zip file format does not allow updating split/spanned files");
			}
		}
		
		RZPEngine zipEngine = new RZPEngine(zipModel);
		zipEngine.addFolderToZip(path, parameters, progressMonitor, runInThread);
		
	}
	
	/**
	 * Creates a new entry in the zip file and adds the content of the inputstream to the
	 * zip file. ZipParameters.isSourceExternalStream and ZipParameters.fileNameInZip have to be
	 * set before in the input parameters. If the file name ends with / or \, this method treats the
	 * content as a directory. Setting the flag ProgressMonitor.setRunInThread to true will have
	 * no effect for this method and hence this method cannot be used to add content to zip in
	 * thread mode
	 * @param inputStream
	 * @param parameters
	 * @throws RZPException
	 */
	public void addStream(InputStream inputStream, RZPParameters parameters) throws RZPException {
		if (inputStream == null) {
			throw new RZPException("inputstream is null, cannot add file to zip");
		}
		
		if (parameters == null) {
			throw new RZPException("zip parameters are null");
		}
		
		this.setRunInThread(false);
		
		checkZipModel();
		
		if (this.zipModel == null) {
			throw new RZPException("internal error: zip model is null");
		}
		
		if (RZPUtil.checkFileExists(file)) {
			if (zipModel.isSplitArchive()) {
				throw new RZPException("Zip file already exists. Zip file format does not allow updating split/spanned files");
			}
		}
		
		RZPEngine zipEngine = new RZPEngine(zipModel);
		zipEngine.addStreamToZip(inputStream, parameters);
	}
	
	/**
	 * Reads the zip header information for this zip file. If the zip file
	 * does not exist, then this method throws an exception.<br><br>
	 * <b>Note:</b> This method does not read local file header information
	 * @throws RZPException
	 */
	private void readZipInfo() throws RZPException {
		
		if (!RZPUtil.checkFileExists(file)) {
			throw new RZPException("zip file does not exist");
		}
		
		if (!RZPUtil.checkFileReadAccess(this.file)) {
			throw new RZPException("no read access for the input zip file");
		}
		
		if (this.mode != InternalRZPConstants.MODE_UNZIP) {
			throw new RZPException("Invalid mode");
		}
		
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(new File(file), InternalRZPConstants.READ_MODE);
			
			if (zipModel == null) {
				
				HeaderReader headerReader = new HeaderReader(raf);
				zipModel = headerReader.readAllHeaders(this.fileNameCharset);
				if (zipModel != null) {
					zipModel.setZipFile(file);
				}
			}
		} catch (FileNotFoundException e) {
			throw new RZPException(e);
		} finally {
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException e) {
					//ignore
				}
			}
		}
	}
	
	/**
	 * Extracts all the files in the given zip file to the input destination path.
	 * If zip file does not exist or destination path is invalid then an 
	 * exception is thrown. 
	 * @param destPath
	 * @throws RZPException
	 */
	public void extractAll(String destPath) throws RZPException {
		extractAll(destPath, null);
		
	}
	
	/**
	 * Extracts all the files in the given zip file to the input destination path.
	 * If zip file does not exist or destination path is invalid then an 
	 * exception is thrown.
	 * @param destPath
	 * @param unzipParameters
	 * @throws RZPException
	 */
	public void extractAll(String destPath, 
			UnRZPParameters unzipParameters) throws RZPException {
		
		if (!RZPUtil.isStringNotNullAndNotEmpty(destPath)) {
			throw new RZPException("output path is null or invalid");
		}
		
		if (!RZPUtil.checkOutputFolder(destPath)) {
			throw new RZPException("invalid output path");
		}
		
		if (zipModel == null) {
			readZipInfo();
		}
		
		// Throw an exception if zipModel is still null
		if (zipModel == null) {
			throw new RZPException("Internal error occurred when extracting zip file");
		}
		
		if (progressMonitor.getState() == ProgressMonitor.STATE_BUSY) {
			throw new RZPException("invalid operation - Zip4j is in busy state");
		}
		
		UnRZP unzip = new UnRZP(zipModel);
		unzip.extractAll(unzipParameters, destPath, progressMonitor, runInThread);
		
	}
	
	/**
	 * Extracts a specific file from the zip file to the destination path.
	 * If destination path is invalid, then this method throws an exception.
	 * @param fileHeader
	 * @param destPath
	 * @throws RZPException
	 */
	public void extractFile(FileHeader fileHeader, String destPath) throws RZPException {
		extractFile(fileHeader, destPath, null);
	}
	
	/**
	 * Extracts a specific file from the zip file to the destination path.
	 * If destination path is invalid, then this method throws an exception.
	 * <br><br>
	 * If newFileName is not null or empty, newly created file name will be replaced by 
	 * the value in newFileName. If this value is null, then the file name will be the 
	 * value in FileHeader.getFileName
	 * @param fileHeader
	 * @param destPath
	 * @param unzipParameters
	 * @throws RZPException
	 */
	public void extractFile(FileHeader fileHeader, 
			String destPath, UnRZPParameters unzipParameters) throws RZPException {
		extractFile(fileHeader, destPath, unzipParameters, null);
	}
	
	/**
	 * Extracts a specific file from the zip file to the destination path.
	 * If destination path is invalid, then this method throws an exception.
	 * @param fileHeader
	 * @param destPath
	 * @param unzipParameters
	 * @param newFileName
	 * @throws RZPException
	 */
	public void extractFile(FileHeader fileHeader, String destPath, 
			UnRZPParameters unzipParameters, String newFileName) throws RZPException {
		
		if (fileHeader == null) {
			throw new RZPException("input file header is null, cannot extract file");
		}
		
		if (!RZPUtil.isStringNotNullAndNotEmpty(destPath)) {
			throw new RZPException("destination path is empty or null, cannot extract file");
		}
		
		readZipInfo();
		
		if (progressMonitor.getState() == ProgressMonitor.STATE_BUSY) {
			throw new RZPException("invalid operation - Zip4j is in busy state");
		}
		
		fileHeader.extractFile(zipModel, destPath, unzipParameters, newFileName, progressMonitor, runInThread);
		
	}
	
	/**
	 * Extracts a specific file from the zip file to the destination path. 
	 * This method first finds the necessary file header from the input file name.
	 * <br><br>
	 * File name is relative file name in the zip file. For example if a zip file contains
	 * a file "a.txt", then to extract this file, input file name has to be "a.txt". Another
	 * example is if there is a file "b.txt" in a folder "abc" in the zip file, then the
	 * input file name has to be abc/b.txt
	 * <br><br>
	 * Throws an exception if file header could not be found for the given file name or if 
	 * the destination path is invalid
	 * @param fileName
	 * @param destPath
	 * @throws RZPException
	 */
	public void extractFile(String fileName, String destPath) throws RZPException {
		extractFile(fileName, destPath, null);
	}
	
	/**
	 * Extracts a specific file from the zip file to the destination path. 
	 * This method first finds the necessary file header from the input file name.
	 * <br><br>
	 * File name is relative file name in the zip file. For example if a zip file contains
	 * a file "a.txt", then to extract this file, input file name has to be "a.txt". Another
	 * example is if there is a file "b.txt" in a folder "abc" in the zip file, then the
	 * input file name has to be abc/b.txt
	 * <br><br>
	 * Throws an exception if file header could not be found for the given file name or if 
	 * the destination path is invalid
	 * @param fileName
	 * @param destPath
	 * @param unzipParameters
	 * @throws RZPException
	 */
	public void extractFile(String fileName, 
			String destPath, UnRZPParameters unzipParameters) throws RZPException {
		extractFile(fileName, destPath, unzipParameters, null);
	}
	
	/**
	 * Extracts a specific file from the zip file to the destination path. 
	 * This method first finds the necessary file header from the input file name.
	 * <br><br>
	 * File name is relative file name in the zip file. For example if a zip file contains
	 * a file "a.txt", then to extract this file, input file name has to be "a.txt". Another
	 * example is if there is a file "b.txt" in a folder "abc" in the zip file, then the
	 * input file name has to be abc/b.txt
	 * <br><br>
	 * If newFileName is not null or empty, newly created file name will be replaced by 
	 * the value in newFileName. If this value is null, then the file name will be the 
	 * value in FileHeader.getFileName
	 * <br><br>
	 * Throws an exception if file header could not be found for the given file name or if 
	 * the destination path is invalid
	 * @param fileName
	 * @param destPath
	 * @param unzipParameters
	 * @param newFileName
	 * @throws RZPException
	 */
	public void extractFile(String fileName, String destPath, 
			UnRZPParameters unzipParameters, String newFileName) throws RZPException {
		
		if (!RZPUtil.isStringNotNullAndNotEmpty(fileName)) {
			throw new RZPException("file to extract is null or empty, cannot extract file");
		}
		
		if (!RZPUtil.isStringNotNullAndNotEmpty(destPath)) {
			throw new RZPException("destination string path is empty or null, cannot extract file");
		}
		
		readZipInfo();
		
		FileHeader fileHeader = RZPUtil.getFileHeader(zipModel, fileName);
		
		if (fileHeader == null) {
			throw new RZPException("file header not found for given file name, cannot extract file");
		}
		
		if (progressMonitor.getState() == ProgressMonitor.STATE_BUSY) {
			throw new RZPException("invalid operation - Zip4j is in busy state");
		}
		
		fileHeader.extractFile(zipModel, destPath, unzipParameters, newFileName, progressMonitor, runInThread);
		
	}
	
	/**
	 * Sets the password for the zip file.<br>
	 * <b>Note</b>: For security reasons, usage of this method is discouraged. Use 
	 * setPassword(char[]) instead. As strings are immutable, they cannot be wiped
	 * out from memory explicitly after usage. Therefore, usage of Strings to store 
	 * passwords is discouraged. More info here: 
	 * http://docs.oracle.com/javase/1.5.0/docs/guide/security/jce/JCERefGuide.html#PBEEx
	 * @param password
	 * @throws RZPException
	 */
	public void setPassword(String password) throws RZPException {
		if (!RZPUtil.isStringNotNullAndNotEmpty(password)) {
			throw new NullPointerException();
		}
		setPassword(password.toCharArray());
	}
	
	/**
	 * Sets the password for the zip file
	 * @param password
	 * @throws RZPException
	 */
	public void setPassword(char[] password) throws RZPException {
		if (zipModel == null) {
			readZipInfo();
			if (zipModel == null) {
				throw new RZPException("Zip Model is null");
			}
		}
		
		if (zipModel.getCentralDirectory() == null || zipModel.getCentralDirectory().getFileHeaders() == null) {
			throw new RZPException("invalid zip file");
		}
		
		for (int i = 0; i < zipModel.getCentralDirectory().getFileHeaders().size(); i++) {
			if (zipModel.getCentralDirectory().getFileHeaders().get(i) != null) {
				if (((FileHeader)zipModel.getCentralDirectory().getFileHeaders().get(i)).isEncrypted()) {
					((FileHeader)zipModel.getCentralDirectory().getFileHeaders().get(i)).setPassword(password);
				}
			}
		}
	}
	
	/**
	 * Returns the list of file headers in the zip file. Throws an exception if the 
	 * zip file does not exist
	 * @return list of file headers
	 * @throws RZPException
	 */
	public List<?> getFileHeaders() throws RZPException {
		readZipInfo();
		if (zipModel == null || zipModel.getCentralDirectory() == null) {
			return null;
		}
		return zipModel.getCentralDirectory().getFileHeaders();
	}
	
	/**
	 * Returns FileHeader if a file header with the given fileHeader 
	 * string exists in the zip model: If not returns null
	 * @param fileName
	 * @return FileHeader
	 * @throws RZPException
	 */
	public FileHeader getFileHeader(String fileName) throws RZPException {
		if (!RZPUtil.isStringNotNullAndNotEmpty(fileName)) {
			throw new RZPException("input file name is emtpy or null, cannot get FileHeader");
		}
		
		readZipInfo();
		if (zipModel == null || zipModel.getCentralDirectory() == null) {
			return null;
		}
		
		return RZPUtil.getFileHeader(zipModel, fileName);
	}
	
	/**
	 * Checks to see if the zip file is encrypted
	 * @return true if encrypted, false if not
	 * @throws RZPException
	 */
	public boolean isEncrypted() throws RZPException {
		if (zipModel == null) {
			readZipInfo();
			if (zipModel == null) {
				throw new RZPException("Zip Model is null");
			}
		}
		
		if (zipModel.getCentralDirectory() == null || zipModel.getCentralDirectory().getFileHeaders() == null) {
			throw new RZPException("invalid zip file");
		}
		
		ArrayList<?> fileHeaderList = zipModel.getCentralDirectory().getFileHeaders();
		for (int i = 0; i < fileHeaderList.size(); i++) {
			FileHeader fileHeader = (FileHeader)fileHeaderList.get(i);
			if (fileHeader != null) {
				if (fileHeader.isEncrypted()) {
					isEncrypted = true;
					break;
				}
			}
		}
		
		return isEncrypted;
	}
	
	/**
	 * Checks if the zip file is a split archive
	 * @return true if split archive, false if not
	 * @throws RZPException
	 */
	public boolean isSplitArchive() throws RZPException {

		if (zipModel == null) {
			readZipInfo();
			if (zipModel == null) {
				throw new RZPException("Zip Model is null");
			}
		}
		
		return zipModel.isSplitArchive();
	
	}
	
	/**
	 * Removes the file provided in the input paramters from the zip file.
	 * This method first finds the file header and then removes the file.
	 * If file does not exist, then this method throws an exception.
	 * If zip file is a split zip file, then this method throws an exception as
	 * zip specification does not allow for updating split zip archives.
	 * @param fileName
	 * @throws RZPException
	 */
	public void removeFile(String fileName) throws RZPException {
		
		if (!RZPUtil.isStringNotNullAndNotEmpty(fileName)) {
			throw new RZPException("file name is empty or null, cannot remove file");
		}
		
		if (zipModel == null) {
			if (RZPUtil.checkFileExists(file)) {
				readZipInfo();
			}
		}
		
		if (zipModel.isSplitArchive()) {
			throw new RZPException("Zip file format does not allow updating split/spanned files");
		}
		
		FileHeader fileHeader = RZPUtil.getFileHeader(zipModel, fileName);
		if (fileHeader == null) {
			throw new RZPException("could not find file header for file: " + fileName);
		}
		
		removeFile(fileHeader);
	}
	
	/**
	 * Removes the file provided in the input file header from the zip file.
	 * If zip file is a split zip file, then this method throws an exception as
	 * zip specification does not allow for updating split zip archives.
	 * @param fileHeader
	 * @throws RZPException
	 */
	public void removeFile(FileHeader fileHeader) throws RZPException {
		if (fileHeader == null) {
			throw new RZPException("file header is null, cannot remove file");
		}
		
		if (zipModel == null) {
			if (RZPUtil.checkFileExists(file)) {
				readZipInfo();
			}
		}
		
		if (zipModel.isSplitArchive()) {
			throw new RZPException("Zip file format does not allow updating split/spanned files");
		}
		
		ArchiveMaintainer archiveMaintainer = new ArchiveMaintainer();
		archiveMaintainer.initProgressMonitorForRemoveOp(zipModel, fileHeader, progressMonitor);
		archiveMaintainer.removeZipFile(zipModel, fileHeader, progressMonitor, runInThread);
	}
	
	/**
	 * Merges split zip files into a single zip file without the need to extract the
	 * files in the archive
	 * @param outputZipFile
	 * @throws RZPException
	 */
	public void mergeSplitFiles(File outputZipFile) throws RZPException {
		if (outputZipFile == null) {
			throw new RZPException("outputZipFile is null, cannot merge split files");
		}
		
		if (outputZipFile.exists()) {
			throw new RZPException("output Zip File already exists");
		}
		
		checkZipModel();
		
		if (this.zipModel == null) {
			throw new RZPException("zip model is null, corrupt zip file?");
		}
		
		ArchiveMaintainer archiveMaintainer = new ArchiveMaintainer();
		archiveMaintainer.initProgressMonitorForMergeOp(zipModel, progressMonitor);
		archiveMaintainer.mergeSplitZipFiles(zipModel, outputZipFile, progressMonitor, runInThread);
	}
	
	/**
	 * Sets comment for the Zip file
	 * @param comment
	 * @throws RZPException
	 */
	public void setComment(String comment) throws RZPException {
		if (comment == null) {
			throw new RZPException("input comment is null, cannot update zip file");
		}
		
		if (!RZPUtil.checkFileExists(file)) {
			throw new RZPException("zip file does not exist, cannot set comment for zip file");
		}
		
		readZipInfo();
		
		if (this.zipModel == null) {
			throw new RZPException("zipModel is null, cannot update zip file");
		}
		
		if (zipModel.getEndCentralDirRecord() == null) {
			throw new RZPException("end of central directory is null, cannot set comment");
		}
		
		ArchiveMaintainer archiveMaintainer = new ArchiveMaintainer();
		archiveMaintainer.setComment(zipModel, comment);
	}
	
	/**
	 * Returns the comment set for the Zip file
	 * @return String
	 * @throws RZPException
	 */
	public String getComment() throws RZPException {
		return getComment(null);
	}
	
	/**
	 * Returns the comment set for the Zip file in the input encoding
	 * @param encoding
	 * @return String
	 * @throws RZPException
	 */
	public String getComment(String encoding) throws RZPException {
		if (encoding == null) {
			if (RZPUtil.isSupportedCharset(InternalRZPConstants.CHARSET_COMMENTS_DEFAULT)) {
				encoding = InternalRZPConstants.CHARSET_COMMENTS_DEFAULT;
			} else {
				encoding = InternalRZPConstants.CHARSET_DEFAULT;
			}
		}
		
		if (RZPUtil.checkFileExists(file)) {
			checkZipModel();
		} else {
			throw new RZPException("zip file does not exist, cannot read comment");
		}
		
		if (this.zipModel == null) {
			throw new RZPException("zip model is null, cannot read comment");
		}
		
		if (this.zipModel.getEndCentralDirRecord() == null) {
			throw new RZPException("end of central directory record is null, cannot read comment");
		}
		
		if (this.zipModel.getEndCentralDirRecord().getCommentBytes() == null || 
				this.zipModel.getEndCentralDirRecord().getCommentBytes().length <= 0) {
			return null;
		}
		
		try {
			return new String(this.zipModel.getEndCentralDirRecord().getCommentBytes(), encoding);
		} catch (UnsupportedEncodingException e) {
			throw new RZPException(e);
		}
	}
	
	/**
	 * Loads the zip model if zip model is null and if zip file exists.
	 * @throws RZPException
	 */
	private void checkZipModel() throws RZPException {
		if (this.zipModel == null) {
			if (RZPUtil.checkFileExists(file)) {
				readZipInfo();
			} else {
				createNewZipModel();
			}
		}
	}
	
	/**
	 * Creates a new instance of zip model
	 * @throws RZPException
	 */
	private void createNewZipModel() {
		zipModel = new RZPModel();
		zipModel.setZipFile(file);
		zipModel.setFileNameCharset(fileNameCharset);
	}
	
	/**
	 * Zip4j will encode all the file names with the input charset. This method throws
	 * an exception if the Charset is not supported
	 * @param charsetName
	 * @throws RZPException
	 */
	public void setFileNameCharset(String charsetName) throws RZPException {
		if (!RZPUtil.isStringNotNullAndNotEmpty(charsetName)) {
			throw new RZPException("null or empty charset name");
		}
		
		if (!RZPUtil.isSupportedCharset(charsetName)) {
			throw new RZPException("unsupported charset: " + charsetName);
		}
		
		this.fileNameCharset = charsetName;
	}
	
	/**
	 * Returns an input stream for reading the contents of the Zip file corresponding
	 * to the input FileHeader. Throws an exception if the FileHeader does not exist
	 * in the ZipFile
	 * @param fileHeader
	 * @return ZipInputStream
	 * @throws RZPException
	 */
	public RZPInputStream getInputStream(FileHeader fileHeader) throws RZPException {
		if (fileHeader == null) {
			throw new RZPException("FileHeader is null, cannot get InputStream");
		}
		
		checkZipModel();
		
		if (zipModel == null) {
			throw new RZPException("zip model is null, cannot get inputstream");
		}
		
		UnRZP unzip = new UnRZP(zipModel);
		return unzip.getInputStream(fileHeader);
	}
	
	/**
	 * Returns an input stream for reading the contents of the Zip file corresponding
	 * to the input FileHeader. Throws an exception if the FileHeader does not exist
	 * in the ZipFile
	 * @param fileHeader
	 * @return ZipInputStream
	 * @throws RZPException
	 */
	public RZPInputStream getInputStream(String fileName) throws RZPException {
		if (fileName == null) {
			throw new RZPException("fileName is null, cannot get InputStream");
		}
		
		checkZipModel();
		
		if (zipModel == null) {
			throw new RZPException("zip model is null, cannot get inputstream");
		}
		
		FileHeader fileHeader = RZPUtil.getFileHeader(zipModel, fileName);
		
		UnRZP unzip = new UnRZP(zipModel);
		return unzip.getInputStream(fileHeader);
	}
	
	
	/**
	 * Checks to see if the input zip file is a valid zip file. This method
	 * will try to read zip headers. If headers are read successfully, this
	 * method returns true else false 
	 * @return boolean
	 * @since 1.2.3
	 */
	public boolean isValidZipFile() {
		try {
			readZipInfo();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Returns the full file path+names of all split zip files 
	 * in an ArrayList. For example: If a split zip file(abc.zip) has a 10 split parts
	 * this method returns an array list with path + "abc.z01", path + "abc.z02", etc.
	 * Returns null if the zip file does not exist
	 * @return ArrayList of Strings
	 * @throws RZPException
	 */
	public ArrayList getSplitZipFiles() throws RZPException {
		checkZipModel();
		return RZPUtil.getSplitZipFiles(zipModel);
	}
	
	public ProgressMonitor getProgressMonitor() {
		return progressMonitor;
	}

	public boolean isRunInThread() {
		return runInThread;
	}

	public void setRunInThread(boolean runInThread) {
		this.runInThread = runInThread;
	}
	
	/**
	 * Returns the File object of the zip file 
	 * @return File
	 */
	public File getFile() {
		return new File(this.file);
	}
}

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

package rmkj.lib.rzp.model;

import java.io.FileOutputStream;

import rmkj.lib.rzp.crypto.IDecrypter;
import rmkj.lib.rzp.unrzp.UnRZPEngine;

public class UnRZPEngineParameters {
	
	private RZPModel zipModel;
	
	private FileHeader fileHeader;
	
	private LocalFileHeader localFileHeader;
	
	private IDecrypter iDecryptor;
	
	private FileOutputStream outputStream;
	
	private UnRZPEngine unzipEngine;

	public RZPModel getZipModel() {
		return zipModel;
	}

	public void setZipModel(RZPModel zipModel) {
		this.zipModel = zipModel;
	}

	public FileHeader getFileHeader() {
		return fileHeader;
	}

	public void setFileHeader(FileHeader fileHeader) {
		this.fileHeader = fileHeader;
	}

	public LocalFileHeader getLocalFileHeader() {
		return localFileHeader;
	}

	public void setLocalFileHeader(LocalFileHeader localFileHeader) {
		this.localFileHeader = localFileHeader;
	}

	public IDecrypter getIDecryptor() {
		return iDecryptor;
	}

	public void setIDecryptor(IDecrypter decrypter) {
		iDecryptor = decrypter;
	}

	public FileOutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(FileOutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public UnRZPEngine getUnzipEngine() {
		return unzipEngine;
	}

	public void setUnzipEngine(UnRZPEngine unzipEngine) {
		this.unzipEngine = unzipEngine;
	}
	
	
	
}

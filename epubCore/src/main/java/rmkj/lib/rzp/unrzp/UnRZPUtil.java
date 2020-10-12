package rmkj.lib.rzp.unrzp;

import java.io.File;

import rmkj.lib.rzp.exception.RZPException;
import rmkj.lib.rzp.model.FileHeader;
import rmkj.lib.rzp.model.UnRZPParameters;
import rmkj.lib.rzp.util.InternalRZPConstants;
import rmkj.lib.rzp.util.RZPUtil;

public class UnRZPUtil {
	
	public static void applyFileAttributes(FileHeader fileHeader, File file) throws RZPException {
		applyFileAttributes(fileHeader, file, null);
	}
	
	public static void applyFileAttributes(FileHeader fileHeader, File file,
			UnRZPParameters unzipParameters) throws RZPException{
		
		if (fileHeader == null) {
			throw new RZPException("cannot set file properties: file header is null");
		}
		
		if (file == null) {
			throw new RZPException("cannot set file properties: output file is null");
		}
		
		if (!RZPUtil.checkFileExists(file)) {
			throw new RZPException("cannot set file properties: file doesnot exist");
		}
		
		if (unzipParameters == null || !unzipParameters.isIgnoreDateTimeAttributes()) {
			setFileLastModifiedTime(fileHeader, file);
		}
		
		if (unzipParameters == null) {
			setFileAttributes(fileHeader, file, true, true, true, true);
		} else {
			if (unzipParameters.isIgnoreAllFileAttributes()) {
				setFileAttributes(fileHeader, file, false, false, false, false);
			} else {
				setFileAttributes(fileHeader, file, !unzipParameters.isIgnoreReadOnlyFileAttribute(),
						!unzipParameters.isIgnoreHiddenFileAttribute(), 
						!unzipParameters.isIgnoreArchiveFileAttribute(),
						!unzipParameters.isIgnoreSystemFileAttribute());
			}
		}
	}
	
	private static void setFileAttributes(FileHeader fileHeader, File file, boolean setReadOnly, 
			boolean setHidden, boolean setArchive, boolean setSystem) throws RZPException {
		if (fileHeader == null) {
			throw new RZPException("invalid file header. cannot set file attributes");
		}
		
		byte[] externalAttrbs = fileHeader.getExternalFileAttr();
		if (externalAttrbs == null) {
			return;
		}
		
		int atrrib = externalAttrbs[0];
		switch (atrrib) {
		case InternalRZPConstants.FILE_MODE_READ_ONLY:
			if (setReadOnly) RZPUtil.setFileReadOnly(file);
			break;
		case InternalRZPConstants.FILE_MODE_HIDDEN:
		case InternalRZPConstants.FOLDER_MODE_HIDDEN:
			if (setHidden) RZPUtil.setFileHidden(file);
			break;
		case InternalRZPConstants.FILE_MODE_ARCHIVE:
		case InternalRZPConstants.FOLDER_MODE_ARCHIVE:
			if (setArchive) RZPUtil.setFileArchive(file);
			break;
		case InternalRZPConstants.FILE_MODE_READ_ONLY_HIDDEN:
			if (setReadOnly) RZPUtil.setFileReadOnly(file);
			if (setHidden) RZPUtil.setFileHidden(file);
			break;
		case InternalRZPConstants.FILE_MODE_READ_ONLY_ARCHIVE:
			if (setArchive) RZPUtil.setFileArchive(file);
			if (setReadOnly) RZPUtil.setFileReadOnly(file);
			break;
		case InternalRZPConstants.FILE_MODE_HIDDEN_ARCHIVE:
		case InternalRZPConstants.FOLDER_MODE_HIDDEN_ARCHIVE:
			if (setArchive) RZPUtil.setFileArchive(file);
			if (setHidden) RZPUtil.setFileHidden(file);
			break;
		case InternalRZPConstants.FILE_MODE_READ_ONLY_HIDDEN_ARCHIVE:
			if (setArchive) RZPUtil.setFileArchive(file);
			if (setReadOnly) RZPUtil.setFileReadOnly(file);
			if (setHidden) RZPUtil.setFileHidden(file);
			break;
		case InternalRZPConstants.FILE_MODE_SYSTEM:
			if (setReadOnly) RZPUtil.setFileReadOnly(file);
			if (setHidden) RZPUtil.setFileHidden(file);
			if (setSystem) RZPUtil.setFileSystemMode(file);
			break;
		default:
			//do nothing
			break;
		}
	}
	
	private static void setFileLastModifiedTime(FileHeader fileHeader, File file) throws RZPException {
		if (fileHeader.getLastModFileTime() <= 0) {
			return;
		}
		
		if (file.exists()) {
			file.setLastModified(RZPUtil.dosToJavaTme(fileHeader.getLastModFileTime()));
		}
	}
	
}

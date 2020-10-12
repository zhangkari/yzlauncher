package rmkj.lib.read.epub.entity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import rmkj.lib.read.RMReadController;
import rmkj.lib.read.global.RMFileType;
import rmkj.lib.rzp.core.RZPFile;
import rmkj.lib.rzp.exception.RZPException;

public class RMEPUBResourceProvider {
    private RMFileType type;
    // private ZipFile zipFile;
    private RZPFile rzpFile;

    public RMEPUBResourceProvider() {
        try {
            setFileType(RMReadController.GLOBAL_DATA.FILE_TYPE);
        } catch (Exception e) {
        }
    }

    public void setFileType(RMFileType type) throws RZPException, IOException {
        this.type = type;
        switch (type) {
            case EPUB:
                break;
            case EPUB_RZP:
            case EPUB_ZIP:
                rzpFile = new RZPFile(RMReadController.GLOBAL_DATA.BOOK_PATH);

                boolean isEncry = rzpFile.isEncrypted();

                if (isEncry) {
                    rzpFile.setPassword(RMReadController.GLOBAL_DATA.PASS_WORDS);
                }

                break;
            // zipFile = new ZipFile(RMReadController.GLOBAL_DATA.BOOK_PATH);
            // break;
            case TXT:
                break;
        }
    }

    public InputStream getSpineContent(String src) throws IOException, RZPException {
        switch (type) {
            case EPUB_RZP:
            case EPUB_ZIP:
                return rzpFile.getInputStream(src);
            // return zipFile.getInputStream(zipFile.getEntry(src));
            case EPUB:
                return new FileInputStream(new File(src));
            case TXT:
                return new FileInputStream(new File(src));
        }
        return null;
    }

}

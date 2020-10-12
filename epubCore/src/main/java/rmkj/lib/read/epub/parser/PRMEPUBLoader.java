package rmkj.lib.read.epub.parser;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import rmkj.lib.exception.PRMException;
import rmkj.lib.read.RMReadController;
import rmkj.lib.read.epub.entity.RMEPUBContainer;
import rmkj.lib.read.epub.entity.RMEPUBMimeType;
import rmkj.lib.read.epub.entity.RMEPUBNCXManager;
import rmkj.lib.read.epub.entity.RMEPUBOPFManager;
import rmkj.lib.read.epub.entity.RMEPUBOPFManifestItem;
import rmkj.lib.read.epub.entity.RMEPUBObject;
import rmkj.lib.read.epub.entity.RMEPUBZipObject;
import rmkj.lib.read.txt.entry.RMTXTObject;
import rmkj.lib.read.txt.entry.TXTFileConverter;
import rmkj.lib.read.util.LogUtil;
import rmkj.lib.read.util.RMEncode;
import rmkj.lib.read.util.RMZipFileUtil;
import rmkj.lib.rzp.core.RZPFile;
import rmkj.lib.rzp.exception.RZPException;

public class PRMEPUBLoader {
    private String filePath;
    private String unZipPath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUnZipPath() {
        return unZipPath;
    }

    public void setUnZipPath(String unZipPath) {
        this.unZipPath = unZipPath;
    }

    // TODO 加载数据的时候，所有相对路径 需要另外对应一个绝对路径
    public static RMEPUBObject loadEpub(String epubPath, String unzipFolder)
            throws XmlPullParserException, PRMException, IOException {

        PRMEPUBContainerParser containerParser = new PRMEPUBContainerParser();
        PRMEPUBNcxParser ncxParser = new PRMEPUBNcxParser();
        PRMEPUBOpfParser opfParser = new PRMEPUBOpfParser();
        PRMEPUBMimeTypeParser mimeTypeParser = new PRMEPUBMimeTypeParser();
        File epubFile = new File(epubPath);
        if (!epubFile.exists()) {
            throw new PRMException(PRMException.ERROR_EPUB_FILE_NOT_FOUND,
                    "epub文件未找到" + String.valueOf(epubPath));
        }

        File unZipDir = new File(unzipFolder);
        if (!unZipDir.exists()) {
            // TODO 还原 没有加密
            RMZipFileUtil.unzipTo(epubPath, unzipFolder);
            // RMZipFileUtil.unzipEpubTo(epubPath, unzipFolder);
        }

        RMEPUBMimeType mimeType = mimeTypeParser.parserMimeType(unzipFolder);
        if (!mimeType.isEpub()) {
            throw new PRMException(PRMException.ERROR_FILE_FORMART);
        }
        RMEPUBObject object = new RMEPUBObject();
        object.setMimeType(mimeType);
        XmlPullParser parserContainer = Xml.newPullParser();
        RMEPUBContainer container = containerParser.parserContainer(
                unzipFolder, parserContainer);
        object.setContainer(container);
        File opfPath = new File(unzipFolder, container.getFullPath());
        // RMEPUBOPFManager opf = opfParser.parserOPF(unzipFolder, container,
        // parser);
        XmlPullParser parserOpf = Xml.newPullParser();
        RMEPUBOPFManager opf = opfParser.parserOPF(opfPath.getPath(), parserOpf);
        object.setOpfManager(opf);

        XmlPullParser parserNcx = Xml.newPullParser();
        RMEPUBNCXManager ncxManager = ncxParser.parserNCX(unzipFolder,
                container, opf, parserNcx);

        object.setNcxManager(ncxManager);
        object.setOpfFolder(opfPath.getParent());
        return object;
    }

    public static RMEPUBZipObject loadEpubRzp(String epubPath)
            throws XmlPullParserException, PRMException, IOException,
            RZPException {

        PRMEPUBContainerParser containerParser = new PRMEPUBContainerParser();
        PRMEPUBNcxParser ncxParser = new PRMEPUBNcxParser();
        PRMEPUBOpfParser opfParser = new PRMEPUBOpfParser();
        PRMEPUBMimeTypeParser mimeTypeParser = new PRMEPUBMimeTypeParser();
        File epubFile = new File(epubPath);

        if (!epubFile.exists()) {
            throw new PRMException(PRMException.ERROR_EPUB_FILE_NOT_FOUND,
                    "epub文件未找到" + String.valueOf(epubPath));
        }
        RZPFile rzpFile = new RZPFile(epubFile);

        boolean isEncry = rzpFile.isEncrypted();

        if (isEncry) {
            rzpFile.setPassword(RMReadController.GLOBAL_DATA.PASS_WORDS);
        }

        InputStream mimeTypeInput = rzpFile.getInputStream("mimetype");

        //LogUtil.e("yang", "mimeTypeInput---- " + mimeTypeInput.toString());
        if (mimeTypeInput == null) {
            throw new PRMException(PRMException.ERROR_FILE_FORMART,
                    "epub文件格式错误");
        }
        RMEPUBMimeType mimeType = mimeTypeParser.parserMimeType(mimeTypeInput);
        if (!mimeType.isEpub()) {
            throw new PRMException(PRMException.ERROR_FILE_FORMART);
        }
        RMEPUBZipObject object = new RMEPUBZipObject();
        object.setMimeType(mimeType);
        InputStream containerInput = rzpFile
                .getInputStream("META-INF/container.xml");
        if (containerInput == null) {
            throw new PRMException(PRMException.ERROR_FILE_FORMART,
                    "epub文件格式错误");
        }

        XmlPullParser parserContainer = Xml.newPullParser();
        RMEPUBContainer container = containerParser.parserContainer(
                containerInput, parserContainer);
        object.setContainer(container);
        InputStream opfInput = rzpFile.getInputStream(container.getFullPath());

        XmlPullParser parserOpf = Xml.newPullParser();
        RMEPUBOPFManager opf = opfParser.parserOPF(opfInput, parserOpf);
        object.setOpfManager(opf);

        RMEPUBOPFManifestItem item = opf.getManifest().getItem(
                opf.getSpine().getToc());

        String prefix = container.getFullPath().substring(0,
                container.getFullPath().lastIndexOf("/") + 1);

        InputStream ncxInput = rzpFile.getInputStream(prefix + item.href);

        XmlPullParser parserNcf = Xml.newPullParser();
        RMEPUBNCXManager ncxManager = ncxParser.parserNCX(ncxInput, parserNcf);

        object.setNcxManager(ncxManager);
        object.setOpfFolder(prefix);
        return object;
    }

    public static RMEPUBZipObject loadEpubZip(String epubPath)
            throws XmlPullParserException, PRMException, IOException {
        PRMEPUBContainerParser containerParser = new PRMEPUBContainerParser();
        PRMEPUBNcxParser ncxParser = new PRMEPUBNcxParser();
        PRMEPUBOpfParser opfParser = new PRMEPUBOpfParser();
        PRMEPUBMimeTypeParser mimeTypeParser = new PRMEPUBMimeTypeParser();
        File epubFile = new File(epubPath);
        if (!epubFile.exists()) {
            throw new PRMException(PRMException.ERROR_EPUB_FILE_NOT_FOUND,
                    "epub文件未找到" + String.valueOf(epubPath));
        }
        ZipFile zipFile = new ZipFile(epubFile);
        InputStream mimeTypeInput = zipFile.getInputStream(zipFile
                .getEntry("mimetype"));

        if (mimeTypeInput == null) {
            zipFile.close();
            throw new PRMException(PRMException.ERROR_FILE_FORMART,
                    "epub文件格式错误");
        }
        RMEPUBMimeType mimeType = mimeTypeParser.parserMimeType(mimeTypeInput);
        if (!mimeType.isEpub()) {
            zipFile.close();
            throw new PRMException(PRMException.ERROR_FILE_FORMART);
        }
        RMEPUBZipObject object = new RMEPUBZipObject();
        object.setMimeType(mimeType);
        InputStream containerInput = zipFile.getInputStream(zipFile
                .getEntry("META-INF/container.xml"));
        if (containerInput == null) {
            zipFile.close();
            throw new PRMException(PRMException.ERROR_FILE_FORMART,
                    "epub文件格式错误");
        }

        XmlPullParser parserContainer = Xml.newPullParser();
        RMEPUBContainer container = containerParser.parserContainer(
                containerInput, parserContainer);
        object.setContainer(container);
        InputStream opfInput = zipFile.getInputStream(zipFile
                .getEntry(container.getFullPath()));

        XmlPullParser parserOpf = Xml.newPullParser();
        RMEPUBOPFManager opf = opfParser.parserOPF(opfInput, parserOpf);
        object.setOpfManager(opf);
        RMEPUBOPFManifestItem item = opf.getManifest().getItem(
                opf.getSpine().getToc());
        String prefix = container.getFullPath().substring(0,
                container.getFullPath().lastIndexOf("/") + 1);
        InputStream ncxInput = zipFile.getInputStream(zipFile.getEntry(prefix
                + item.href));

        XmlPullParser parserNcf = Xml.newPullParser();
        RMEPUBNCXManager ncxManager = ncxParser.parserNCX(ncxInput, parserNcf);
        object.setNcxManager(ncxManager);
        object.setOpfFolder(prefix);
        zipFile.close();
        return object;
    }

    public static RMTXTObject loadTXT(String epubPath, String unzipFolder)
            throws PRMException, IOException {
        RMTXTObject object = new RMTXTObject();
        File srcFile = new File(epubPath);
        File f = new File(unzipFolder);
        if (!f.exists()) {
            f.mkdirs();
        }
        File unFile = new File(unzipFolder, RMTXTObject.DEFAULT_HTML_NAME);
        if (!srcFile.exists()) {
            throw new PRMException(PRMException.ERROR_TXT_FILE_NOT_FOUND,
                    "txt文件未找到:" + unFile.getPath());
        }
        String encode = "utf-8";
        if (!unFile.exists()) {
            encode = TXTFileConverter.toOneHtml(epubPath, unFile.getPath());
        } else {
            encode = RMEncode.getTxtEncode(epubPath);
        }
        object.setEncode(encode);

        object.setWorkPath(unzipFolder);
        return object;
    }
}

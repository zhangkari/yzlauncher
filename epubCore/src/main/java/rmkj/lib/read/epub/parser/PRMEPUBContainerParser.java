package rmkj.lib.read.epub.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import rmkj.lib.exception.PRMException;
import rmkj.lib.read.epub.entity.RMEPUBContainer;
import rmkj.lib.read.util.RMUnicodeInputStream;

/**
 * 解析 container.xml文件
 *
 * @author zsx
 */
public class PRMEPUBContainerParser {
    public RMEPUBContainer parserContainer(String dirPath, XmlPullParser parser) throws XmlPullParserException, IOException, PRMException {
        File containerFile = new File(dirPath + File.separator + "META-INF", "container.xml");
        if (containerFile.exists() && containerFile.isFile()) {
            InputStream in = new FileInputStream(containerFile);
            RMEPUBContainer container = parserContainer(in, parser);
            return container;
        } else {
            throw new PRMException(PRMException.ERROR_UNZIP_FILE_NOT_FOUND, "container.xml:" + containerFile.getPath());
        }
    }

    public RMEPUBContainer parserContainer(InputStream in, XmlPullParser parser) throws XmlPullParserException, IOException, PRMException {
        RMEPUBContainer container = new RMEPUBContainer();
        parser.setInput(new RMUnicodeInputStream(in, "UTF-8"), "UTF-8");
        int event;
        while ((event = parser.getEventType()) != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:// 判断当前事件是否是文档开始事件
                    break;
                case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
                    if ("rootfile".equals(parser.getName())) {// 判断开始标签元素是否是book
                        if ("rootfile".equals(parser.getName())) {//
                            container.setFullPath(parser.getAttributeValue(null, "full-path"));
                            container.setMediaType(parser.getAttributeValue(null, "media-type"));
                            return container;
                        }
                    }
                    break;
            }
            parser.next();// 进入下一个元素并触发相应事件
        }
        if (container.getFullPath() == null) {
            throw new PRMException(PRMException.ERROR_PARSER, "container.fullPath = null");
        }
        return container;
    }
}

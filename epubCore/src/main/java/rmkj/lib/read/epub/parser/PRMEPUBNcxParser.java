package rmkj.lib.read.epub.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import rmkj.lib.exception.PRMException;
import rmkj.lib.read.epub.entity.RMEPUBContainer;
import rmkj.lib.read.epub.entity.RMEPUBNCXDocTitle;
import rmkj.lib.read.epub.entity.RMEPUBNCXHead;
import rmkj.lib.read.epub.entity.RMEPUBNCXManager;
import rmkj.lib.read.epub.entity.RMEPUBNCXNavMap;
import rmkj.lib.read.epub.entity.RMEPUBNCXNavPoint;
import rmkj.lib.read.epub.entity.RMEPUBOPFManager;
import rmkj.lib.read.epub.entity.RMEPUBOPFManifestItem;
import rmkj.lib.read.util.RMUnicodeInputStream;
import rmkj.lib.read.util.RMUtilFileStream;

/**
 * 解析NCX文件
 *
 * @author zsx
 */
public class PRMEPUBNcxParser {
    public RMEPUBNCXManager parserNCX(String dirPath, RMEPUBContainer container, RMEPUBOPFManager opf, XmlPullParser parser) throws IOException,
            XmlPullParserException, PRMException {
        File opfFile = new File(dirPath + File.separator + container.getFullPath());
        RMEPUBOPFManifestItem item = opf.getManifest().getItem(opf.getSpine().getToc());
        if (item == null) {
            return null;
        }
        String ncxPath = opfFile.getParent() + File.separator + item.href;
        return parserNCX(ncxPath, opf, parser);
    }

    public RMEPUBNCXManager parserNCX(String ncxPath, RMEPUBOPFManager opf, XmlPullParser parser) throws IOException, XmlPullParserException, PRMException {
        File ncxFile = new File(ncxPath);
        if (!ncxFile.exists()) {
            throw new PRMException(PRMException.ERROR_UNZIP_FILE_NOT_FOUND, "ncx:" + ncxPath);
        }
        if (ncxFile.isDirectory()) {
            throw new PRMException(PRMException.ERROR_FILE_FORMART, "ncx is directory :" + ncxFile.getPath());
        }
        InputStream in = RMUtilFileStream.clearFileBom(ncxFile);
        return parserNCX(in, parser);
    }

    public RMEPUBNCXManager parserNCX(InputStream in, XmlPullParser parser) throws IOException, XmlPullParserException, PRMException {
        RMEPUBNCXManager ncx = null;
        parser.setInput(new RMUnicodeInputStream(in, "UTF-8"), "UTF-8");

        boolean isHead = false;
        boolean isDocTitle = false;
        boolean isNav = false;
        List<RMEPUBNCXNavPoint> list = new ArrayList<RMEPUBNCXNavPoint>();
        int index = -1;// TODO 有问题
        int level = 0;

        int type = parser.getEventType();

        while (type != XmlPullParser.END_DOCUMENT) {

            switch (type) {
                case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
                    if (isHead) {
                        if ("meta".equals(parser.getName())) {
                            String name = parser.getAttributeValue(null, "name");
                            String content = parser.getAttributeValue(null, "content");
                            if ("dtb:uid".equals(name)) {
                                ncx.getHead().setUid(content);
                                break;
                            }
                            if ("dtb:depth".equals(name)) {
                                ncx.getHead().setDepth(content);
                                break;
                            }
                            if ("dtb:totalPageCount".equals(name)) {
                                ncx.getHead().setTotalPageCount(content);
                                break;
                            }
                            if ("maxPageNumber".equals(name)) {
                                ncx.getHead().setMaxPageNumber(content);
                                break;
                            }
                            break;
                        }
                    }
                    if (isDocTitle) {
                        if ("text".equals(parser.getName())) {
                            ncx.getDocTitle().setTitle(parser.nextText());
                            break;
                        }
                    }
                    if (isNav) {
                        if ("navPoint".equals(parser.getName())) {
                            index++;
                            level++;
                            RMEPUBNCXNavPoint point = new RMEPUBNCXNavPoint();
                            point.setId(parser.getAttributeValue(null, "id"));
                            point.setPlayOrder(parser.getAttributeValue(null, "playOrder"));
                            point.setLevel(level);
                            list.add(point);
                            break;
                        }

                        if ("content".equals(parser.getName())) {
                            list.get(index).setSrc(parser.getAttributeValue(null, "src"));
                            break;
                        }
                        if ("text".equals(parser.getName())) {
                            list.get(index).setText(parser.nextText());
                            break;
                        }

                    }
                    if ("head".equals(parser.getName())) {
                        ncx.setHead(new RMEPUBNCXHead());
                        isHead = true;
                        break;
                    }
                    if ("docTitle".equals(parser.getName())) {
                        ncx.setDocTitle(new RMEPUBNCXDocTitle());
                        isDocTitle = true;
                        break;
                    }
                    if ("navMap".equals(parser.getName())) {
                        ncx.setNav(new RMEPUBNCXNavMap());
                        isNav = true;
                        break;
                    }
                    if ("ncx".equals(parser.getName())) {
                        ncx = new RMEPUBNCXManager();
                        break;
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("head".equals(parser.getName())) {
                        isHead = false;
                        break;
                    }
                    if ("docTitle".equals(parser.getName())) {
                        isDocTitle = false;
                        break;
                    }
                    if ("navMap".equals(parser.getName())) {
                        isNav = false;
                        break;
                    }
                    if (isNav) {
                        if ("navPoint".equals(parser.getName())) {
                            level--;
                            break;
                        }
                    }

                    break;

            }

            //把指针移动到下一个节点，并返回该节点的事件类型
            type = parser.next();
        }

        ncx.getNav().setNavPoint(list);
        return ncx;
    }

}

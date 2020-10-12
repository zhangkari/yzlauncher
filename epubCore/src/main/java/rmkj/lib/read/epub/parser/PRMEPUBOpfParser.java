package rmkj.lib.read.epub.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import rmkj.lib.exception.PRMException;
import rmkj.lib.read.epub.entity.RMEPUBContainer;
import rmkj.lib.read.epub.entity.RMEPUBOPFGuide;
import rmkj.lib.read.epub.entity.RMEPUBOPFManager;
import rmkj.lib.read.epub.entity.RMEPUBOPFManifest;
import rmkj.lib.read.epub.entity.RMEPUBOPFMeta;
import rmkj.lib.read.epub.entity.RMEPUBOPFSpine;
import rmkj.lib.read.epub.entity.RMEPUBOPFSpineItem;
import rmkj.lib.read.util.LogUtil;
import rmkj.lib.read.util.RMUnicodeInputStream;
import rmkj.lib.read.util.RMUtilFileStream;

/**
 * 解析opf文件
 * 
 * @author zsx
 * 
 */
public class PRMEPUBOpfParser {
	public RMEPUBOPFManager parserOPF(String dirPath, RMEPUBContainer container, XmlPullParser parser) throws XmlPullParserException, IOException, PRMException {
		String opfPath = dirPath + File.separator + container.getFullPath();
		return parserOPF(opfPath, parser);
	}

	public RMEPUBOPFManager parserOPF(String opfPath, XmlPullParser parser) throws XmlPullParserException, IOException, PRMException {
		File opfFile = new File(opfPath);
		if (!opfFile.exists()) {
			throw new PRMException(PRMException.ERROR_UNZIP_FILE_NOT_FOUND, "opf:" + opfPath);
		}
		if (opfFile.isDirectory()) {
			throw new PRMException(PRMException.ERROR_UNZIP_FILE_NOT_FOUND, "opf is directory" + opfFile.getPath());
		}
		InputStream in = RMUtilFileStream.clearFileBom(opfFile);
		return parserOPF(in, parser);
	}

	public RMEPUBOPFManager parserOPF(InputStream in, XmlPullParser parser) throws XmlPullParserException, IOException, PRMException {
		RMEPUBOPFManager opf = new RMEPUBOPFManager();
		parser.setInput(new RMUnicodeInputStream(in, "UTF-8"), "UTF-8");
		int event;
		RMEPUBOPFGuide guide = null;
		boolean isGuide = false;
		RMEPUBOPFManifest manifest = null;
		boolean isManifest = false;
		RMEPUBOPFMeta metadata = null;
		boolean isMetadata = false;
		RMEPUBOPFSpine spine = null;
		boolean isSpine = false;
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
		while ((event = parser.getEventType()) != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
				if (isMetadata) {
					parserOPFMetadata(parser, metadata);
					break;
				}
				if (isManifest) {
					parserOPFManifest(parser, manifest);
					break;
				}
				if (isGuide) {
					parserOPFGuide(parser, guide);
					break;
				}
				if (isSpine) {
					parserOPFSpine(parser, spine);
					break;
				}
				if ("metadata".equals(parser.getName())) {
					isMetadata = true;
					metadata = new RMEPUBOPFMeta();
					opf.setMetadata(metadata);
				}
				if ("manifest".equals(parser.getName())) {
					isManifest = true;
					manifest = new RMEPUBOPFManifest();
					opf.setManifest(manifest);
				}
				if ("spine".equals(parser.getName())) {
					isSpine = true;
					spine = new RMEPUBOPFSpine();
					spine.setToc(parser.getAttributeValue(null, "toc"));
					spine.setPage_progression_direction(parser.getAttributeValue(null, "page-progression-direction"));
					String orientation = parser.getAttributeValue(null, "orientation");
					if (orientation != null) {
						spine.setOrientation(orientation);
					}
					opf.setSpine(spine);
				}
				if ("guide".equals(parser.getName())) {
					isGuide = true;
					guide = new RMEPUBOPFGuide();
					opf.setGuide(guide);
				}
				break;
			case XmlPullParser.END_TAG:
				if ("metadata".equals(parser.getName())) {
					isMetadata = false;
				}
				if ("manifest".equals(parser.getName())) {
					isManifest = false;
				}
				if ("spine".equals(parser.getName())) {
					isSpine = false;
				}
				if ("guide".equals(parser.getName())) {
					isGuide = false;
				}
				break;
			}
			parser.next();// 进入下一个元素并触发相应事件
		}
		return opf;
	}

	private void parserOPFGuide(XmlPullParser parser, RMEPUBOPFGuide guide) {
		if ("reference".equals(parser.getName())) {
			String type = parser.getAttributeValue(null, "type");
			String title = parser.getAttributeValue(null, "title");
			String href = parser.getAttributeValue(null, "href");
			guide.addGuideItem(type, title, href);
		}
	}

	private void parserOPFSpine(XmlPullParser parser, RMEPUBOPFSpine spine) {
		if ("itemref".equals(parser.getName())) {
			String idref = parser.getAttributeValue(null, "idref");
			RMEPUBOPFSpineItem item = new RMEPUBOPFSpineItem();
			item.setItemref(idref);
			String orientation = parser.getAttributeValue(null, "orientation");
			if (orientation != null) {
				item.setOrientation(orientation);
			}
			String property = parser.getAttributeValue(null,"properties");
			if(property !=null){
				item.setProperty(property);
			}
			spine.addItemref(item);
		}
	}

	private void parserOPFManifest(XmlPullParser parser, RMEPUBOPFManifest manifest) {
		if ("item".equals(parser.getName())) {
			String id = parser.getAttributeValue(null, "id");
			String href = parser.getAttributeValue(null, "href");
			String media_type = parser.getAttributeValue(null, "media-type");
			manifest.addItem(id, href, media_type);
		}
	}

	private void parserOPFMetadata(XmlPullParser parser, RMEPUBOPFMeta metadata) throws XmlPullParserException, IOException {
		if ("title".equals(parser.getName())) {
			metadata.setTitle(parser.nextText());
			return;
		}
		if ("creator".equals(parser.getName())) {
			metadata.setCreator(parser.nextText());
			return;
		}
		if ("language".equals(parser.getName())) {
			metadata.setLanguage(parser.nextText());
			return;
		}
		if ("identifier".equals(parser.getName())) {
			metadata.setIdentifier(parser.nextText());
			return;
		}
		if ("publisher".equals(parser.getName())) {
			metadata.setPublisher(parser.nextText());
			return;
		}
		if ("date".equals(parser.getName())) {
			metadata.setDate(parser.nextText());
			return;
		}

		if ("subject".equals(parser.getName())) {
			metadata.setSubject(parser.nextText());
			return;
		}
		if ("description".equals(parser.getName())) {
			metadata.setDescription(parser.nextText());
			return;
		}
		if ("contributor".equals(parser.getName())) {
			metadata.setContributor(parser.nextText());
			return;
		}
		if ("type".equals(parser.getName())) {
			metadata.setType(parser.nextText());
			return;
		}
		if ("format".equals(parser.getName())) {
			metadata.setFormat(parser.nextText());
			return;
		}
		if ("source".equals(parser.getName())) {
			metadata.setSource(parser.nextText());
			return;
		}
		if ("relation".equals(parser.getName())) {
			metadata.setRelation(parser.nextText());
			return;
		}
		if ("coverage".equals(parser.getName())) {
			metadata.setCoverage(parser.nextText());
			return;
		}
		if ("rights".equals(parser.getName())) {
			metadata.setRights(parser.nextText());
			return;
		}
		String key = parser.getAttributeValue(null, "name");
		String value = parser.getAttributeValue(null, "content");
		if (key == null) {
			if (LogUtil.DEBUG) {
				LogUtil.e("RMEPUBOpfParser",
						"meta 没有添加成功: name:" + String.valueOf(parser.getName()) + ",attrCount:" + String.valueOf(parser.getAttributeCount()));
			}
			return;
		}
		metadata.addMeta(key, value);
	}
}

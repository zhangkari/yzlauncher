package rmkj.lib.read.js;

import rmkj.lib.read.util.LogUtil;

public class PRMHtmlDoctype {

    /**
     * http://www.w3school.com.cn/tags/tag_doctype.asp
     */

    public static final String DOCTYPE_HTML5 = "<!DOCTYPE html>";
    public static final String DOCTYPE_HTML401_STRICT = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n  \"http://www.w3.org/TR/html4/strict.dtd\">";
    public static final String DOCTYPE_HTML401_TRANSITIONAL = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n  \"http://www.w3.org/TR/html4/loose.dtd\">";
    public static final String DOCTYPE_HTML401_FRAMESET = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\"\n  \"http://www.w3.org/TR/html4/frameset.dtd\">";


    public static final String DOCTYPE_XHTML11 = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"\n  \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">";
    public static final String DOCTYPE_XHTML10_STRICT = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">";
    public static final String DOCTYPE_XHTML10_TRANSITIONAL = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\"\n  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
    public static final String DOCTYPE_XHTML10_FRAMESET = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\"\n  \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">";


    public static final String DOCHEADER_XML = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>";

    public static final String HTML_XML_ATTR = "html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"zh-CN\" xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"";
    public static final String HTML_NO_ATTR = "html";


    public static String changeXhtmlToHtml(String html) {


        String ret = setDoctypeTypeHtml5(html);

        ret = deleteXmlAttr(ret);


        return ret;
    }

    private static String setDoctypeTypeHtml5(String html) {

        //LogUtil.e("yang", "setDoctypeTypeHtml5 " + html);

        String ret = null;

        ret = html.replaceAll(DOCTYPE_XHTML11, DOCTYPE_HTML5);
        ret = ret.replaceAll(DOCTYPE_XHTML10_STRICT, DOCTYPE_HTML5);
        ret = ret.replaceAll(DOCTYPE_XHTML10_TRANSITIONAL, DOCTYPE_HTML5);
        ret = ret.replaceAll(DOCTYPE_XHTML10_FRAMESET, DOCTYPE_HTML5);

//        ret = html.replaceAll("<!DOCTYPE (.+?)>", DOCTYPE_HTML5);

        return ret;
    }

    private static String deleteXmlAttr(String html) {
        String ret = null;
        ret = html.replace(DOCHEADER_XML, "\n");

        ret = ret.replace(HTML_XML_ATTR, HTML_NO_ATTR);
        return ret;
    }
}

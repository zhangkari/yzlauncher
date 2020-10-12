package rmkj.lib.read.js;

import android.annotation.SuppressLint;
import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import rmkj.lib.read.db.RMReadingManager;
import rmkj.lib.read.db.RMReadingNote;
import rmkj.lib.read.epub.entity.RMEPUBSpan;
import rmkj.lib.read.global.PRMWebHtmlParam;
import rmkj.lib.read.global.PRMWebSetting;
import rmkj.lib.read.util.LogUtil;

@SuppressLint("DefaultLocale")
public class PRMWebHtml {
    private String meta;
    private PRMJarResource jarJS;
    public static String LINE_SPAN_CLASS_NAME = "bottom_line";
    public String noteImagePath = "file:///android_asset/note.png";
    public static String SEARCH_CSS_CLASS_NAME = "search_highlight";

    public PRMWebHtml() {
        meta = getViewportMeta();
        jarJS = new PRMJarResource();
    }

    public void setNoteImagePath(String noteImagepath) {
        this.noteImagePath = noteImagepath;
    }

    public String replaceSpan(String html, int spineIndex,
                              RMReadingManager dbManager) {
        List<RMReadingNote> notes = dbManager.getNotes(spineIndex);
        if (notes != null) {
            for (int i = 0; i < notes.size(); i++) {

                RMReadingNote note = notes.get(i);
                try {
                    List<RMEPUBSpan> list = note.toSpans();
                    if (list != null) {
                        for (RMEPUBSpan span : list) {

                            String spanTag = null;
                            spanTag = String
                                    .format("<span id=\"%s\" class=\"%s\" name=\"%s\">%s</span>",
                                            note.noteID, "hightlight",
                                            note.noteID, span.srcSelection);

                            if (span.isEnd) {
                                String endTag = String
                                        .format("<img id=\"%s\" name=\"%s\" class=\"%s\" src=\"%s\"\\>",
                                                note.noteID, note.noteID,
                                                "note_tag", noteImagePath);
                                spanTag += endTag;
                            }
                            // 处理特殊img标签
                            // span.srcHtml = span.srcHtml.replaceAll("\">",
                            // "\" />");

                            //开始替换
                            span.replaceHtml = span.srcHtml.replace(
                                    span.srcSelection, spanTag);

                            html = html.replaceAll(span.srcHtml,
                                    span.replaceHtml);
                        }
                    }
                } catch (JSONException e) {
                    if (LogUtil.DEBUG) {
                        LogUtil.e(this, "解析错误:" + note.jsonArray);
                    }
                    e.printStackTrace();
                } catch (PatternSyntaxException e) {
                    if (LogUtil.DEBUG) {
                        LogUtil.e(e);
                    }
                }
            }
        }
        return html;
    }

    public String getSpineHtmlData(String html, String head,
                                   PRMWebHtmlParam param, PRMWebSetting setting, Context context) {
        if (head == null) {
            head = "";
        }
        String srcHtml = deleteInvalidNode(html);

        StringBuilder headSB = new StringBuilder();
        headSB.append(meta);
        headSB.append(getExternalCss(param.width, param.height, param.margin,
                setting, context));
        headSB.append(getExternalJS(param));
        headSB.append(head);

        int pos = srcHtml.indexOf("</head>");
        StringBuffer sb = new StringBuffer();
        if (pos != -1) {
            sb.append(srcHtml.substring(0, pos));
            sb.append("\n");
            sb.append(headSB.toString());
            sb.append("\n");
            sb.append(srcHtml.substring(pos));
        } else {
            pos = srcHtml.indexOf("<body");
            if (pos != -1) {
                sb.append("<head>");
                sb.append("\n");
                sb.append(headSB.toString());

                sb.append("</head>\n");
                sb.append(srcHtml.substring(pos));
            } else {
                sb.append("<head>");
                sb.append("\n");
                sb.append(headSB.toString());
                sb.append("</head>\n");
                sb.append("<body>");
                sb.append(srcHtml);
                sb.append("</body>");
            }
        }
        return sb.toString();
    }

    public String replaceKeyword(String html, String keyword) {
        return html.replaceAll(keyword, "<font color='red'>" + keyword
                + "</font>");
    }

    /**
     * 拿到meta
     */
    private String getViewportMeta() {
        return "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0\"/>";
    }

    private String getExternalCss(int width, int height, int margin,
                                  PRMWebSetting setting, Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("<style type='text/css'>");
        String htmlStyle = String
                .format("html { text-align:justify; width : %dpx; height: %dpx; padding: %dpx; -webkit-column-gap: %dpx; -webkit-column-width: %dpx; }\n",
                        width - 2 * margin, height - 2 * margin, margin,
                        2 * margin, width - 2 * margin);
        // TODO
        // htmlStyle = String.format("html { text-align:justify; " +
        // "width: %dpx; height: %dpx;" +
        // "padding-top: %dpx;padding-bottom: %dpx;padding-left: %dpx;padding-right: %dpx; "
        // "-webkit-column-gap: %dpx; -webkit-column-width: %dpx; }\n",
        // width-2*margin,height-2*margin,
        // margin,margin,margin,margin,
        // 2*margin,width-2*margin);

        // 新增text-align:justify;
        sb.append(htmlStyle);
        String imgStyle = " img {" + " margin-left: auto;"
                + " margin-right: auto;" + " max-height: 100% !important;"
                + " max-width: 65% !important;" + " height : auto !important;"
                + "}\n";

        if (LogUtil.isPort(context)) {
            imgStyle = " img {" + " margin-left: auto;"
                    + " margin-right: auto;" + " max-height: 100% !important;"
                    + " max-width: 90% !important;" + " height : auto !important;"
                    + "}\n";
        }

        sb.append(imgStyle);
        // sb.append(".hightlight{background-color: rgb(99,176,182);}");// TODO
        // 新增
        sb.append(".hightlight{border-bottom:2px solid #FF2E7E;}");// TODO 新增
        String bottomLine = ".bottom_line{border-bottom:2px solid #FF2E7E;}\n";
        sb.append(bottomLine);
        String search_highlight = ".search_highlight {background-color: rgba(96, 199, 0, 0.3) !important;color: darkred;}";
        sb.append(search_highlight);
        String noteTag = ".note_tag{width:16px;height:16px;vertical-align:middle;}\n";
        sb.append(noteTag);
        String theme = this.themedCss(setting.getFontSize(),
                setting.getLineSpace(), setting.getFontColor());
        sb.append(theme);
        sb.append("</style>");
        sb.append("\n");
        return sb.toString();
    }

    private String themedCss(int fontSize, int lineSpacing, String fontColor) {
        // background:%s;, backgroundColor
        String body = String.format("body { font-size:%d%%;color:%s;}",
                fontSize, fontColor);
        String p = String
                .format("p {line-height:%d%%;} span {line-height:%d%%;} div {line-height:%d%%;}",
                        lineSpacing, lineSpacing, lineSpacing);
        String bodyCss = body + p;
        return bodyCss;
    }

    private String getExternalJS(PRMWebHtmlParam param) {
        StringBuilder js = new StringBuilder();

        try {
            // if (LogUtil.DEBUG) {
            // js.append("<script type=\"text/javascript\">");
            // /* debug */
            // js.append(jarJS.getDebugJS());
            // js.append("\n");
            // js.append("</script>");
            // }
            js.append("<script type=\"text/javascript\">");
            String jqueryJS = jarJS.getJqueryJS();
            if (jqueryJS != null) {
                js.append(jqueryJS);
                js.append("\n");
            }

            String searchJS = jarJS.getSearchJS();
            if (searchJS != null) {
                js.append(searchJS);
                js.append("\n");
            }

            String nativeJS = jarJS.getNativeJS();
            if (nativeJS != null) {
                js.append(nativeJS);
                js.append("\n");
            }
            String epubJS = jarJS.getEpubJS();
            if (epubJS != null) {
                js.append(epubJS);
                js.append("\n");
            }
            String clientJS = jarJS.getClientJS();
            if (clientJS != null) {
                js.append(clientJS);
                js.append("\n");
            }

            js.append("function init(){");
            js.append("\n");
            js.append(" initEPUB(" + param.width + "," + param.height
                    + ",\"android\");");
            js.append("epub.setHighlightClass('hightlight');");
            if (param.isVertical) {
                js.append("epub.setOrientation(1);");
            } else {
                js.append("epub.setOrientation(0);");
            }
            js.append("\n");
            js.append("}");
            js.append("function start(){");
            if (param.loadFromEnd) {
                js.append("epub.setStartFromEnd();");
            } else if (param.loadFromPage) {
                js.append("epub.setStartPage(" + param.loadPage + ");");
            } else if (param.loadFromPercent) {
                js.append("epub.setStartPercent(" + param.loadPercent + ");");
            } else if (param.loadFromAnchor) {
                js.append("epub.setStartAnchor('" + param.loadAnchor + "');");
            } else if (param.loadFormPageAndTotalPage) {
                js.append("epub.setStartFromTotalAndCurrent("
                        + param.loadTotalPage + "," + param.loadPage + ");");
            }
            js.append("\n");
            js.append("}");
        } catch (IOException e) {
            if (LogUtil.DEBUG) {
                LogUtil.e(this, "读取jar错误");
            }
            e.printStackTrace();
        } finally {
            js.append("</script>");
        }
        return js.toString();
    }

    private final Pattern INVALID_CHAR_PATTERN = Pattern.compile("<title />");

    private String deleteInvalidNode(String html) {
        String ret;
        try {
            ret = INVALID_CHAR_PATTERN.matcher(html).replaceAll("");
        } catch (PatternSyntaxException e) {
            LogUtil.e(e);
            return html;
        }
        return ret;
    }

}

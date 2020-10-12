package rmkj.lib.read.search;

import java.io.Serializable;

import android.text.Html;
import android.text.Spanned;

public class RMSearchSesultItem implements Serializable {
	private static final long serialVersionUID = 6816774553708156145L;
	// 上下文
	public String context = "";
	public float percent;
	public int keywordIndex;
	public String keyword = "";
//	public int index;
	public int spineIndex;
	public int indexInSpine;
	public Spanned getContext() {
		if (context == null)
			return Html.fromHtml("");
		String highlightContext = context.replace(keyword, "<font color='red'>" + keyword + "</font>");
		return Html.fromHtml(highlightContext);
	}
}

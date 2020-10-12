package rmkj.lib.read.js.entity;

import java.io.Serializable;


public class HighlightSelectionText implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7396128515200363538L;
	private String selectionReplaceArray;
	private String spanID;
	private String selectText;

	public HighlightSelectionText (String selectionReplaceArray, String spanID,
			String selectText) {
		this.setSelectionReplaceArray(selectionReplaceArray);
		this.setSpanID(spanID);
		this.setSelectText(selectText);
	}

	public String getSelectionReplaceArray() {
		return selectionReplaceArray;
	}

	public void setSelectionReplaceArray(String selectionReplaceArray) {
		this.selectionReplaceArray = selectionReplaceArray;
		
	}

	public String getSpanID() {
		return spanID;
	}

	public void setSpanID(String spanID) {
		this.spanID = spanID;
	}

	public String getSelectText() {
		return selectText;
	}

	public void setSelectText(String selectText) {
		this.selectText = selectText;
	}
}
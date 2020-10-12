package rmkj.lib.read.global;

public class PRMWebSetting {

	public static final int SETTING_LINE_SPACE_LARGE = 140;
	public static final int SETTING_LINE_SPACE_MILLDE = 115;
	public static final int SETTING_LINE_SPACE_SMALL = 110;
	public static final int SETTING_FONT_LARGE = 150;
	public static final int SETTING_FONT_DEFAULT = 100;
	public static final int SETTING_FONT_SMALL = 70;
	private int fontSize = SETTING_FONT_DEFAULT;
	private int lineSpace = SETTING_LINE_SPACE_MILLDE;
	private String fontColor;
	private String backgroundColor;

	public PRMWebSetting() {
		this.reset();
	}

	public void reset() {
		this.fontSize = SETTING_FONT_DEFAULT;
		this.lineSpace = SETTING_LINE_SPACE_MILLDE;
		this.fontColor = "#000000";
		this.backgroundColor = "#ffffff";
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public int getLineSpace() {
		return lineSpace;
	}

	public void setLineSpace(int lineSpace) {
		this.lineSpace = lineSpace;
	}

	public String getFontColor() {
		return fontColor;
	}

	public void setFontColor(String fontColor) {
		this.fontColor = fontColor;
	}
}

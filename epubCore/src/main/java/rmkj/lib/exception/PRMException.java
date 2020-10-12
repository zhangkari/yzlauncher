package rmkj.lib.exception;

/**
 * 自定义异常
 * @author zsx
 */
public class PRMException extends Exception {
	private static final long serialVersionUID = -3305431690376348636L;
	private String messageInfo = "";
	public static final String ERROR_FILE_FORMART = "文件格式错误";
	public static final String ERROR_EPUB_FILE_NOT_FOUND = "epub文件未找到";
	public static final String ERROR_TXT_FILE_NOT_FOUND = "txt文件未找到";
	public static final String ERROR_UNZIP_FILE_NOT_FOUND = "epub所需格式文件未找到";
	public static final String ERROR_PARSER = "解析错误";

	public PRMException(String message) {
		super(message);
	}

	public PRMException(String message, String messageInfo) {
		super(message);
		this.messageInfo = messageInfo;
	}

	public String getMessageInfo() {
		return messageInfo;
	}

}

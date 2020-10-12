package rmkj.lib.read.widget;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import rmkj.lib.read.RMReadController;
import rmkj.lib.read.epub.entity.RMEPUBResourceProvider;
import rmkj.lib.read.global.RMFileType;
import rmkj.lib.read.util.LogUtil;
import rmkj.lib.read.widget.RMZoomImageView.OnDoNothingListener;
import rmkj.lib.rzp.exception.RZPException;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;

public class RMZoomImageViewActivity extends Activity implements OnDoNothingListener {
	public static final String EXTRA_IMAGE_URL_KEY = "image_path";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		final RMZoomImageView imageView = new RMZoomImageView(this);
		setContentView(imageView);
		imageView.setOnDoNothingListener(this);
		String str = getIntent().getStringExtra(EXTRA_IMAGE_URL_KEY);
		LogUtil.e(this, "");
		if (str == null) {
			LogUtil.e(this, "must Intent.putExtra(RMZoomImageViewActivity.EXTRA_IMAGE_URL_KEY："+str);
			return;
		}
		if (LogUtil.DEBUG) {
			LogUtil.e(this, str);
		}
		RMFileType type = RMReadController.GLOBAL_DATA.FILE_TYPE;
		InputStream is;
		try {
			switch (type) {
			case EPUB_ZIP:
			case EPUB_RZP:
				RMEPUBResourceProvider provider = new RMEPUBResourceProvider();
				provider.setFileType(type);
				is = provider.getSpineContent(RMReadController.GLOBAL_DATA.object.getOpfFolder() + str.replace("file:///", ""));
				imageView.setImageDrawable(Drawable.createFromStream(is, ""));
				System.gc();
				break;
			case EPUB:
			case TXT:
				File f = new File(str.replace("file://", ""));
				imageView.setImageDrawable(Drawable.createFromPath(f.getPath()));
				System.gc();
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (RZPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doNothing(RMZoomImageView sender) {
		this.finish();
	}
}

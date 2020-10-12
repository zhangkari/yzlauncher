package rmkj.lib.read.widget;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * 
 * @author Vken.Chen
 * 
 */
public class RMZoomImageView extends ImageView {

	private PointF startPoint = new PointF();
	private Matrix matrix = new Matrix();
	private Matrix currentMaritx = new Matrix();

	private int mode = 0; // 用于标记模式
	private static final int DRAG = 1;// 拖动
	private static final int CLICK = 0;
	private static final int ZOOM = 2;// 放大
	private float startDis = 0;
	private PointF midPoint;// 中心点

	private float maxScale;
	private float minScale;
	
	public interface OnDoNothingListener{
		public void doNothing(RMZoomImageView sender);
	}
	
	private OnDoNothingListener onDoNothingListener;
	public void setOnDoNothingListener(OnDoNothingListener listener)
	{
		onDoNothingListener = listener;
	}
	

	/**
	 * 默认构造函数
	 * 
	 * @param context
	 */
	public RMZoomImageView(Context context) {
		super(context);
		init();
	}

	/**
	 * 该构造方法在静态引入XML文件中是必须的
	 * 
	 * @param context
	 * @param paramAttributeSet
	 */
	public RMZoomImageView(Context context, AttributeSet paramAttributeSet) {
		super(context, paramAttributeSet);
		init();
	}

	DisplayMetrics dm;

	public void init() {
		setAdjustViewBounds(true);
		dm = new DisplayMetrics();
		WindowManager manager = (WindowManager) getContext().getSystemService(Service.WINDOW_SERVICE);
		manager.getDefaultDisplay().getMetrics(dm);
		setScaleType(ScaleType.MATRIX);
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onGlobalLayout() {
				if (getDrawable() != null) {
					int drawableWidth = getDrawable().getIntrinsicWidth();
					float scale = 1f * dm.widthPixels / drawableWidth;
					maxScale = 3 * scale;
					minScale = 1f;
					matrix.setScale(scale, scale); // 开始先缩小
					center(true, true);
					setImageMatrix(matrix);
					getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			}
		});
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mode = DRAG;
			currentMaritx.set(this.getImageMatrix());// 记录ImageView当期的移动位置
			startPoint.set(event.getX(), event.getY());// 开始点
			break;

		case MotionEvent.ACTION_MOVE:// 移动事件
			if (mode == DRAG) {// 图片拖动事件
				float dx = event.getX() - startPoint.x;// x轴移动距离
				float dy = event.getY() - startPoint.y;
				matrix.set(currentMaritx);// 在当前的位置基础上移动
				matrix.postTranslate(dx, dy);
			} else if (mode == ZOOM) {// 图片放大事件
				float endDis = distance(event);// 结束距离
				if (endDis > 10f) {
					float scale = endDis / startDis;// 放大倍数
					matrix.set(currentMaritx);
					matrix.postScale(scale, scale, midPoint.x, midPoint.y);
				}
				CheckScale();
				center(true, true);
			}

			break;

		case MotionEvent.ACTION_UP:
			if(startPoint.equals(event.getX(), event.getY()))
			{
				mode = 0;
				//点击了原位置，没有进行放大或者缩小
				if(onDoNothingListener!=null)
				{
					this.onDoNothingListener.doNothing(this);
				}
			}
			break;
		// 有手指离开屏幕，但屏幕还有触点(手指)
		case MotionEvent.ACTION_POINTER_UP:
			mode = 0;
			break;
		// 当屏幕上已经有触点（手指）,再有一个手指压下屏幕
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = ZOOM;
			startDis = distance(event);
			if (startDis > 10f) {// 避免手指上有两个茧
				midPoint = mid(event);
				currentMaritx.set(this.getImageMatrix());// 记录当前的缩放倍数
			}
			break;

		}
		this.setImageMatrix(matrix);
		return true;
	}

	/**
	 * 两点之间的距离
	 * 
	 * @param event
	 * @return
	 */
	@SuppressLint("FloatMath")
	private float distance(MotionEvent event) {
		// 两根线的距离
		float dx = event.getX(1) - event.getX(0);
		float dy = event.getY(1) - event.getY(0);
		return (float)Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * 计算两点之间中心点的距离
	 * 
	 * @param event
	 * @return
	 */
	private PointF mid(MotionEvent event) {
		float midx = event.getX(1) + event.getX(0);
		float midy = event.getY(1) - event.getY(0);

		return new PointF(midx / 2, midy / 2);
	}

	protected void CheckScale() {
		float p[] = new float[9];
		matrix.getValues(p);
		if (mode == ZOOM) {
			if (p[0] < minScale) {
				matrix.setScale(minScale, minScale);
			}
			if (p[0] > maxScale) {
				matrix.set(currentMaritx);
			}
		}
	}

	private void center(boolean horizontal, boolean vertical) {
		Matrix m = new Matrix();
		m.set(matrix);
		int dw = getDrawable().getBounds().width();
		int dh = getDrawable().getBounds().height();
		RectF rect = new RectF(0, 0, dw, dh);
		m.mapRect(rect);
		float height = rect.height();
		float width = rect.width();
		float deltaX = 0, deltaY = 0;
		if (vertical) {
			int screenHeight = dm.heightPixels; // 手机屏幕分辨率的高度
			if (height < screenHeight) {
				deltaY = (screenHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < screenHeight) {
				deltaY = getHeight() - rect.bottom;
			}
		}

		if (horizontal) {
			int screenWidth = dm.widthPixels; // 手机屏幕分辨率的宽度
			if (width < screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < screenWidth) {
				deltaX = screenWidth - rect.right;
			}
		}
		matrix.postTranslate(deltaX, deltaY);
	}
}
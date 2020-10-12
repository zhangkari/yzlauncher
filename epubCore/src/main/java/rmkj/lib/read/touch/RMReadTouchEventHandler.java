package rmkj.lib.read.touch;

import android.view.MotionEvent;

/**
 * onTouch 事件处理类
 * 
 * @author zsx
 * 
 */
public class RMReadTouchEventHandler {
	private float downX, downY;
	private boolean isLongClick = false;
	private int downIndex;
	private int clickOffset = 30;
	private int longTrueTime = 1000;
	private boolean isNeedChangeLongClick = true;
	private float lastMoveX;
	private float lastMoveY;
	private OnRMWebViewListener listener;

	public RMReadTouchEventHandler(OnRMWebViewListener listener) {
		this.listener = listener;
	}

	/**
	 * 主要方法
	 */
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		int x, y;
		// final int count = ev.getPointerCount();
		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_POINTER_DOWN:
			// LogUtil.e("", "ACTION_POINTER_DOWN");多点不管
			break;

		case MotionEvent.ACTION_POINTER_UP:
			// LogUtil.e("", "ACTION_POINTER_UP");多点不管
			break;

		case MotionEvent.ACTION_DOWN:
			isLongClick = false;
			isNeedChangeLongClick = true;
			downIndex = ev.getActionIndex();
			downX = ev.getX(downIndex);
			downY = ev.getY(downIndex);
			break;

		case MotionEvent.ACTION_MOVE:
			if (isLongClick) {
				if (Math.abs(ev.getX(downIndex) - lastMoveX) > 16 || Math.abs(ev.getY(downIndex) - lastMoveY) > 8) {
					onLongClickMoveing(ev.getX(downIndex), ev.getY(downIndex), ev);
					lastMoveX = (int) ev.getX(downIndex);
					lastMoveY = (int) ev.getY(downIndex);
				}
				return true;
			}
			/** 判断是否满足长按要求 */
			if (isNeedChangeLongClick) {
				x = (int) (ev.getX(downIndex) - downX);
				y = (int) (ev.getY(downIndex) - downY);
				if (Math.abs(x - y) > clickOffset) {
					isNeedChangeLongClick = false;
				}
			}
			/** 判断是否需要转成长按模式 */
			if (isNeedChangeLongClick) {
				if (ev.getEventTime() - ev.getDownTime() > longTrueTime) {
					isLongClick = true;
					onLongClick(ev.getX(downIndex), ev.getY(downIndex), ev);
					return true;
				}
			} else {
				onMove(ev);
			}
			/** 翻页 */
			// TODO 翻页效果
			break;

		case MotionEvent.ACTION_UP:
			if (!isLongClick) {
				x = (int) (ev.getX(downIndex) - downX);
				y = (int) (ev.getY(downIndex) - downY);
				if (Math.abs(x - y) < clickOffset) {
					onClick(ev.getX(downIndex), ev.getY(downIndex), ev);
					return true;
				}
				if (x >= 30) {
					onMoveLeft();
				} else if (x <= -30) {
					onMoveRight();
				}
			} else {
				onLongClickUp(ev.getX(downIndex), ev.getY(downIndex), downX, downY, ev);
			}
			isLongClick = false;
			isNeedChangeLongClick = true;
			break;

		case MotionEvent.ACTION_CANCEL:
			isLongClick = false;
			isNeedChangeLongClick = true;
			break;
		}
		return true;
	}

	private void onMove(MotionEvent ev) {
		if (listener != null) {
			listener.onMove(ev);
		}
	}

	private void onClick(float currentX, float currentY, MotionEvent evt) {
		if (listener != null) {
			listener.onClick(currentX, currentY, evt);
		}
	}

	private void onLongClick(float currentX, float currentY, MotionEvent evt) {
		if (listener != null) {
			listener.onLongClick(currentX, currentY, evt);
		}
	}

	private void onLongClickMoveing(float currentX, float currentY, MotionEvent evt) {
		if (listener != null) {
			listener.onLongClickMoveing(currentX, currentY, evt);
		}
	}

	public void onMoveRight() {
		if (listener != null) {
			listener.onMoveRight();
		}
	}

	public void onMoveLeft() {
		if (listener != null) {
			listener.onMoveLeft();
		}
	}

	public void onLongClickUp(float currentX, float currentY, float downX, float downY, MotionEvent evt) {
		if (listener != null) {
			listener.onLongClickUp(currentX, currentY, downX, downY, evt);
		}
	}

	public interface OnRMWebViewListener {
		void onClick(float currentX, float currentY, MotionEvent evt);

		void onMoveRight();

		void onMoveLeft();

		void onMove(MotionEvent ev);

		void onLongClickUp(float currentX, float currentY, float downX, float downY, MotionEvent evt);

		void onLongClick(float currentX, float currentY, MotionEvent evt);

		void onLongClickMoveing(float currentX, float currentY, MotionEvent evt);
	}
}

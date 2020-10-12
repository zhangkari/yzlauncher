package rmkj.lib.read.itf;

import android.graphics.Rect;

public interface OnRMEPUBSelectionListener 
{
	
	public void onEnterSelectionMode(int x, int y);
	public void onExitSelectionMode();
	
	public void onSelectionUpdate(Rect rc);
	public void onSelectionEnd();
}

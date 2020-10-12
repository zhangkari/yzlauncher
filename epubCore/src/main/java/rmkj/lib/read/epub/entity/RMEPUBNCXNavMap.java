package rmkj.lib.read.epub.entity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * NCX 文件 Nav标签
 * 
 * @author zsx
 * 
 */
public class RMEPUBNCXNavMap {
	private List<RMEPUBNCXNavPoint> list;

	public List<RMEPUBNCXNavPoint> getNavPoints() {
		return list;
	}

	public void setNavPoint(List<RMEPUBNCXNavPoint> navPoint) {
		Collections.sort(navPoint, new Comparator<RMEPUBNCXNavPoint>() {

			@Override
			public int compare(RMEPUBNCXNavPoint o1, RMEPUBNCXNavPoint o2) {
				Integer i1 = Integer.parseInt(o1.getPlayOrder());
				Integer i2 = Integer.parseInt(o2.getPlayOrder());
				return i1 > i2 ? 1 : -1;
			}
		});
		list = navPoint;
	}

	/**
	 * @param spinePath
	 *            相对路径
	 */
	public String getSpineName(String spinePath) {
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				RMEPUBNCXNavPoint p = list.get(i);
				if (p.getSrc().contains(spinePath)) {
					return p.getText();
				}
			}
		}
		return null;
	}
}

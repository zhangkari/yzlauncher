package rmkj.lib.read.itf;

public interface IRMEPUBPageInterface {
	boolean hasPrevPage();

	void showPrevPage();

	boolean hasNextPage();

	void showNextPage();

	boolean hasPage(int pageIndex);
}

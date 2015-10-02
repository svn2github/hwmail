package com.hs.mail.webmail.util;

public class Pager {

	// instance attributes
	private int pageNumber; // start page is 0
	private int pageSize;
	private int itemCount;
	private boolean ascending;

	/**
	 * Item count is not yet determined.
	 */
	public Pager(int page, int size, boolean asc) {
		this.pageSize = size;
		this.itemCount = -1;
		this.pageNumber = page;
		this.ascending = asc;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
		// recalculate page number
		this.pageNumber = Math.min(this.pageNumber,
				Math.max(0, getPageCount() - 1));
	}

	public int getPageCount() {
		return (int) Math.ceil((double) itemCount / (double) pageSize);
	}

	public boolean isAscending() {
		return ascending;
	}

	public int getBegin() {
		return (ascending) ? pageNumber * pageSize : Math.max(0, itemCount
				- (pageNumber + 1) * pageSize);
	}

	public int getEnd() {
		return (ascending) ? Math.min(itemCount - 1, (pageNumber + 1)
				* pageSize - 1) : itemCount - pageNumber * pageSize - 1;
	}
	
}

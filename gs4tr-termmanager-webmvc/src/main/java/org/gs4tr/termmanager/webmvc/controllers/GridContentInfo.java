package org.gs4tr.termmanager.webmvc.controllers;

import org.gs4tr.foundation.modules.entities.model.PagedListInfo;

public class GridContentInfo {
    private Boolean _hasNext;

    private Boolean _hasPrevious;

    private PagedListInfo _pagedListInfo;

    private Integer[] _pageIndexes;

    private Long _totalCount;

    private Long _totalCountPerSource;

    private Long _totalCountPerTarget;

    private Integer _totalPageCount;

    public Boolean getHasNext() {
	return _hasNext;
    }

    public Boolean getHasPrevious() {
	return _hasPrevious;
    }

    public PagedListInfo getPagedListInfo() {
	return _pagedListInfo;
    }

    public Integer[] getPageIndexes() {
	return _pageIndexes;
    }

    public Long getTotalCount() {
	return _totalCount;
    }

    public Long getTotalCountPerSource() {
	return _totalCountPerSource;
    }

    public Long getTotalCountPerTarget() {
	return _totalCountPerTarget;
    }

    public Integer getTotalPageCount() {
	return _totalPageCount;
    }

    public void setTotalCountPerSource(Long totalCountPerSource) {
	_totalCountPerSource = totalCountPerSource;
    }

    public void setTotalCountPerTarget(Long totalCountPerTarget) {
	_totalCountPerTarget = totalCountPerTarget;
    }

    protected void setHasNext(Boolean hasNext) {
	_hasNext = hasNext;
    }

    protected void setHasPrevious(Boolean hasPrevious) {
	_hasPrevious = hasPrevious;
    }

    protected void setPagedListInfo(PagedListInfo pagedListInfo) {
	_pagedListInfo = pagedListInfo;
    }

    protected void setPageIndexes(Integer[] pageIndexes) {
	_pageIndexes = pageIndexes;
    }

    protected void setTotalCount(Long totalCount) {
	_totalCount = totalCount;
    }

    protected void setTotalPageCount(Integer totalPageCount) {
	_totalPageCount = totalPageCount;
    }
}

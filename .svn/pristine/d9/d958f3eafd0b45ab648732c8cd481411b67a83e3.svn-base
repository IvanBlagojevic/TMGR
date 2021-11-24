package org.gs4tr.termmanager.model.dto.pagedlist;

import org.gs4tr.termmanager.model.dto.Term;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("termPagedList")
public class TermPagedList {

    @XStreamAlias("term")
    private Term[] _elements;

    @XStreamAlias("pagedListInfo")
    private PagedListInfo _pagedListInfo;

    @XStreamAlias("totalCount")
    private Long _totalCount = (long) -1;

    public Term[] getElements() {
	return _elements;
    }

    public PagedListInfo getPagedListInfo() {
	return _pagedListInfo;
    }

    public Long getTotalCount() {
	return _totalCount;
    }

    public void setElements(Term[] elements) {
	_elements = elements;
    }

    public void setPagedListInfo(PagedListInfo pagedListInfo) {
	_pagedListInfo = pagedListInfo;
    }

    public void setTotalCount(Long totalCount) {
	_totalCount = totalCount;
    }

}

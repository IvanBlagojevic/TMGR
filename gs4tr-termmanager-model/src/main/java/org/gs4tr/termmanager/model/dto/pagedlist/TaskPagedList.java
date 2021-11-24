package org.gs4tr.termmanager.model.dto.pagedlist;

import org.gs4tr.termmanager.model.dto.Task;
import org.gs4tr.termmanager.model.dto.TermSearchModel;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("taskPagedList")
public class TaskPagedList {
    @XStreamAlias("elements")
    private TermSearchModel[] _elements;
    @XStreamAlias("pageIndexes")
    private Integer[] _pageIndexes;
    @XStreamAlias("pagedListInfo")
    private PagedListInfo _pagedListInfo;
    @XStreamAlias("tasks")
    private Task[] _tasks;

    @XStreamAlias("totalCount")
    private Long _totalCount = (long) -1;

    public TermSearchModel[] getElements() {
	return _elements;
    }

    public Integer[] getPageIndexes() {
	return _pageIndexes;
    }

    public PagedListInfo getPagedListInfo() {
	return _pagedListInfo;
    }

    public Task[] getTasks() {
	return _tasks;
    }

    public Long getTotalCount() {
	return _totalCount;
    }

    public void setElements(TermSearchModel[] elements) {
	_elements = elements;
    }

    public void setPageIndexes(Integer[] pageIndexes) {
	_pageIndexes = pageIndexes;
    }

    public void setPagedListInfo(PagedListInfo pagedListInfo) {
	_pagedListInfo = pagedListInfo;
    }

    public void setTasks(Task[] tasks) {
	_tasks = tasks;
    }

    public void setTotalCount(Long totalCount) {
	_totalCount = totalCount;
    }

}

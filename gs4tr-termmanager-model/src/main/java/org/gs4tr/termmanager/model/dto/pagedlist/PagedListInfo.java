package org.gs4tr.termmanager.model.dto.pagedlist;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("pagedListInfo")
public class PagedListInfo {

    public static final int DEFAULT_INDEX = 0;

    public static final int DEFAULT_INDEXES_SIZE = 5;

    public static final int DEFAULT_SIZE = 10;

    public static final String DEFAULT_SORT_DIRECTION = "ASCENDING";

    @XStreamAlias("index")
    private Integer _index = DEFAULT_INDEX;

    @XStreamAlias("indexesSize")
    private Integer _indexesSize = DEFAULT_INDEXES_SIZE;

    @XStreamAlias("size")
    private Integer _size = DEFAULT_SIZE;

    @XStreamAlias("sortDirection")
    private String _sortDirection = DEFAULT_SORT_DIRECTION;

    @XStreamAlias("sortProperty")
    private String _sortProperty;

    public Integer getIndex() {
	return _index;
    }

    public Integer getIndexesSize() {
	return _indexesSize;
    }

    public Integer getSize() {
	return _size;
    }

    public String getSortDirection() {
	return _sortDirection;
    }

    public String getSortProperty() {
	return _sortProperty;
    }

    public void setIndex(Integer index) {
	_index = index;
    }

    public void setIndexesSize(Integer indexesSize) {
	_indexesSize = indexesSize;
    }

    public void setSize(Integer size) {
	_size = size;
    }

    public void setSortDirection(String sortDirection) {
	_sortDirection = sortDirection;
    }

    public void setSortProperty(String sortProperty) {
	_sortProperty = sortProperty;
    }

}

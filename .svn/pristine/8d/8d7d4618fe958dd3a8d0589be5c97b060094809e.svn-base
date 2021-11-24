package org.gs4tr.termmanager.webservice.model.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "PageInfo")
public class WsPageInfo {

    private int _maxNumFound = 200;

    private int _pageIndex = 0;

    private String _sortDirection = "ASC";

    private String _sortProperty;

    @ApiModelProperty(value = "Maximum number of found items in search result. Represents number of term entries per searched page.")
    public int getMaxNumFound() {
	return _maxNumFound;
    }

    @ApiModelProperty(value = "This property represents page number. Default value is 0 which is first page.")
    public int getPageIndex() {
	return _pageIndex;
    }

    @ApiModelProperty(value = "Sort direction. Available options are \"ASC\" for ascending sorting and \"DESC\" for descending sorting.Default value is \"ASC\".")
    public String getSortDirection() {
	return _sortDirection;
    }

    @ApiModelProperty(value = "Property or column on which search results will be sorted. Default sorting is by source term. Additional options are \"creationDate\" and \"modificationDate\".")
    public String getSortProperty() {
	return _sortProperty;
    }

    public void setMaxNumFound(int maxNumFound) {
	_maxNumFound = maxNumFound;
    }

    public void setPageIndex(int pageIndex) {
	_pageIndex = pageIndex;
    }

    public void setSortDirection(String sortDirection) {
	_sortDirection = sortDirection;
    }

    public void setSortProperty(String sortProperty) {
	_sortProperty = sortProperty;
    }
}

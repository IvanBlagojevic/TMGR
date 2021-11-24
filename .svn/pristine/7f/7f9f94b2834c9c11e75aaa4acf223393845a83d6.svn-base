package org.gs4tr.termmanager.webservice.model.response;

import java.util.Collection;

import io.swagger.annotations.ApiModelProperty;

public class SearchTermEntryResponse extends BaseResponse {

    private int _itemsPerPage;
    private int _pageIndex;
    private int _totalResults;
    Collection<TermEntryHit> _termEntryHits;

    public SearchTermEntryResponse(int returnCode, boolean success) {
	setReturnCode(returnCode);
	setSuccess(success);
    }

    public SearchTermEntryResponse() {
    }

    public SearchTermEntryResponse(int returnCode, boolean success, int itemsPerPage, int pageIndex, int totalResults,
	    Collection<TermEntryHit> termEntryHits) {
	setReturnCode(returnCode);
	setSuccess(success);
	_itemsPerPage = itemsPerPage;
	_pageIndex = pageIndex;
	_totalResults = totalResults;
	_termEntryHits = termEntryHits;
    }

    @ApiModelProperty(value = "Number of term entries per page.")
    public int getItemsPerPage() {
	return _itemsPerPage;
    }

    @ApiModelProperty(value = "Page number. Default value is 0 which is first page.")
    public int getPageIndex() {
	return _pageIndex;
    }

    @ApiModelProperty(value = "Collection of term entry matches.")
    public Collection<TermEntryHit> getTermEntryHits() {
	return _termEntryHits;
    }

    @ApiModelProperty(value = "Total number of found term entries.")
    public int getTotalResults() {
	return _totalResults;
    }

    public void setItemsPerPage(int itemsPerPage) {
	_itemsPerPage = itemsPerPage;
    }

    public void setPageIndex(int pageIndex) {
	_pageIndex = pageIndex;
    }

    public void setTermEntryHits(Collection<TermEntryHit> termEntryHits) {
	_termEntryHits = termEntryHits;
    }

    public void setTotalResults(int totalResults) {
	_totalResults = totalResults;
    }
}

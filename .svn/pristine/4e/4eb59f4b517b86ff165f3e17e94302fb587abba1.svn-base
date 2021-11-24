package org.gs4tr.termmanager.webservice.model.request;

import java.util.List;
import java.util.Set;

import org.gs4tr.termmanager.model.dto.Description;
import org.gs4tr.tm3.api.ConcordanceMatchLocation;

import io.swagger.annotations.ApiModelProperty;

public class ConcordanceSearchCommand extends BaseCommand {

    private boolean _caseSensitive;

    private WsDateFilter _creationDateFilter;

    private String _creationUser;

    private Set<Description> _descriptions;

    private boolean _includeAttributesSearch = false;

    private ConcordanceMatchLocation _matchLocation = ConcordanceMatchLocation.SOURCE;

    private boolean _matchWholeWords;

    private WsDateFilter _modificationDateFilter;

    private String _modificationUser;

    private WsPageInfo _pageInfo;

    private String _projectTicket;

    private boolean _searchForbidden = false;

    private String _sourceLocale;

    private String _status;

    private List<String> _targetLocales;

    private String _term;

    @ApiModelProperty(value = "Creation date filter.")
    public WsDateFilter getCreationDateFilter() {
	return _creationDateFilter;
    }

    @ApiModelProperty(value = "Creation user filter.")
    public String getCreationUser() {
	return _creationUser;
    }

    @ApiModelProperty(value = "Description filter.")
    public Set<Description> getDescriptions() {
	return _descriptions;
    }

    @ApiModelProperty(value = "Represents an option to declare if the search will match in source, target or both.", allowableValues = "source")
    public ConcordanceMatchLocation getMatchLocation() {
	return _matchLocation;
    }

    @ApiModelProperty(value = "Modification date filter.")
    public WsDateFilter getModificationDateFilter() {
	return _modificationDateFilter;
    }

    @ApiModelProperty(value = "Modification user filter.")
    public String getModificationUser() {
	return _modificationUser;
    }

    @ApiModelProperty(value = "Information about search results page, like page index, number of term entries per page and sorting.")
    public WsPageInfo getPageInfo() {
	return _pageInfo;
    }

    @ApiModelProperty(value = "Project unique identifier.", required = true)
    public String getProjectTicket() {
	return _projectTicket;
    }

    @ApiModelProperty(value = "Source language code.", required = true)
    public String getSourceLocale() {
	return _sourceLocale;
    }

    @ApiModelProperty(value = "Term status.")
    public String getStatus() {
	return _status;
    }

    @ApiModelProperty(value = "Collection of target language codes.")
    public List<String> getTargetLocales() {
	return _targetLocales;
    }

    @ApiModelProperty(value = "The text that will be searched.")
    public String getTerm() {
	return _term;
    }

    @ApiModelProperty(value = "Enables case sensitive search.", example = "false")
    public boolean isCaseSensitive() {
	return _caseSensitive;
    }

    @ApiModelProperty(value = "Includes attribute matching in search filter. If false, search filter will look matches only in terms text. This parameter is optional.", example = "false")
    public boolean isIncludeAttributesSearch() {
	return _includeAttributesSearch;
    }

    @ApiModelProperty(value = "Enables exact match search.", example = "false")
    public boolean isMatchWholeWords() {
	return _matchWholeWords;
    }

    @ApiModelProperty(value = "Applies search for blacklisted terms.", example = "false")
    public boolean isSearchForbidden() {
	return _searchForbidden;
    }

    public void setCaseSensitive(boolean caseSensitive) {
	_caseSensitive = caseSensitive;
    }

    public void setCreationDateFilter(WsDateFilter creationDateFilter) {
	_creationDateFilter = creationDateFilter;
    }

    public void setCreationUser(String creationUser) {
	_creationUser = creationUser;
    }

    public void setDescriptions(Set<Description> descriptions) {
	_descriptions = descriptions;
    }

    public void setIncludeAttributesSearch(boolean includeAttributesSearch) {
	_includeAttributesSearch = includeAttributesSearch;
    }

    public void setMatchLocation(ConcordanceMatchLocation matchLocation) {
	_matchLocation = matchLocation;
    }

    public void setMatchWholeWords(boolean matchWholeWords) {
	_matchWholeWords = matchWholeWords;
    }

    public void setModificationDateFilter(WsDateFilter modificationDateFilter) {
	_modificationDateFilter = modificationDateFilter;
    }

    public void setModificationUser(String modificationUser) {
	_modificationUser = modificationUser;
    }

    public void setPageInfo(WsPageInfo pageInfo) {
	_pageInfo = pageInfo;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setSearchForbidden(boolean searchForbidden) {
	_searchForbidden = searchForbidden;
    }

    public void setSourceLocale(String sourceLocale) {
	_sourceLocale = sourceLocale;
    }

    public void setStatus(String status) {
	_status = status;
    }

    public void setTargetLocales(List<String> targetLocales) {
	_targetLocales = targetLocales;
    }

    public void setTerm(String term) {
	_term = term;
    }
}

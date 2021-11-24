package org.gs4tr.termmanager.model.search.command;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.gs4tr.termmanager.model.search.ItemSearchRequest;
import org.gs4tr.termmanager.model.search.TypeSearchEnum;

public class TranslationSearchRequest extends ItemSearchRequest {

    private List<String> _assingees;

    private Date _dateCompletedFrom;

    private Date _dateCompletedTo;

    private Date _dateModifiedFrom;

    private Date _dateModifiedTo;

    private Date _dateSubmittedFrom;

    private Date _dateSubmittedTo;

    private Long _projectId;

    private List<Long> _projectIds;

    private String _searchType;

    private String _source;

    private Long _submissionId;

    private String _submissionName;

    private List<Long> _submissionTermIds;

    private List<String> _submitters;

    private String _target;

    private String _term;

    private Set<Long> _termEntryIds;

    private List<Long> _termIds;

    private TypeSearchEnum _typeSearch = TypeSearchEnum.TERM;

    public List<String> getAssingees() {
	return _assingees;
    }

    public Date getDateCompletedFrom() {
	return _dateCompletedFrom;
    }

    public Date getDateCompletedTo() {
	return _dateCompletedTo;
    }

    public Date getDateModifiedFrom() {
	return _dateModifiedFrom;
    }

    public Date getDateModifiedTo() {
	return _dateModifiedTo;
    }

    public Date getDateSubmittedFrom() {
	return _dateSubmittedFrom;
    }

    public Date getDateSubmittedTo() {
	return _dateSubmittedTo;
    }

    public Long getProjectId() {
	return _projectId;
    }

    public List<Long> getProjectIds() {
	return _projectIds;
    }

    public String getSearchType() {
	return _searchType;
    }

    public String getSource() {
	return _source;
    }

    public Long getSubmissionId() {
	return _submissionId;
    }

    public String getSubmissionName() {
	return _submissionName;
    }

    public List<Long> getSubmissionTermIds() {
	return _submissionTermIds;
    }

    public List<String> getSubmitters() {
	return _submitters;
    }

    public String getTarget() {
	return _target;
    }

    public String getTerm() {
	return _term;
    }

    public Set<Long> getTermEntryIds() {
	return _termEntryIds;
    }

    public List<Long> getTermIds() {
	return _termIds;
    }

    public TypeSearchEnum getTypeSearch() {
	return _typeSearch;
    }

    public void setAssingees(List<String> assingees) {
	_assingees = assingees;
    }

    public void setDateCompletedFrom(Date dateCompletedFrom) {
	_dateCompletedFrom = dateCompletedFrom;
    }

    public void setDateCompletedTo(Date dateCompletedTo) {
	_dateCompletedTo = dateCompletedTo;
    }

    public void setDateModifiedFrom(Date dateModifiedFrom) {
	_dateModifiedFrom = dateModifiedFrom;
    }

    public void setDateModifiedTo(Date dateModifiedTo) {
	_dateModifiedTo = dateModifiedTo;
    }

    public void setDateSubmittedFrom(Date dateSubmittedFrom) {
	_dateSubmittedFrom = dateSubmittedFrom;
    }

    public void setDateSubmittedTo(Date dateSubmittedTo) {
	_dateSubmittedTo = dateSubmittedTo;
    }

    public void setProjectId(Long projectId) {
	_projectId = projectId;
    }

    public void setProjectIds(List<Long> projectIds) {
	_projectIds = projectIds;
    }

    public void setSearchType(String searchType) {
	_searchType = searchType;
    }

    public void setSource(String source) {
	_source = source;
    }

    public void setSubmissionId(Long submissionId) {
	_submissionId = submissionId;
    }

    public void setSubmissionName(String submissionName) {
	_submissionName = submissionName;
    }

    public void setSubmissionTermIds(List<Long> submissionTermIds) {
	_submissionTermIds = submissionTermIds;
    }

    public void setSubmitters(List<String> submitters) {
	_submitters = submitters;
    }

    public void setTarget(String target) {
	_target = target;
    }

    public void setTerm(String term) {
	_term = term;
    }

    public void setTermEntryIds(Set<Long> termEntryIds) {
	_termEntryIds = termEntryIds;
    }

    public void setTermIds(List<Long> termIds) {
	_termIds = termIds;
    }

    public void setTypeSearch(TypeSearchEnum typeSearch) {
	_typeSearch = typeSearch;
    }
}
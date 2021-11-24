package org.gs4tr.termmanager.model.dto;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("termSearchModel")
public class TermSearchModel {

    @XStreamOmitField
    private Notification[] _alerts;

    @XStreamOmitField
    private Long _availableTasks;

    @XStreamOmitField
    private Comment[] _comments;

    @XStreamAlias("finalTerm")
    private Boolean _finalTerm;

    @XStreamAlias("forbidden")
    private Boolean _forbidden;

    @XStreamAlias("projectName")
    private String _projectName;

    @XStreamAlias("projectTicket")
    private String _projectTicket;

    @XStreamAlias("sourceTermLanguage")
    private String _sourceTermLanguage;

    @XStreamOmitField
    private String _sourceTermLanguageAlignment;

    @XStreamOmitField
    private String _sourceTermName;

    @XStreamOmitField
    private String _sourceTermTicket;

    @XStreamOmitField
    private String _targetTermLanguageAlignment;

    @XStreamAlias("termEntryTicket")
    private String _termEntryTicket;

    @XStreamAlias("termLanguage")
    private String _termLanguage;

    @XStreamAlias("termName")
    private String _termName;

    @XStreamAlias("termTicket")
    private String _ticket;

    @XStreamOmitField
    private Boolean _viewHistory;

    public Notification[] getAlerts() {
	return _alerts;
    }

    public Long getAvailableTasks() {
	return _availableTasks;
    }

    public Comment[] getComments() {
	return _comments;
    }

    public Boolean getFinalTerm() {
	return _finalTerm;
    }

    public Boolean getForbidden() {
	return _forbidden;
    }

    public String getProjectName() {
	return _projectName;
    }

    public String getProjectTicket() {
	return _projectTicket;
    }

    public String getSourceTermLanguage() {
	return _sourceTermLanguage;
    }

    public String getSourceTermLanguageAlignment() {
	return _sourceTermLanguageAlignment;
    }

    public String getSourceTermName() {
	return _sourceTermName;
    }

    public String getSourceTermTicket() {
	return _sourceTermTicket;
    }

    public String getTargetTermLanguageAlignment() {
	return _targetTermLanguageAlignment;
    }

    public String getTermEntryTicket() {
	return _termEntryTicket;
    }

    public String getTermLanguage() {
	return _termLanguage;
    }

    public String getTermName() {
	return _termName;
    }

    public String getTicket() {
	return _ticket;
    }

    public Boolean getViewHistory() {
	return _viewHistory;
    }

    public void setAlerts(Notification[] alerts) {
	_alerts = alerts;
    }

    public void setAvailableTasks(Long availableTasks) {
	_availableTasks = availableTasks;
    }

    public void setComments(Comment[] comments) {
	_comments = comments;
    }

    public void setFinalTerm(Boolean finalTerm) {
	_finalTerm = finalTerm;
    }

    public void setForbidden(Boolean forbidden) {
	_forbidden = forbidden;
    }

    public void setProjectName(String projectName) {
	_projectName = projectName;
    }

    public void setProjectTicket(String projectTicket) {
	_projectTicket = projectTicket;
    }

    public void setSourceTermLanguage(String sourceTermLanguage) {
	_sourceTermLanguage = sourceTermLanguage;
    }

    public void setSourceTermLanguageAlignment(String sourceTermLanguageAlignment) {
	_sourceTermLanguageAlignment = sourceTermLanguageAlignment;
    }

    public void setSourceTermName(String sourceTermName) {
	_sourceTermName = sourceTermName;
    }

    public void setSourceTermTicket(String sourceTermTicket) {
	_sourceTermTicket = sourceTermTicket;
    }

    public void setTargetTermLanguageAlignment(String targetTermLanguageAlignment) {
	_targetTermLanguageAlignment = targetTermLanguageAlignment;
    }

    public void setTermEntryTicket(String termEntryTicket) {
	_termEntryTicket = termEntryTicket;
    }

    public void setTermLanguage(String termLanguage) {
	_termLanguage = termLanguage;
    }

    public void setTermName(String termName) {
	_termName = termName;
    }

    public void setTicket(String ticket) {
	_ticket = ticket;
    }

    public void setViewHistory(Boolean viewHistory) {
	_viewHistory = viewHistory;
    }

}

package org.gs4tr.termmanager.service.xls.report.builder.terminology;

import java.util.List;

import org.gs4tr.termmanager.model.ItemStatusTypeHolder;
import org.gs4tr.termmanager.model.glossary.Term;
import org.gs4tr.termmanager.model.xls.report.ReportColor;
import org.gs4tr.termmanager.service.xls.report.builder.ReportDescriptionConverter;

public class ReportTerm {

    private ReportColor _color;

    private boolean _first;

    private String _languageId;

    private List<ReportDescription> _newDescriptions;

    private String _newName;

    private String _newStatus;

    private List<ReportDescription> _oldDescriptions;

    private String _oldName;

    private String _oldStatus;

    private String _uuId;

    public ReportTerm(Term term) {
	_uuId = term.getUuId();
	_languageId = term.getLanguageId();
	_first = term.isFirst();
	_oldName = term.getName();
	_oldStatus = ItemStatusTypeHolder.getStatusDisplayName(term.getStatus());
	_oldDescriptions = ReportDescriptionConverter.convertTo(term.getDescriptions());
	_newName = term.getName();
	_newStatus = _oldStatus;
	_newDescriptions = ReportDescriptionConverter.convertTo(term.getDescriptions());
	_color = ReportColor.BLACK;
    }

    public ReportTerm(Term term, ReportColor color) {
	this(term);
	_color = color;
    }

    public ReportTerm(Term oldTerm, Term newTerm) {
	this(oldTerm);
	_uuId = newTerm.getUuId();
	_newName = newTerm.getName();
	_newStatus = ItemStatusTypeHolder.getStatusDisplayName(newTerm.getStatus());
	_newDescriptions = ReportDescriptionConverter.convertTo(newTerm.getDescriptions());
    }

    public ReportTerm(Term oldTerm, Term newTerm, ReportColor color) {
	this(oldTerm, newTerm);
	_color = color;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ReportTerm other = (ReportTerm) obj;
	if (_uuId == null) {
	    if (other._uuId != null)
		return false;
	} else if (!_uuId.equals(other._uuId))
	    return false;
	return true;
    }

    public ReportColor getColor() {
	return _color;
    }

    public String getLanguageId() {
	return _languageId;
    }

    public List<ReportDescription> getNewDescriptions() {
	return _newDescriptions;
    }

    public String getNewName() {
	return _newName;
    }

    public String getNewStatus() {
	return _newStatus;
    }

    public List<ReportDescription> getOldDescriptions() {
	return _oldDescriptions;
    }

    public String getOldName() {
	return _oldName;
    }

    public String getOldStatus() {
	return _oldStatus;
    }

    public String getUuId() {
	return _uuId;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((_uuId == null) ? 0 : _uuId.hashCode());
	return result;
    }

    public boolean isFirst() {
	return _first;
    }

    @Override
    public String toString() {
	return "ReportTerm [_uuId=" + _uuId + ", _languageId=" + _languageId + ", _first=" + _first + ", _oldName="
		+ _oldName + ", _oldStatus=" + _oldStatus + ", _oldDescriptions=" + _oldDescriptions + ", _newName="
		+ _newName + ", _newStatus=" + _newStatus + ", _newDescriptions=" + _newDescriptions + "]";
    }
}

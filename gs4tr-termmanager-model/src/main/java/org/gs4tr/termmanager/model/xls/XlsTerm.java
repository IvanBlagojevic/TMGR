package org.gs4tr.termmanager.model.xls;

import java.util.List;

public class XlsTerm {

    private List<XlsDescription> _descriptions;

    private String _localeCode;

    private String _term;

    private String _xlsStatus;

    public XlsTerm() {
    }

    public XlsTerm(String term, List<XlsDescription> descriptions, String localeCode) {
	_term = term;
	_descriptions = descriptions;
	_localeCode = localeCode;
    }

    public XlsTerm(String term, List<XlsDescription> descriptions, String localeCode, String xlsStatus) {
	this(term, descriptions, localeCode);
	_xlsStatus = xlsStatus;
    }

    public List<XlsDescription> getDescriptions() {
	return _descriptions;
    }

    public String getLocaleCode() {
	return _localeCode;
    }

    public String getTerm() {
	return _term;
    }

    public String getXlsStatus() {
	return _xlsStatus;
    }

    public void setDescriptions(List<XlsDescription> descriptions) {
	_descriptions = descriptions;
    }

    public void setLocaleCode(String localeCode) {
	_localeCode = localeCode;
    }

    public void setTerm(String term) {
	_term = term;
    }

    public void setXlsStatus(String xlsStatus) {
	_xlsStatus = xlsStatus;
    }
}

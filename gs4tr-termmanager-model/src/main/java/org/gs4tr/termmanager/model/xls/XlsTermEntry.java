package org.gs4tr.termmanager.model.xls;

import java.util.List;

public class XlsTermEntry {

    private List<XlsDescription> _termEntryDescriptions;

    private List<XlsTerm> _terms;

    private String _xlsUuId;

    public XlsTermEntry() {

    }

    public XlsTermEntry(List<XlsTerm> terms, List<XlsDescription> termEntryDescriptions) {
	_terms = terms;
	_termEntryDescriptions = termEntryDescriptions;
    }

    public XlsTermEntry(List<XlsTerm> terms, List<XlsDescription> termEntryDescriptions, String xlsUuId) {
	this(terms, termEntryDescriptions);
	_xlsUuId = xlsUuId;
    }

    public List<XlsDescription> getTermEntryDescriptions() {
	return _termEntryDescriptions;
    }

    public List<XlsTerm> getTerms() {
	return _terms;
    }

    public String getXlsUuId() {
	return _xlsUuId;
    }

    public void setTermEntryDescriptions(List<XlsDescription> termEntryDescriptions) {
	_termEntryDescriptions = termEntryDescriptions;
    }

    public void setTerms(List<XlsTerm> terms) {
	_terms = terms;
    }

    public void setXlsUuId(String xlsUuId) {
	_xlsUuId = xlsUuId;
    }
}

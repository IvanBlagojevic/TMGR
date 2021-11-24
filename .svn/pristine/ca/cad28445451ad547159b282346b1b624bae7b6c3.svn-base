package org.gs4tr.termmanager.model.xls.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class XlsReportSheet implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 3284141155642798001L;

    private List<String> _header;

    private AtomicInteger _rowIndex;

    private List<XlsReportRow> _rows;

    private String _sourceLanguageId;

    private String _targetLanguageId;

    public XlsReportSheet(String sourceLanguageId, String targetLanguageId) {
	this();
	_rowIndex = new AtomicInteger(ImportReportConstants.HEADER_ROW_INDEX + 1);
	_sourceLanguageId = sourceLanguageId;
	_targetLanguageId = targetLanguageId;
    }

    public XlsReportSheet(String sourceLanguageId, String targetLanguageId, int rowIndex) {
	this();
	_rowIndex = new AtomicInteger(rowIndex);
	_sourceLanguageId = sourceLanguageId;
	_targetLanguageId = targetLanguageId;
    }

    private XlsReportSheet() {
	_rows = new ArrayList<>();
	_header = new ArrayList<>();
    }

    public void addRow(XlsReportRow row) {
	_rows.add(row);
    }

    public void addToHeader(String value) {
	getHeader().add(value);
    }

    public List<String> getHeader() {
	return _header;
    }

    public AtomicInteger getRowIndex() {
	return _rowIndex;
    }

    public List<XlsReportRow> getRows() {
	return _rows;
    }

    public String getSourceLanguageId() {
	return _sourceLanguageId;
    }

    public String getTargetLanguageId() {
	return _targetLanguageId;
    }

    public void incrementRowIndex() {
	_rowIndex.incrementAndGet();
    }

    @Override
    public String toString() {
	return "XlsReportSheet [_header=" + _header + ", _sourceLanguageId=" + _sourceLanguageId
		+ ", _targetLanguageId=" + _targetLanguageId + ", _rowIndex=" + _rowIndex + ", _rows=" + _rows + "]";
    }
}
